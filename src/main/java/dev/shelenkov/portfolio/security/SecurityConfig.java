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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN");

        http.authorizeRequests()
            .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN");

        http.sessionManagement()
            .maximumSessions(1).expiredUrl("/expiredSession.html");

        http.formLogin()
            .loginPage("/login.html")
            .usernameParameter("email")
            .defaultSuccessUrl("/")
            .failureHandler(authenticationFailureHandler())
            .permitAll();

        http.oauth2Login()
            .loginPage("/login.html")
            .defaultSuccessUrl("/")
            .failureHandler(authenticationFailureHandler());

        http.logout().deleteCookies(cookieName);

        http.rememberMe()
            .tokenRepository(tokenRepository)
            .useSecureCookie(securityProperties.getRememberMe().isSecure());

        http.csrf().disable();
    }
}
