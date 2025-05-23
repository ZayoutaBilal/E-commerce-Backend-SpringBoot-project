package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Rating;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.entities.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    UserRating findUserRatingByRatingAndUser(Rating rating, User user);

    List<UserRating> findTop5ByRatingOrderByUpdatedAtDesc(Rating rating);

    List<UserRating> findAllByRating_Product_ProductIdAndIsApproved(long productId,Boolean isApproved);

    long countUserRatingsByRating_RatingIdAndIsApproved(long ratingId, boolean isApproved);
}
