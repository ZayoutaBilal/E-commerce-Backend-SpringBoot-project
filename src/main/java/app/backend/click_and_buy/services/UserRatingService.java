package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Rating;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.entities.UserRating;
import app.backend.click_and_buy.repositories.UserRatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingService {

    private final UserRatingRepository userRatingRepository;
    public UserRatingService(UserRatingRepository userRatingRepository) {
        this.userRatingRepository = userRatingRepository;
    }
    public UserRating save(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }
    public List<UserRating> findAll() {
        return userRatingRepository.findAll();
    }

    public UserRating findByRatingAndUser(Rating rating, User user) {
        return userRatingRepository.findUserRatingByRatingAndUser(rating,user);
    }
}
