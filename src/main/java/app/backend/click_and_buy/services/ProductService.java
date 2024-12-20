package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.repositories.CategoryRepository;
import app.backend.click_and_buy.repositories.ProductRepository;
import app.backend.click_and_buy.repositories.UserRatingRepository;
import app.backend.click_and_buy.responses.ColorSizeQuantityCombination;
import app.backend.click_and_buy.responses.ProductReview;
import app.backend.click_and_buy.statics.ObjectValidator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UserRatingRepository userRatingRepository;


    public Product findProductById(long id) {
        return productRepository.findByProductId(id);
    }

    public List<Product> findProductByIdIn(Set<Long> ids) {
        return productRepository.findByProductIdIn(ids);
    }


//    public List<Product> findByCategory(List<Category> categories) {
//        return productRepository.findByCategoryIsIn(categories);
//    }

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

//    public List<Product> getProductsFromCategory(List<Category> categories, int limit) {
//        return categories.stream()
//                .flatMap(category -> getLimitedProductsByCategory(category, limit).stream())
//                .distinct()
//                .collect(Collectors.toList());
//    }




}
