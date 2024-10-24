package app.backend.click_and_buy.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new JCacheCacheManager(jCacheManagerFactory());
    }

    @Bean
    public javax.cache.CacheManager jCacheManagerFactory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager();

        // Define cache configuration with expiry policy
        MutableConfiguration<Object, Object> cacheConfiguration = new MutableConfiguration<>()
                .setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 10)))
                .setStatisticsEnabled(true);
        cacheManager.createCache("passwordVerificationCodes", cacheConfiguration);

        // Define cache configuration with expiry policy for emailConfirmationCode
        MutableConfiguration<Object, Object> emailConfirmationCodeCacheConfiguration = new MutableConfiguration<>()
                .setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 10)))
                .setStatisticsEnabled(true);
        cacheManager.createCache("emailConfirmationCodes", emailConfirmationCodeCacheConfiguration);


        return cacheManager;
    }
}

