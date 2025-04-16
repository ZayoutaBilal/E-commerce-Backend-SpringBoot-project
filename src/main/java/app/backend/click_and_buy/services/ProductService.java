package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.repositories.ProductRepository;
import app.backend.click_and_buy.repositories.UserBehaviorRepository;
import app.backend.click_and_buy.repositories.UserRatingRepository;
import app.backend.click_and_buy.request.CreateProduct;
import app.backend.click_and_buy.request.UpdateProduct;
import app.backend.click_and_buy.responses.ColorSizeQuantityCombination;
import app.backend.click_and_buy.responses.GetProduct;
import app.backend.click_and_buy.responses.ProductOverviewManagement;
import app.backend.click_and_buy.responses.ProductReview;
import app.backend.click_and_buy.statics.ObjectValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.ModelMapper;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UserRatingRepository userRatingRepository;
    private final ProductVariationService productVariationService;
    private final DiscountService discountService;
    private final RatingService ratingService;
    private final ProductImageService productImageService;
    private final UserBehaviorRepository userBehaviorRepository;
    private final ModelMapper modelMapper;

    public Product findProductById(long id) {
        return productRepository.findByProductId(id);
    }

    public List<Product> findProductByIdIn(Set<Long> ids) {
        return productRepository.findByProductIdIn(ids);
    }

    public void deleteProductById(long id) {
        userBehaviorRepository.deleteProductTimestampBy(id);
        productRepository.deleteById(id);
    }


    public Page<Product> getLimitedProductsByCategoryIn(List<Category> category, int page ,int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("productId").ascending());
        return productRepository.findByCategoryIsIn(category, pageRequest);
    }

    public Page<Product> findProductsByCategoryTree(String categoryName,String origin,int page ,int size) {
        Page<Product> products;
        if (ObjectValidator.stringValidator(origin)) {
            List<Category> originCategories = categoryService.getCategoriesTree(Collections.singletonList(categoryService.getOriginCategory(origin, 0)));
            List<Category> categories = categoryService.getCategoriesTree(categoryService.getCategoryByName(categoryName));
            List<Category> intersection = new ArrayList<>(originCategories);
            intersection.retainAll(categories);
            products = getLimitedProductsByCategoryIn(intersection,page,size);
        } else {
            List<Category> categories = categoryService.getCategoriesTree(categoryService.getCategoryByName(categoryName));
            products = getLimitedProductsByCategoryIn(categories,page,size);
        }
        return products;

    }


    public ArrayList<ColorSizeQuantityCombination> generateColorSizeCombinations(List<ProductVariation> productVariations) {
        List<ColorSizeQuantityCombination> colorSizeQuantityCombinations = new ArrayList<>();
        Set<String> processedSizes = new HashSet<>();
        ColorSizeQuantityCombination colorSizeQuantityCombination =null;
        for (ProductVariation pV : productVariations) {
            if (!processedSizes.contains(pV.getSize())) {
                colorSizeQuantityCombination = new ColorSizeQuantityCombination();
                colorSizeQuantityCombination.setSize(pV.getSize());
                HashMap<String,Integer> colorsAndQuantities = new HashMap<>();
                for (ProductVariation pV2 : productVariations) {
                    if (Objects.equals(pV2.getSize(), pV.getSize())) {
                        colorsAndQuantities.put(pV2.getColor(), pV2.getQuantity());
                    }
                }
                colorSizeQuantityCombination.setColorQuantityMap(colorsAndQuantities);
                colorSizeQuantityCombinations.add(colorSizeQuantityCombination);
                processedSizes.add(pV.getSize());
            }
        }
        return new ArrayList<>(colorSizeQuantityCombinations);
    }

    public List<ProductReview> getTopReviewsForProduct(Product product) {
        Rating rating = product.getRating();
        if(rating == null) return null;
        List<ProductReview> productReviews = new ArrayList<>();
        List<UserRating> userRatings = userRatingRepository.findTop5ByRatingOrderByUpdatedAtDesc(rating);
        for (UserRating ur : userRatings) {
            productReviews.add(ProductReview.builder()
                            .stars(ur.getStars())
                            .comment(ur.getComment())
                            .creationDate(Objects.requireNonNullElse(ur.getCreatedAt(),ur.getUpdatedAt()))
                            .username(ur.getUser().getUsername())
                            .image(ur.getUser().getCustomer().getPicture())
                    .build());
        }
        return productReviews;
    }

    public List<Category> getCategoriesFromProductList(List<Product> products) {
        return products.stream()
                .map(Product::getCategory)
                .distinct() // To remove duplicate products, if any
                .collect(Collectors.toList());
    }

    public int countByCategory(Category category){
        return productRepository.countByCategory(category);
    }

    public Page<Product> getRecentProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findByOrderByCreatedAtDesc(pageable);
    }

    public Page<Product> getMostLikedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("rating.averageStars")));
        return productRepository.findByRating_AverageStarsGreaterThanEqual(3.5, pageable);
    }

    @Transactional
    public void saveNewProduct(CreateProduct createProduct,List<MultipartFile> files) {
        final Product product = Product.builder()
                .name(createProduct.getName())
                .description(createProduct.getDescription())
                .price(createProduct.getPrice())
                .information(createProduct.getInformation())
                .category(categoryService.getCategoryById(createProduct.getCategory()))
                .build();

        if(createProduct.getDiscount() != 0)
            product.setDiscount(discountService.getDiscountById(createProduct.getDiscount()));

        Rating rating = Rating.builder().build();
        rating.setProduct(product);

        productRepository.save(product);
        ratingService.save(rating);
        productVariationService.addVariationsToProduct(createProduct.getVariations(),product);
        productImageService.addImagesToProduct(files,product);
    }

    public Page<ProductOverviewManagement> getProductOverviewManagement(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(product -> {
            ProductOverviewManagement overview = modelMapper.map(product, ProductOverviewManagement.class);
                overview.setCategoryName(product.getCategory().getName());
                if (!Objects.isNull(product.getDiscount()))
                    overview.setDiscountPercent(product.getDiscount().getPercent());
                    else overview.setDiscountPercent(0f);
                if (!product.getProductImages().isEmpty())
                        overview.setImage(product.getProductImages().get(0).getImage());
            if (!Objects.isNull(product.getRating())) overview.setCountOfComments(userRatingRepository.countUserRatingsByRating_RatingIdAndIsApproved(product.getRating().getRatingId(),false));
            return overview;
        });
    }

    public Optional<GetProduct> getProduct(long productId){
        Product product = findProductById(productId);
        if(Objects.isNull(product))
            return Optional.empty();
        GetProduct getProduct = modelMapper.map(product, GetProduct.class);
        getProduct.setDiscount(
                Objects.isNull(product.getDiscount()) ? 0 : product.getDiscount().getDiscountId()
        );
        getProduct.setCategory(product.getCategory().getCategoryId());
        if (!product.getProductImages().isEmpty())
            getProduct.setImages(
                    product.getProductImages().stream().map(productImage -> GetProduct.Image.builder().id(productImage.getProductImageId())
                            .url(productImage.getImage())
                            .build()).toList()
            );
        getProduct.setVariations(
                product.getProductVariations().stream().map(pv -> modelMapper.map(pv, GetProduct.Variation.class)).toList());
        return Optional.of(getProduct);
    }


    @Transactional
    public boolean updateProduct(UpdateProduct updateProduct, List<MultipartFile> images){
        Product product = findProductById(updateProduct.getProductId());
        if(!Objects.isNull(product)){
            product.setCategory(categoryService.getCategoryById(updateProduct.getCategory()));
            product.setDiscount(discountService.getDiscountById(updateProduct.getDiscount()));
            product.setName(updateProduct.getName());
            product.setInformation(updateProduct.getInformation());
            product.setDescription(updateProduct.getDescription());
            product.setPrice(updateProduct.getPrice());
            if(!Objects.isNull(images) && !images.isEmpty()){
                productImageService.addImagesToProduct(images,product);
            }
            productImageService.deleteAllById(updateProduct.getDeletedImages());
            productVariationService.deleteAllByVariationId(updateProduct.getDeletedVariations());
            productVariationService.addVariationsToProduct(updateProduct.getVariations(),product);
            productVariationService.updateProductVariationQuantity(updateProduct.getUpdatedVariations());
            productRepository.save(product);
            return true;
        }
        return false;
    }







}
