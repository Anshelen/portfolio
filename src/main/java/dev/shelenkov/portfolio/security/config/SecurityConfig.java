package dev.shelenkov.portfolio.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private WebAuthenticationDetailsSource webAuthenticationDetailsSource;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(e -> e
                .antMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN"))
            .sessionManagement(e -> e
                .maximumSessions(1)
                .expiredUrl("/expiredSession"))
            .formLogin(e -> e
                .loginPage("/login")
                .usernameParameter("email")
                .authenticationDetailsSource(webAuthenticationDetailsSource)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll())
            .oauth2Login(e -> e
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successHandler(authenticationSuccessHandler)
                .authenticationDetailsSource(webAuthenticationDetailsSource)
                .failureHandler(authenticationFailureHandler))
            .logout(e -> e.deleteCookies(securityProperties.getCookieName()))
            .rememberMe(e -> e
                .tokenRepository(tokenRepository)
                .useSecureCookie(securityProperties.getRememberMe().isSecure()))
            .headers(e -> e
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true))
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    securityProperties.getHeaders().getContentSecurityPolicy()))
                .referrerPolicy(ref -> ref.policy(
                    securityProperties.getHeaders().getReferrerPolicy()))
                .featurePolicy(
                    securityProperties.getHeaders().getFeaturePolicy()))
            .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler))
            .cors();
    }
}
