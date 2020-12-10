package dev.shelenkov.portfolio.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
public class ServletConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
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
