package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.dto.CategoryProjection;
import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.groups.CreateDiscount;
import app.backend.click_and_buy.groups.UpdateDiscount;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.request.CreateProduct;
import app.backend.click_and_buy.massages.Warning;
import app.backend.click_and_buy.request.DiscountRequest;
import app.backend.click_and_buy.request.UpdateProduct;
import app.backend.click_and_buy.responses.CategoryDetails;
import app.backend.click_and_buy.responses.GetProduct;
import app.backend.click_and_buy.responses.ProductRating;
import app.backend.click_and_buy.services.*;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/api/customer-service")
@Validated
@Valid
public class CustomerServiceController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final DiscountService discountService;
    private final MessageSource messageSource;
    private final UserRatingService userRatingService;
    private final UserService userService;

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
    public ResponseEntity<?> addProduct(@RequestPart("createProduct") CreateProduct createProduct,
                                        @RequestPart("files") List<MultipartFile> files) {
        try {
            productService.saveNewProduct(createProduct,files);
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
        Optional<GetProduct> product = productService.getProduct(productId);
        return product.isPresent() ? ResponseEntity.ok().body(product)
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
    public ResponseEntity<?> updateProduct(@RequestPart("updateProduct") UpdateProduct updateProduct,
                                           @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        try {
            System.out.println(updateProduct);
            return productService.updateProduct(updateProduct,files)
                   ? ResponseEntity.ok().body(messageSource.getMessage(Success.UPDATE_PRODUCT,null, Locale.getDefault()))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.UPDATE_PRODUCT,null, Locale.getDefault()));
        }
    }

    @GetMapping("discounts")
    public ResponseEntity<?> getDiscounts(@RequestParam(name = "isEnded") boolean isEnded,@RequestParam(name = "all") boolean all ){
        return all ? ResponseEntity.ok().body(discountService.getAllDiscounts())
            : ResponseEntity.ok().body(discountService.getAllDiscountsByEndDate(isEnded));
    }

    @PostMapping("discounts")
    public ResponseEntity<?> addDiscount(@RequestBody @Validated(CreateDiscount.class) DiscountRequest discountRequest){
        try{
            Map<String,Object> response = new HashMap<>();
            response.put("message",messageSource.getMessage(Success.ADD_DISCOUNT,null, Locale.getDefault()));
            response.put("resource",discountService.saveDiscount(discountRequest));
            return ResponseEntity.ok().body(response);
        }catch (Exception ignored){
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.ADD_DISCOUNT,null, Locale.getDefault()));
        }
    }

    @PutMapping("discounts")
    public ResponseEntity<?> updateDiscount(@RequestBody @Validated(UpdateDiscount.class) DiscountRequest discountRequest){
        try{
            discountService.updateDiscount(discountRequest);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.UPDATE_DISCOUNT,null, Locale.getDefault()));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.DISCOUNT_NOT_EXISTS,null, Locale.getDefault()));
        }
    }

    @DeleteMapping("discounts")
    public ResponseEntity<?> deleteDiscount(@RequestParam @Min(1) long discountId) {
        try {
            discountService.deleteDiscount(discountId);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.DELETE_DISCOUNT,null, Locale.getDefault()));
        }catch (Exception ignored) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.DELETE_DISCOUNT,null, Locale.getDefault()));
        }
    }

    @GetMapping("comments/{productId}")
    public ResponseEntity<?> getProductComments(@PathVariable long productId, @RequestParam(required = false) Boolean isApproved){
        try{
            List<ProductRating> productComments = userRatingService.getProductComments(productId,isApproved);
            return ResponseEntity.ok().body(productComments);
        }catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        }
    }

    @DeleteMapping("comments/{userRatingId}")
    public ResponseEntity<?> deleteUserRating(@PathVariable long userRatingId){
        userRatingService.deleteUserRating(userRatingId);
        return ResponseEntity.ok().body("The comment has been deleted successfully");
    }

    @PutMapping("comments/{userRatingId}/approve")
    public ResponseEntity<?> approveOnUserRating(@PathVariable long userRatingId){
        try{
            userRatingService.approveOnUserRating(userRatingId);
            return ResponseEntity.ok().body("The comment has been approved successfully");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage("User Rating does not exists",null, Locale.getDefault()));
        }
    }

    @PutMapping("customers/{userId}/report")
    public ResponseEntity<?> reportACustomer(@PathVariable long userId){
        try{
            userService.reportOrUnReport(userId,true);
            return ResponseEntity.ok().body("The customer has been reported successfully");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage("User does not exists",null, Locale.getDefault()));
        }
    }

    @PutMapping("customers/{userId}/unReport")
    public ResponseEntity<?> unReportACustomer(@PathVariable long userId){
        try{
            userService.reportOrUnReport(userId,false);
            return ResponseEntity.ok().body("The customer has been unreported successfully");
        }catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(messageSource.getMessage("User does not exists",null, Locale.getDefault()));
        }
    }



}
