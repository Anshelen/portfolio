package dev.shelenkov.portfolio.config;

import dev.shelenkov.portfolio.web.auxiliary.SupportedLanguagesCookieLocaleResolver;
import dev.shelenkov.portfolio.web.converter.ResumeFormatConverter;
import dev.shelenkov.portfolio.web.converter.ResumeLanguageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private ThymeleafProperties thymeleafProperties;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/about.html").setViewName("about");
        registry.addViewController("/skills.html").setViewName("skills");
        registry.addViewController("/projects.html").setViewName("projects");
        registry.addViewController("/contacts.html").setViewName("contacts");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
        registry.addViewController("/expiredSession.html").setViewName("expiredSession");
        registry.addViewController("/literals.js").setViewName("literals");
        registry.addViewController("/admin/").setViewName("admin/admin_home");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ResumeLanguageConverter());
        registry.addConverter(new ResumeFormatConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/*");
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
        changeInterceptor.setParamName("lang");
        return changeInterceptor;
    }

    @Bean
    public SupportedLanguagesCookieLocaleResolver localeResolver() {
        SupportedLanguagesCookieLocaleResolver resolver
            = new SupportedLanguagesCookieLocaleResolver(Arrays.asList("en", "ru"));
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieName("localeInfo");
        return resolver;
    }

    @Bean
    @Autowired
    public ITemplateResolver javascriptTemplateResolver(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix(thymeleafProperties.getPrefix());
        resolver.setSuffix(".js");
        resolver.setTemplateMode(TemplateMode.JAVASCRIPT);
        if (thymeleafProperties.getEncoding() != null) {
            resolver.setCharacterEncoding(thymeleafProperties.getEncoding().name());
        }
        resolver.setCacheable(thymeleafProperties.isCache());
        resolver.setCheckExistence(thymeleafProperties.isCheckTemplate());
        return resolver;
    }

    @Bean
    @Autowired
    public ThymeleafViewResolver javascriptViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding(thymeleafProperties.getEncoding().name());
        resolver.setContentType("application/javascript");
        resolver.setProducePartialOutputWhileProcessing(
            thymeleafProperties.getServlet().isProducePartialOutputWhileProcessing());
        resolver.setViewNames(new String[]{"*.js"});
        resolver.setCache(thymeleafProperties.isCache());
        return resolver;
    }

    /**
     * Keys of messages that should be available to a JS code via 'messages'
     * object. This object contains localized messages that are used in JS code.
     * See literals.js.
     */
    @Bean
    public String[] jsMessageKeys() {
        return new String[] {
            "contacts.js.validation.name.required",
            "contacts.js.validation.name.minlength",
            "contacts.js.validation.name.maxlength",
            "contacts.js.validation.subject.required",
            "contacts.js.validation.subject.minlength",
            "contacts.js.validation.subject.maxlength",
            "contacts.js.validation.text.required",
            "contacts.js.validation.text.maxlength"
        };
    }

    /**
     * A map with keys and corresponding urls that should be available to a JS
     * code via 'ajax_urls' object. This object contains context-corrected urls
     * for ajax requests.
     * See literals.js.
     */
    @Bean
    public Map<String, String> ajaxUrlMap() {
        Map<String, String> ajaxMap = new HashMap<>();
        ajaxMap.put("sendMail", "/email/send");
        ajaxMap.put("resendRegistrationEmail", "/resendRegistrationEmail");
        return ajaxMap;
    }
}
