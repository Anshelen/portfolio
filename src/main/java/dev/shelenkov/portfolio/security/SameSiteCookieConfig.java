package dev.shelenkov.portfolio.security;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SameSiteCookieConfig {
    /*
     * This config can not be moved to SecurityConfig due to issue
     * https://stackoverflow.com/questions/48971937/ugrade-spring-boot-2-0-0-rc2-exception-no-servletcontext-set
     */

    @Bean
    public TomcatContextCustomizer sameSiteCookiesConfig() {
        return context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies(SameSiteCookies.LAX.getValue());
            context.setCookieProcessor(cookieProcessor);
        };
    }
}
