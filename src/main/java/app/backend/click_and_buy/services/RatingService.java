package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Rating;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.entities.UserRating;
import app.backend.click_and_buy.repositories.RatingRepository;
import app.backend.click_and_buy.statics.ObjectValidator;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRatingService userRatingService;


    public RatingService(RatingRepository ratingRepository, UserRatingService userRatingService, EntityManager entityManager) {
        this.ratingRepository = ratingRepository;
        this.userRatingService = userRatingService;
    }
    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Optional<Rating> findById(long id) {
        return ratingRepository.findById(id);
    }

    public boolean addUserRating(Rating rating, User user,Integer stars,String comment) {
        try{
            UserRating userRating=userRatingService.findByRatingAndUser(rating,user);
            if(userRating != null){
                if(stars != null) userRating.setStars(stars);
                if (ObjectValidator.stringValidator(comment))    userRating.setComment(comment);
            }else {
                userRating = UserRating.builder()
                        .user(user).stars(Objects.requireNonNullElse(stars,0)).comment(comment).rating(rating).build();
            }
            userRatingService.save(userRating);
            rating.updateRatingSummary();
            ratingRepository.save(rating);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
