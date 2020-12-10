package dev.shelenkov.portfolio.security.config;

import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.security.internal.LoggingAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class AuthorizationConfig {

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new LoggingAccessDeniedHandler();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(String.format(
            "%s > %s", Role.ADMIN.getFullName(), Role.USER.getFullName()));
        return hierarchy;
    }
}
