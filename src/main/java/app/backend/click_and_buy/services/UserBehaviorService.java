package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.enums.Numbers;
import app.backend.click_and_buy.enums.UserActionType;
import app.backend.click_and_buy.repositories.UserBehaviorRepository;
import app.backend.click_and_buy.responses.ProductOverview;
import app.backend.click_and_buy.statics.Builder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserBehaviorService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductImageService productImageService;


    public void save(User user, UserActionType actionType, Object details) {
        if (!actionType.getDetailType().isInstance(details)) {
            System.out.println("Invalid details!");
            throw new IllegalArgumentException("Invalid details type for action: " + actionType);
        }

        UserBehavior userBehavior = userBehaviorRepository.findByUser_UserIdAndActionType(user.getUserId(), actionType);
        System.out.println("We in"+LocalDateTime.now());
        if (userBehavior == null) {
            userBehavior = UserBehavior.builder()
                    .user(user)
                    .actionType(actionType)
                    .build();
            userBehavior=userBehaviorRepository.save(userBehavior);
            System.out.println("We in 3"+userBehavior);

        }
        System.out.println(userBehavior);

        switch (actionType) {
            case VIEW, ADD_TO_CART -> {
                Product product = productService.findProductById((long) details);
                saveProductTimestamps(userBehavior,product);
                System.out.println(true);
            }
            case SEARCH -> {
                System.out.println("details => "+details);
                saveCategoryTimestamps(userBehavior,(String) details);
                System.out.println(true);
            }
            default -> throw new IllegalArgumentException("Invalid details type for action: " + actionType);
        }

    }


    public void saveProductTimestamps(UserBehavior userBehavior,Product product) {
            ProductTimestamp pt=userBehaviorRepository.findProductTimestampByUserBehaviorIdAndProductId(userBehavior.getUserBehaviorId(),
                    product.getProductId() );
            if (pt == null) {
                userBehaviorRepository.saveProductTimestampsBatch(userBehavior.getUserBehaviorId(), product.getProductId(),
                        LocalDateTime.now(), 1
                );
            }else {
                userBehaviorRepository.updateProductTimestampsBatch(userBehavior.getUserBehaviorId(), product.getProductId(),
                        LocalDateTime.now(), pt.getTimes()+1
                );
            }
    }


    public void saveCategoryTimestamps(UserBehavior userBehavior,String details) {
        List<Category> categoryHashSet= categoryService.getCategoryByName(details);
        for (Category category : categoryHashSet) {
            System.out.println("category => "+category.toString());
            CategoryTimestamp ct=userBehaviorRepository.findCategoryTimestampByUserBehaviorIdAndCategoryId(userBehavior.getUserBehaviorId(),
                    category.getCategoryId());

            if (ct == null) {
                try {
                    userBehaviorRepository.saveCategoryTimestampsBatch(userBehavior.getUserBehaviorId(), category.getCategoryId(),
                            LocalDateTime.now(), 1
                    );
                    System.out.println("saved");
                }catch (Exception e){
                    System.out.println(" not saved");
                    System.out.println("Error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }else{
                System.out.println("CategoryTimestamp => "+ ct);
                userBehaviorRepository.updateCategoryTimestampsBatch(userBehavior.getUserBehaviorId(), category.getCategoryId(),
                        LocalDateTime.now(), ct.getTimes()+1);
            }
        }
    }


    public List<Product> getRecentProductsByUserBehavior(long userBehaviorId, int limit) {
        Pageable pageable = PageRequest.of(0, 5);
        return userBehaviorRepository.findProductByUserBehaviorIdOrderByAddedAtDesc(userBehaviorId,pageable);
    }

    public List<Category> getRecentCategoriesByUserBehavior(long userBehaviorId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userBehaviorRepository.findCategoriesByUserBehaviorIdOrderByAddedAtDesc(userBehaviorId, pageable);
    }



    public List<UserBehavior> getUserBehaviorsByUser(long userId) {
        return userBehaviorRepository.findByUser_UserId(userId);
    }

//    public List<?> getDetailsFromUserBehaviorList(List<UserBehavior> userBehaviors) {
//        return userBehaviors.stream()
//                .flatMap(userBehavior -> userBehavior.getDetails().stream())
//                .distinct()
//                .collect(Collectors.toList());
//    }

    public List<?> getDetailsFromUserBehaviorList(List<UserBehavior> userBehaviors, int limit) {
        List<Object> list = new ArrayList<>();
        for (UserBehavior userBehavior : userBehaviors) {
            switch (userBehavior.getActionType()) {
                case VIEW,ADD_TO_CART -> list.addAll(getRecentProductsByUserBehavior(userBehavior.getUserBehaviorId(),limit));
                case SEARCH -> list.addAll(getRecentCategoriesByUserBehavior(userBehavior.getUserBehaviorId(),10));
                default -> throw new IllegalArgumentException("Invalid details type for action: " + userBehavior.getActionType());
            }
        }
        return list;
    }

    public <T> List<T> extractDetailsByTypeFromDetailsList(List<?> details, Class<T> type) {
        return details.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .distinct()
                .collect(Collectors.toList());
    }



    public Page<ProductOverview> getAllRecommendationsForNewUser(int page) {

        Pageable pageableP = PageRequest.of(
                page, Numbers.NUMBER_OF_PRODUCTS_COULD_EXPORTED_FROM_DB_FOR_A_USER_BEHAVIOR.getIntValue(),
                Sort.by(Sort.Direction.valueOf("DESC"), "count")
        );

        Pageable pageableC = PageRequest.of(
                page, Numbers.NUMBER_OF_CATEGORIES_COULD_EXPORTED_FROM_DB_FOR_A_USER_BEHAVIOR.getIntValue(),
                Sort.by(Sort.Direction.valueOf("DESC"), "count")
        );

        Page<Product> viewedProducts = userBehaviorRepository.findTopProductsByAction(UserActionType.VIEW, pageableP);
        List<Product> combinedProducts = new ArrayList<>(viewedProducts.getContent());

        Page<Product> addedToCartProducts = userBehaviorRepository.findTopProductsByAction(UserActionType.ADD_TO_CART, pageableP);
        combinedProducts.addAll(addedToCartProducts.getContent());

        List<Category> categoryList = productService.getCategoriesFromProductList(combinedProducts);
        categoryList.addAll(userBehaviorRepository.findTopCategories(pageableC).getContent());

        Page<Product> allProducts = productService.getLimitedProductsByCategoryIn(categoryList, page, Numbers.PARTIAL_PRODUCT_LIST_SIZE.getIntValue());

//        int start = (int) pageable.getOffset();
//        int end = Math.min(start + pageable.getPageSize(), combinedProducts.size());
//        List<Product> paginatedCombinedProducts = combinedProducts.subList(start, end);
//        Page<Product> combinedProductsPage = new PageImpl<>(paginatedCombinedProducts, pageable, combinedProducts.size());

        return Builder.buildProductResponseList(allProducts, productImageService);
    }









}

