package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Rating;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.entities.UserRating;
import app.backend.click_and_buy.repositories.UserRatingRepository;
import app.backend.click_and_buy.responses.ProductRating;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserRatingService {

    private final UserRatingRepository userRatingRepository;
    private final ModelMapper modelMapper;

    public UserRatingService(UserRatingRepository userRatingRepository, ModelMapper modelMapper) {
        this.userRatingRepository = userRatingRepository;
        this.modelMapper = modelMapper;
    }

    public UserRating save(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }

    public List<UserRating> findAll() {
        return userRatingRepository.findAll();
    }

    public UserRating findByRatingAndUser(Rating rating, User user) {
        return userRatingRepository.findUserRatingByRatingAndUser(rating, user);
    }

    public List<ProductRating> getProductComments(long productId, Boolean isApproved){
        List<UserRating> userRatingList;
        if(isApproved) userRatingList = userRatingRepository.findAllByRating_Product_ProductIdAndIsApproved(productId,Boolean.TRUE);
        else userRatingList = userRatingRepository.findAllByRating_Product_ProductIdAndIsApproved(productId,Boolean.FALSE);
        if(Objects.isNull(userRatingList) || userRatingList.isEmpty()) return List.of();
        else    return userRatingList.stream().map( ur -> modelMapper.map(ur,ProductRating.class)).collect(Collectors.toList());
    }

    public void deleteUserRating(long userRatingId){
        userRatingRepository.deleteById(userRatingId);
    }

    public void approveOnUserRating(long userRatingId){
        userRatingRepository.findById(userRatingId).ifPresentOrElse( userRating -> {
            userRating.setIsApproved(Boolean.TRUE);
            userRatingRepository.save(userRating);
        },() -> {throw new EntityNotFoundException("The UserRating with id "+userRatingId+" does not exists");
        });
    }
}
