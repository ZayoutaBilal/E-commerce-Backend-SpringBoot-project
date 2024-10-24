//package app.backend.click_and_buy.repositories;
//
//import app.backend.click_and_buy.entities.ProductTimestamp;
//import app.backend.click_and_buy.entities.UserBehavior;
//import org.springframework.data.jpa.domain.Specification;
//import jakarta.persistence.criteria.*;
//
//public class UserBehaviorSpecifications {
//
//    public static Specification<UserBehavior> findByUserBehaviorId(long userBehaviorId) {
//        return (root, query, criteriaBuilder) -> {
//            // Join with productTimestamps
//            Join<UserBehavior, ProductTimestamp> productTimestampsJoin = root.join("productTimestamps");
//
//            // Add the condition for userBehaviorId
//            Predicate userBehaviorIdPredicate = criteriaBuilder.equal(root.get("userBehaviorId"), userBehaviorId);
//
//            // Add sorting by addedAt
//            query.orderBy(criteriaBuilder.desc(productTimestampsJoin.get("addedAt")));
//
//
//            return userBehaviorIdPredicate;
//        };
//    }
//}
//
