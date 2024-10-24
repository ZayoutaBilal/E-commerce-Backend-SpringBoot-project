package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductVariation;
import app.backend.click_and_buy.repositories.CategoryRepository;
import app.backend.click_and_buy.repositories.ProductRepository;
import app.backend.click_and_buy.responses.ColorSizeQuantityCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;


    public Product findProductById(long id) {
        return productRepository.findByProductId(id);
    }

    public List<Product> findProductByIdIn(Set<Long> ids) {
        return productRepository.findByProductIdIn(ids);
    }


    public List<Product> findByCategory(List<Category> categories) {
        return productRepository.findByCategoryIsIn(categories);
    }

    public List<Product> getLimitedProductsByCategory(Category category, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Page<Product> productPage = productRepository.findByCategory(category, pageRequest);
        return new ArrayList<>(productPage.getContent());
    }

    public List<Product> findProductsByCategoryTree(String categoryName,String origin) {

        if (origin != null && !origin.isEmpty() && !origin.isBlank() && !origin.equals("null")) {
            List<Category> originCategories = categoryService.getCategoriesTree(Collections.singletonList(categoryService.getOriginCategory(origin, 0)));
            List<Category> categories = categoryService.getCategoriesTree(categoryService.getCategoryByName(categoryName));
            List<Category> intersection = new ArrayList<>(originCategories);
            intersection.retainAll(categories);
            List<Product> products = findByCategory(intersection);
            return new ArrayList<>(products);
        } else {
            List<Category> categories = categoryService.getCategoriesTree(categoryService.getCategoryByName(categoryName));
            List<Product> products = findByCategory(categories);
            return new ArrayList<>(products);
        }

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

    public List<Category> getCategoriesFromProductList(List<Product> products) {
        return products.stream()
                .map(Product::getCategory)
                .distinct() // To remove duplicate products, if any
                .collect(Collectors.toList());
    }

    public List<Product> getProductsFromCategory(List<Category> categories, int limit) {
        return categories.stream()
                .flatMap(category -> getLimitedProductsByCategory(category, limit).stream())
                .distinct()
                .collect(Collectors.toList());
    }




}
