package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long>, JpaSpecificationExecutor<UserBehavior> {


    UserBehavior findByUser_UserIdAndActionType(
            long userId, UserActionType actionType);
    List<UserBehavior> findByUser_UserId(long userId);
    
    @Query("SELECT pt.product FROM UserBehavior ub JOIN ub.productTimestamps pt WHERE ub.userBehaviorId = :userBehaviorId ORDER BY pt.addedAt DESC")
    List<Product> findProductByUserBehaviorIdOrderByAddedAtDesc(
            @Param("userBehaviorId") long userBehaviorId,
            Pageable pageable);

    @Query("SELECT ct.category FROM UserBehavior ub JOIN ub.categoryTimestamps ct WHERE ub.userBehaviorId = :userBehaviorId ORDER BY ct.addedAt DESC")
    List<Category> findCategoriesByUserBehaviorIdOrderByAddedAtDesc(
            @Param("userBehaviorId") long userBehaviorId,
            Pageable pageable);

    @Query("SELECT pt FROM UserBehavior ub JOIN ub.productTimestamps pt WHERE ub.userBehaviorId = :userBehaviorId AND pt.product.productId = :productId")
    ProductTimestamp findProductTimestampByUserBehaviorIdAndProductId(
            @Param("userBehaviorId") long userBehaviorId,
            @Param("productId") long productId);

    @Query("SELECT ct FROM UserBehavior ub JOIN ub.categoryTimestamps ct WHERE ub.userBehaviorId = :userBehaviorId AND ct.category.categoryId = :categoryId")
    CategoryTimestamp findCategoryTimestampByUserBehaviorIdAndCategoryId(
            @Param("userBehaviorId") long userBehaviorId,
            @Param("categoryId") long categoryId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_behavior_products (user_behavior_id,product_id, added_at, times) VALUES (:userBehaviorId, :productId, :addedAt, :times)", nativeQuery = true)
    void saveProductTimestampsBatch(@Param("userBehaviorId") Long userBehaviorId,
                                    @Param("productId") Long productId,
                                    @Param("addedAt") LocalDateTime addedAt,
                                    @Param("times") Integer times);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_behavior_products SET added_at = :addedAt, times = :times WHERE user_behavior_id = :userBehaviorId AND product_id = :productId", nativeQuery = true)
    void updateProductTimestampsBatch(@Param("userBehaviorId") Long userBehaviorId,
                                      @Param("productId") Long productId,
                                      @Param("addedAt") LocalDateTime addedAt,
                                      @Param("times") Integer times);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_behavior_categories (user_behavior_id,category_id, added_at, times) VALUES (:userBehaviorId, :categoryId, :addedAt, :times)", nativeQuery = true)
    void saveCategoryTimestampsBatch(@Param("userBehaviorId") Long userBehaviorId,
                                    @Param("categoryId") Long categoryId,
                                    @Param("addedAt") LocalDateTime addedAt,
                                    @Param("times") Integer times);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user_behavior_categories SET added_at = :addedAt, times = :times WHERE user_behavior_id = :userBehaviorId AND category_id = :categoryId", nativeQuery = true)
    void updateCategoryTimestampsBatch(@Param("userBehaviorId") Long userBehaviorId,
                                      @Param("categoryId") Long categoryId,
                                      @Param("addedAt") LocalDateTime addedAt,
                                      @Param("times") Integer times);


    @Query("SELECT pt.product, COUNT(pt) as count " +
            "FROM UserBehavior ub " +
            "JOIN ub.productTimestamps pt " +
            "WHERE ub.actionType = :actionType " +
            "GROUP BY pt.product " +
            "ORDER BY count DESC")
    Page<Product> findTopProductsByAction(@Param("actionType") UserActionType actionType, Pageable pageable);

    @Query("SELECT ct.category, COUNT(ct) as count " +
            "FROM UserBehavior ub " +
            "JOIN ub.categoryTimestamps ct " +
            "GROUP BY ct.category " +
            "ORDER BY count DESC")
    Page<Category> findTopCategories(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_behavior_products WHERE product_id = :productId", nativeQuery = true)
    void deleteProductTimestampBy(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_behavior_categories WHERE category_id = :category_id", nativeQuery = true)
    void deleteCategoryTimestampBy(@Param("category_id") Long category_id);

}
