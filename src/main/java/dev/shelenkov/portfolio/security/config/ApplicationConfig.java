package dev.shelenkov.portfolio.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;

// TODO: 07.12.2020 split this class to other configs
@Configuration
public class ApplicationConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }

    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartFilter() {
        FilterRegistrationBean<MultipartFilter> bean
            = new FilterRegistrationBean<>(new MultipartFilter());
        bean.setOrder(securityProperties.getSecurityFilterChainOrder() - 2);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
        FilterRegistrationBean<HiddenHttpMethodFilter> bean
            = new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
        bean.setOrder(securityProperties.getSecurityFilterChainOrder() - 1);
        return bean;
    }
}
