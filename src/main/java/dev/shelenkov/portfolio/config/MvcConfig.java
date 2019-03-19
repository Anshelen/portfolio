package dev.shelenkov.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@EnableTransactionManagement
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
        registry.addViewController("/index.html").setViewName("/index");
        registry.addViewController("/about.html").setViewName("/about");
        registry.addViewController("/skills.html").setViewName("/skills");
        registry.addViewController("/projects.html").setViewName("/projects");
        registry.addViewController("/contacts.html").setViewName("/contacts");
        registry.addViewController("/login.html").setViewName("/login");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/*");
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    @Bean
    public CookieLocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }
}
