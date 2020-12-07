package dev.shelenkov.portfolio.web.config;

import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.service.attempts.ILoginAttemptsAware;
import dev.shelenkov.portfolio.web.exception.TooManyLoginAttemptsException;
import dev.shelenkov.portfolio.web.security.ExtendedDaoAuthenticationProvider;
import dev.shelenkov.portfolio.web.security.LoggingAccessDeniedHandler;
import dev.shelenkov.portfolio.web.security.OverridingIpWebAuthenticationDetailsSource;
import dev.shelenkov.portfolio.web.security.ParametersMappingAuthenticationFailureHandler;
import dev.shelenkov.portfolio.web.security.oauth2.OAuth2NoVerifiedEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Autowired
    private ILoginAttemptsAware loginAttemptsAwareService;

    @Value("${server.servlet.session.cookie.name}")
    private String cookieName;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ParametersMappingAuthenticationFailureHandler authenticationFailureHandler() {
        ParametersMappingAuthenticationFailureHandler handler
            = new ParametersMappingAuthenticationFailureHandler();
        Map<String, String> map = new HashMap<>();
        map.put(BadCredentialsException.class.getName(),
            "/login?error=BadCredentials");
        map.put(CredentialsExpiredException.class.getName(),
            "/login?error=CredentialsExpired");
        map.put(LockedException.class.getName(),
            "/login?error=Locked");
        map.put(DisabledException.class.getName(),
            "/login?error=Disabled&email=${email}");
        map.put(OAuth2NoVerifiedEmailException.class.getName(),
            "/login?error=NoVerifiedEmail");
        map.put(TooManyLoginAttemptsException.class.getName(),
            "/login?error=TooManyAttempts");
        handler.setExceptionMappings(map);
        return handler;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedOrigins(securityProperties.getHeaders().getAccessControlAllowOrigin());
        configuration.setMaxAge(securityProperties.getHeaders().getAccessControlMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new LoggingAccessDeniedHandler();
    }

    @Bean
    public WebAuthenticationDetailsSource webAuthenticationDetailsSource() {
        return new OverridingIpWebAuthenticationDetailsSource();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(String.format(
            "%s > %s", Role.ADMIN.getFullName(), Role.USER.getFullName()));
        return hierarchy;
    }

    @Bean
    public ExtendedDaoAuthenticationProvider daoAuthenticationProvider() {
        return new ExtendedDaoAuthenticationProvider(
            loginAttemptsAwareService, userDetailsService, passwordEncoder());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
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
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(webAuthenticationDetailsSource())
                .failureHandler(authenticationFailureHandler())
                .permitAll())
            .oauth2Login(e -> e
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(webAuthenticationDetailsSource())
                .failureHandler(authenticationFailureHandler()))
            .logout(e -> e.deleteCookies(cookieName))
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
            .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler()))
            .cors();
    }
}
