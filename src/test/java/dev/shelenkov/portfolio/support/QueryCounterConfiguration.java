package dev.shelenkov.portfolio.support;

import com.yannbriancon.interceptor.HibernateQueryInterceptor;
import com.yannbriancon.interceptor.HibernateQueryInterceptorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConditionalOnMissingBean(HibernateQueryInterceptor.class)
@Configuration
@EnableConfigurationProperties(HibernateQueryInterceptorProperties.class)
@RequiredArgsConstructor
public class QueryCounterConfiguration {

    private final HibernateQueryInterceptorProperties properties;

    @Bean
    public HibernateQueryInterceptor interceptor() {
        return new HibernateQueryInterceptor(properties);
    }

    @Bean
    public QueryCounterConfiguration.HibernateInterceptorInitializer initializer() {
        return new QueryCounterConfiguration.HibernateInterceptorInitializer(interceptor());
    }

    @RequiredArgsConstructor
    private static class HibernateInterceptorInitializer implements HibernatePropertiesCustomizer {

        private final HibernateQueryInterceptor interceptor;

        @Override
        public void customize(Map<String, Object> hibernateProperties) {
            hibernateProperties.put("hibernate.session_factory.interceptor", interceptor);
        }
    }
}
