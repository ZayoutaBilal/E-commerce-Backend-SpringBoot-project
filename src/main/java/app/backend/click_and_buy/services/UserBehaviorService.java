package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.enums.UserActionType;
import app.backend.click_and_buy.repositories.UserBehaviorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserBehaviorService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final CategoryService categoryService;
    private final ProductService productService;

    public UserBehaviorService(UserBehaviorRepository userBehaviorRepository, CategoryService categoryService, ProductService productService) {
        this.userBehaviorRepository = userBehaviorRepository;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    public void save(User user, UserActionType actionType, Object details) {
        if (!actionType.getDetailType().isInstance(details)) {
            System.out.println("Invalid details!");
            throw new IllegalArgumentException("Invalid details type for action: " + actionType);
        }
        UserBehavior userBehavior = userBehaviorRepository.findByUser_UserIdAndActionType(user.getUserId(), actionType);
        System.out.println(userBehavior);
        if (userBehavior == null) {
            userBehavior = UserBehavior.builder()
                    .user(user)
                    .actionType(actionType)
                    .build();
            userBehavior=userBehaviorRepository.save(userBehavior);
        }
        System.out.println(userBehavior);

        switch (actionType) {
            case VIEW, ADD_TO_CART -> {
                Product product = productService.findProductById((long) details);
                saveProductTimestamps(userBehavior,product);
                System.out.println(true);
            }
            case SEARCH -> {
                Set<Category> categoryHashSet= new HashSet<>(categoryService.getCategoryByName((String) details));
                saveCategoryTimestamps(userBehavior,categoryHashSet);
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

    public void saveCategoryTimestamps(UserBehavior userBehavior,Set<Category> categorySet) {
        for (Category category : categorySet) {
            CategoryTimestamp ct=userBehaviorRepository.findCategoryTimestampByUserBehaviorIdAndCategoryId(userBehavior.getUserBehaviorId(),
                    category.getCategoryId());
            if (ct == null) {
                userBehaviorRepository.saveCategoryTimestampsBatch(userBehavior.getUserBehaviorId(), category.getCategoryId(),
                        LocalDateTime.now(), 1
                );
            }else{
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
                case SEARCH -> list.addAll(getRecentCategoriesByUserBehavior(userBehavior.getUserBehaviorId(),1));
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










}

