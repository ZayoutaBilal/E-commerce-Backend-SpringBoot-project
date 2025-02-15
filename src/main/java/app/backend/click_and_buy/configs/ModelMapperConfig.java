package app.backend.click_and_buy.configs;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.responses.GetProduct;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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



        return modelMapper;
      }
}
