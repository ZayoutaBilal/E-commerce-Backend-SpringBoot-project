package app.backend.click_and_buy.configs;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.UserRating;
import app.backend.click_and_buy.responses.GetProduct;
import app.backend.click_and_buy.responses.ProductRating;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;


@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.registerModule(new RecordModule());


        modelMapper.addMappings(new PropertyMap<Product, GetProduct>() {
            @Override
            protected void configure() {
                skip(destination.getCategory());
                skip(destination.getDiscount());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserRating, ProductRating>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getUserId());
                map().setUserName(source.getUser().getUsername());
                map().setIsReported(Objects.requireNonNullElse(source.getUser().getIsReported(),Boolean.FALSE));
            }
        });



        return modelMapper;
      }
}
