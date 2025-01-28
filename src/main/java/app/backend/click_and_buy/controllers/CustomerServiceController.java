package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.dto.CategoryProjection;
import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.dto.ProductManagement;
import app.backend.click_and_buy.massages.Warning;
import app.backend.click_and_buy.responses.CategoryDetails;
import app.backend.click_and_buy.services.CategoryService;
import app.backend.click_and_buy.services.DiscountService;
import app.backend.click_and_buy.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController()
@RequestMapping("/customer-service")
@Validated
@Valid
public class CustomerServiceController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final DiscountService discountService;
    private final MessageSource messageSource;

    @GetMapping("categories")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(
                categories.stream().map(CategoryDetails::new).collect(Collectors.toList())
        );
    }

    @PostMapping("categories")
    public ResponseEntity<?> addCategory(@ModelAttribute CategoryDetails category) {
        try {
            category.setCategoryId(categoryService.addCategory(category));
            Map<String,Object> response = new HashMap<>();
            response.put("message",messageSource.getMessage(Success.ADD_CATEGORY,null, Locale.getDefault()));
            response.put("resource",category.getCategoryId());
            return ResponseEntity.ok().body(response);
        } catch (IOException ignored) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.ADD_CATEGORY,null, Locale.getDefault()));
        }
    }

    @DeleteMapping("categories")
    public ResponseEntity<?> deleteCategory(@RequestParam @Min(1) long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.DELETE_CATEGORY,null, Locale.getDefault()));
        }catch (Exception ignored) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.DELETE_CATEGORY,null, Locale.getDefault()));
        }
    }

    @PutMapping("categories")
    public ResponseEntity<?> updateCategory(@ModelAttribute CategoryDetails category){
        categoryService.updateCategory(category);
        return ResponseEntity.ok().body(messageSource.getMessage(Success.UPDATE_CATEGORY,null, Locale.getDefault()));
    }

    @GetMapping("categories-discounts")
    public ResponseEntity<?> getAllCategoriesAndAllDiscounts() {
        List<CategoryProjection> categoryProjections = categoryService.getAllCategories_Projection();
        List<DiscountProjection> discountProjections = discountService.getAllDiscounts_Projection();
        Map<String,Object> response = new HashMap<>();
        response.put("categories",categoryProjections);
        response.put("discounts",discountProjections);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("products")
    public ResponseEntity<?> addProduct(@RequestParam("product") String product,@RequestParam("images") List<MultipartFile> images) {
        try {
            ProductManagement productManagement = new ObjectMapper().readValue(product, ProductManagement.class);
            productService.saveNewProduct(productManagement,images);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.ADD_PRODUCT,null, Locale.getDefault()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.ADD_PRODUCT,null, Locale.getDefault()));
        }
    }

    @GetMapping("products")
    public ResponseEntity<?> getProducts(@RequestParam(name = "size", defaultValue = "10") int size ,
                                         @RequestParam(name = "page", defaultValue = "0") int page ,
                                         @RequestParam(name = "sortedBy", defaultValue = "productId") String sortedBy,
                                         @RequestParam(name = "order", defaultValue = "asc") String order
    )  {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortedBy));
        return ResponseEntity.ok().body(
                productService.getProductOverviewManagement(pageable)
        );

    }

    @GetMapping("products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable @Min(1) long productId){
        Optional<ProductManagement> productManagement = productService.getProduct(productId);
        return productManagement.isPresent() ? ResponseEntity.ok().body(productManagement)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
    }

    @DeleteMapping("products")
    public ResponseEntity<?> deleteProduct(@RequestParam @Min(1) long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.DELETE_PRODUCT,null, Locale.getDefault()));
        }catch (Exception ignored) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.DELETE_PRODUCT,null, Locale.getDefault()));
        }
    }

    @PutMapping("products")
    public ResponseEntity<?> updateProduct(@RequestParam("productId") long productId,@RequestParam("product") String product,
                                @RequestParam("images") List<MultipartFile> images,@RequestParam("deletedImageIds") String deletedImageIds) {
        try {
            ProductManagement productManagement = new ObjectMapper().readValue(product, ProductManagement.class);
            List<?> deletedImageIdsList = new ObjectMapper().readValue(deletedImageIds, List.class);
            return productService.updateProduct(productId,productManagement,images,deletedImageIdsList)
                   ? ResponseEntity.ok().body(messageSource.getMessage(Success.UPDATE_PRODUCT,null, Locale.getDefault()))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.UPDATE_PRODUCT,null, Locale.getDefault()));
        }
    }


}
