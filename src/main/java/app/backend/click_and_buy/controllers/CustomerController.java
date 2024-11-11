package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.massages.Warning;
import app.backend.click_and_buy.massages.Subject;
import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.enums.Numbers;
import app.backend.click_and_buy.request.ModifyItemQuantity;
import app.backend.click_and_buy.request.ProductRating;
import app.backend.click_and_buy.request.ProductToCart;
import app.backend.click_and_buy.responses.ColorSizeQuantityCombination;
import app.backend.click_and_buy.responses.ProductCart;
import app.backend.click_and_buy.responses.ProductDetails;
import app.backend.click_and_buy.services.*;
import app.backend.click_and_buy.statics.Message;
import io.jsonwebtoken.io.IOException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static app.backend.click_and_buy.enums.UserActionType.*;
import static app.backend.click_and_buy.statics.Builder.buildProductCart;
import static app.backend.click_and_buy.statics.Builder.buildProductResponseList;

@RestController()
@RequestMapping("/customer")
@Validated
public class CustomerController {


    private final MessageSource messageSource;
    private final CommonService commonService;
    private final UserService userService;
    private final ProductService productService;
    private final ProductVariationService productVariationService;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final UserBehaviorService userBehaviorService;
    private final RatingService ratingService;

    public CustomerController(MessageSource messageSource, CommonService commonService, UserService userService, ProductService productService, ProductVariationService productVariationService, CartService cartService, CartItemService cartItemService, ProductImageService productImageService, CategoryService categoryService, UserBehaviorService userBehaviorService, RatingService ratingService) {
        this.messageSource = messageSource;
        this.commonService = commonService;
        this.userService = userService;
        this.productService = productService;
        this.productVariationService = productVariationService;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.productImageService = productImageService;
        this.categoryService = categoryService;
        this.userBehaviorService = userBehaviorService;
        this.ratingService = ratingService;
    }

    //TEST
    @PostMapping("/test")
    public long test() {
        return commonService.getUserIdFromToken();
    }

    @PostMapping("cart/add-item-to-cart")
    public ResponseEntity<?> addItemToCart(@RequestBody @Valid ProductToCart productToCart) {
        Product product = productService.findProductById(productToCart.getProductId());
        if(product == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        }
        ProductVariation productVariation = productVariationService.findProductVariationsByAProductId
                (product.getProductId(), productToCart.getColor(), productToCart.getSize(), productToCart.getQuantity());
        User user = userService.findById(commonService.getUserIdFromToken(), false);
        ResponseEntity<?> responseEntity;
        if (productVariation != null) {
            try {
                Cart cart = cartService.findCartByCustomerId(user.getCustomer().getCustomerId());
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProductVariation(productVariation);
                cartItem.setQuantity(productToCart.getQuantity());
                cartItemService.saveCartItem(cartItem);
                responseEntity = ResponseEntity.ok().body(messageSource.getMessage(Success.PRODUCT_HAS_BEEN_ADDED_TO_CART,null, Locale.getDefault()));
            } catch (Exception ignored) {
                responseEntity = ResponseEntity.internalServerError().body(messageSource.getMessage(Error.ADDING_PRODUCT_TO_CART_FAILED,null, Locale.getDefault()));
            }
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println(product.getProductId());
                userBehaviorService.save(user,ADD_TO_CART, product.getProductId());
            }
        } else {
            responseEntity = ResponseEntity.badRequest().body(messageSource.getMessage(Warning.PRODUCT_NOT_AVAILABLE,null, Locale.getDefault()));
        }
        return responseEntity;
    }

    @GetMapping("cart/get-products-from-cart")
    public ResponseEntity<?> showProductsFromCart() {
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        Cart cart=cartService.findCartByCustomerId(user.getCustomer().getCustomerId());
        List<CartItem> cartItems= new ArrayList<>(cartItemService.findCartItems(cart));
        List<ProductCart> productCarts=new ArrayList<>();
        for(CartItem cartItem:cartItems){
            ProductCart productCart = buildProductCart(cartItem,
                    (productImageService.getOneProductImageByProduct(cartItem.getProductVariation().getProduct()).getImage())
            );
            productCarts.add(productCart);
        }
        return ResponseEntity.ok().body(productCarts);
    }

    @GetMapping("/cart/get-cart-length")
    public ResponseEntity<?> getCartLength() {
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        return ResponseEntity.ok().body(cartItemService.countCartItemByCart(user.getCustomer().getCart()));
    }



    // TEST
    @PostMapping("pro-pic")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file,@RequestParam long pid) {
        Product product=productService.findProductById(pid);
        try {
                ProductImage productImage=new ProductImage();
                productImage.setProduct(product);
                productImage.setImage(file.getBytes());
                productImageService.save(productImage);
                return ResponseEntity.ok().body(messageSource.getMessage(Success.PICTURE_UPLOADED,null, Locale.getDefault()));

        } catch (IOException | java.io.IOException e) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.PICTURE_UPLOAD_FAILED,null, Locale.getDefault()));
        }
    }

    @DeleteMapping("cart/delete-item-from-cart")
    public ResponseEntity<?> deleteItemFromCart(@RequestParam @NotNull(message = "Item id must have a value") @Min(message = "Item id must be greater than or equal to 1",value = 1) Long itemId) {
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        Cart cart=cartService.findCartByCustomerId(user.getCustomer().getCustomerId());
        List<CartItem> cartItems=cartItemService.findCartItems(cart);
        CartItem targetCartItem=cartItemService.findCartItemById(itemId);
        if(targetCartItem==null || !cartItems.contains(targetCartItem)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.ITEM_NOT_EXISTS_IN_CART,null, Locale.getDefault()));
        }else{
            cartItemService.deleteCartItem(targetCartItem);
            return ResponseEntity.ok().body(messageSource.getMessage(Success.ITEM_DELETED_FROM_CART,null, Locale.getDefault()));
        }

    }

    @PutMapping("cart/update-item-quantity")
    public ResponseEntity<?> updateCartItemQuantity(@RequestBody @Valid @NotNull ModifyItemQuantity modifyItemQuantity) {
        User user = userService.findById(commonService.getUserIdFromToken(), false);
        Cart cart = cartService.findCartByCustomerId(user.getCustomer().getCustomerId());
        List<CartItem> cartItems = cartItemService.findCartItems(cart);
        CartItem cartItem = cartItemService.findCartItemById(modifyItemQuantity.getItemId());
        if (cartItem == null || !cartItems.contains(cartItem)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.ITEM_NOT_EXISTS_IN_CART,null, Locale.getDefault()));
        } else {
            ProductVariation productVariation = productVariationService.findProductVariationsById(cartItem.getProductVariation().getProductVariationId());
            if (productVariation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
            } else if (productVariation.getQuantity().longValue() >= modifyItemQuantity.getNewQuantity().longValue()) {
                try {
                    cartItem.setQuantity(modifyItemQuantity.getNewQuantity());
                    cartItemService.updateCartItem(cartItem);
                    return ResponseEntity.ok().body(messageSource.getMessage(Success.ITEM_QUANTITY_UPDATED,null, Locale.getDefault()));
                } catch (Exception ignored) {
                    return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.UPDATING_ITEM_IN_CART_FAILED,null, Locale.getDefault()));
                }
            } else {
                return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.ITEM_QUANTITY_NOT_EXISTS,null, Locale.getDefault()));
            }
        }
    }
        @GetMapping("products/product-details")
        public ResponseEntity<?> showProductDetails(@RequestParam @NotNull(message = "Product id must have a value") @Min(message = "Product id must be greater than or equal to 1",value = 1) Long productId) {
            Product product = productService.findProductById(productId);
            if(product == null){
                return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
            }
            ArrayList<ProductVariation> productVariations=productVariationService.findAllProductVariationsByProduct(product);
            ArrayList<ProductImage> productImages=productImageService.getProductImagesByProduct(product);
            ArrayList<ColorSizeQuantityCombination> colorSizeQuantityCombinations =productService.generateColorSizeCombinations(productVariations);
            try{
                userBehaviorService.save(
                        userService.findById(commonService.getUserIdFromToken(), false),
                        VIEW, product.getProductId()
                );
            }catch(Exception ignored){}
            return new ResponseEntity<>(ProductDetails.builder()
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .productDescription(product.getDescription())
                    .productPrice(product.getPrice())
                    .productOldPrice(product.getOldPrice())
                    .productImages(productImageService.getImagesFromProductImages(productImages))
                    .colorSizeQuantityCombinations(colorSizeQuantityCombinations)
                    .productCategory(categoryService.getCategoryHierarchyString(product))
                    .build(), HttpStatus.OK);

        }

        @PostMapping("products/by-category")
        public ResponseEntity<?> showProductsByCategory(@RequestParam @NotNull @NotEmpty String categoryName,@RequestParam @NotNull String origin) {
            List<Product> products =productService.findProductsByCategoryTree(categoryName,origin);
            try{
                userBehaviorService.save(
                        userService.findById(commonService.getUserIdFromToken(), false),
                        SEARCH, categoryName
                );
            }catch(Exception ignored){}
            return new ResponseEntity<>(buildProductResponseList(products,productImageService), HttpStatus.OK);
        }


//    @PostMapping("track-user-products-review")
//    public ResponseEntity<?> trackUserBehaviorProductsReview(@RequestParam @Valid @NotNull String action, @RequestParam @Valid @NotNull List<Object> details) {
//        User user = userService.findById(commonService.getUserIdFromToken(), false);
//        UserActionType actionType = UserActionType.fromString(action);
//        if (!actionType.getDetailType().isInstance(details)) {
//            return ResponseEntity.badRequest().body("Details type does not match expected type for action: " + actionType);
//        }
//        userBehaviorService.save(user,actionType,details);
//        return ResponseEntity.ok().body("Successfully tracked user behavior");
//    }



    @GetMapping("products/all-for-customer")
        public ResponseEntity<?> showAllForCustomer() {
            User user = userService.findById(commonService.getUserIdFromToken(), false);
            List<UserBehavior> userBehaviorList=userBehaviorService.getUserBehaviorsByUser(user.getUserId());
//            Class<?>[] actionTypes = {
//                    UserActionType.VIEW.getDetailType(),
//                    UserActionType.SEARCH.getDetailType(),
//                    UserActionType.ADD_TO_CART.getDetailType()
//            };
//
//            for (Class<?> actionType : actionTypes) {
//                userBehaviorList.add(userBehaviorService.getUserBehaviorByUserAndAction(user.getUserId(), actionType));
//            }
            List<?> DetailsList=userBehaviorService.getDetailsFromUserBehaviorList(userBehaviorList,Numbers.NUMBER_OF_PRODUCTS_COULD_EXPORTED_FROM_DB_FOR_A_USER_BEHAVIOR.getIntValue());
            List<Product> productList=userBehaviorService.extractDetailsByTypeFromDetailsList(DetailsList,Product.class);
            List<Category> categoryList=productService.getCategoriesFromProductList(productList);
            categoryList.addAll(userBehaviorService.extractDetailsByTypeFromDetailsList(DetailsList,Category.class));
            List<Product> products=new ArrayList<>();
            for(Category category : categoryList){
                products.addAll(productService.getLimitedProductsByCategory(category, Numbers.PARTIAL_PRODUCT_LIST_SIZE.getIntValue()/categoryList.size()));
            }
            return ResponseEntity.ok().body(buildProductResponseList(products,productImageService).stream().distinct().toList());
        }


        @PostMapping("products/rate-and-comment")
        public ResponseEntity<?> setRatingForProduct(@RequestBody @Valid ProductRating productRating) {
            User user = userService.findById(commonService.getUserIdFromToken(), false);
            Product product = productService.findProductById(productRating.getProductId());
            if(product == null || user == null){
                return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.FEEDBACK_CANNOT_BE_ADDED,null, Locale.getDefault()));
            }else {
                Rating rating = product.getRating();
                if (rating == null) {
                    rating = ratingService.save(Rating.builder().product(product).build());
                }
                boolean test = ratingService.addUserRating(rating, user, productRating.getRatingValue(),productRating.getComment());
                if (test) return ResponseEntity.ok().body(messageSource.getMessage(Success.FEEDBACK_ADDED,null, Locale.getDefault()));
                else return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.FEEDBACK_FAILED,null, Locale.getDefault()));
            }
        }



}
