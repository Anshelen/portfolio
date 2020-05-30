package dev.shelenkov.portfolio.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

    private SecurityProperties.RememberMeProperties rememberMe;

    @SuppressWarnings({"WeakerAccess", "PublicInnerClass"})
    @Data
    public static class RememberMeProperties {
        private boolean secure;
    }
}
