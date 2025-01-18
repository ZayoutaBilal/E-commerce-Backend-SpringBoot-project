package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.dto.CategoryProjection;
import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.request.ProductInsertion;
import app.backend.click_and_buy.responses.CategoryDetails;
import app.backend.click_and_buy.services.CategoryService;
import app.backend.click_and_buy.services.DiscountService;
import app.backend.click_and_buy.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("categories/get")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(
                categories.stream().map(CategoryDetails::new).collect(Collectors.toList())
        );
    }

    @PostMapping("categories/add")
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

    @DeleteMapping("categories/delete")
    public ResponseEntity<?> deleteCategory(@RequestParam @Min(1) long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.DELETE_CATEGORY,null, Locale.getDefault()));
        }catch (Exception ignored) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.DELETE_CATEGORY,null, Locale.getDefault()));
        }
    }

    @PutMapping("categories/update")
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

    @PostMapping("products/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductInsertion pI) {
        try {
            long id = productService.save(pI);
            return ResponseEntity.ok().body("Product with id "+id+" added successfully");
        } catch (Exception ignored) {
            return ResponseEntity.internalServerError().body("Error while adding product");
        }
    }

}
