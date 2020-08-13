package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.security.oauth2.OAuth2NoVerifiedEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@Import(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    @Autowired
    private PersistentTokenRepository tokenRepository;

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
            "/login.html?error=BadCredentials");
        map.put(CredentialsExpiredException.class.getName(),
            "/login.html?error=CredentialsExpired");
        map.put(LockedException.class.getName(),
            "/login.html?error=Locked");
        map.put(DisabledException.class.getName(),
            "/login.html?error=Disabled&email=${email}");
        map.put(OAuth2NoVerifiedEmailException.class.getName(),
            "/login.html?error=NoVerifiedEmail");
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(e -> e
            .antMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
        );

        http.sessionManagement(e -> e
            .maximumSessions(1)
            .expiredUrl("/expiredSession.html")
        );

        http.formLogin(e -> e
            .loginPage("/login.html")
            .usernameParameter("email")
            .defaultSuccessUrl("/")
            .failureHandler(authenticationFailureHandler())
            .permitAll()
        );

        http.oauth2Login(e -> e
            .loginPage("/login.html")
            .defaultSuccessUrl("/")
            .failureHandler(authenticationFailureHandler())
        );

        http.logout(e -> e.deleteCookies(cookieName));

        http.rememberMe(e -> e
            .tokenRepository(tokenRepository)
            .useSecureCookie(securityProperties.getRememberMe().isSecure())
        );

        http.headers(e -> e
            .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true))
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                securityProperties.getHeaders().getContentSecurityPolicy()))
        );

        http.cors();

        http.csrf().disable();
    }
}
