package dev.shelenkov.portfolio.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("PublicInnerClass")
@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

    private SecurityProperties.RememberMeProperties rememberMe;

    private SecurityProperties.HeaderProperties headers;

    @Data
    public static class RememberMeProperties {
        private boolean secure;
    }

    @SuppressWarnings({"WeakerAccess", "MagicNumber"})
    @Data
    public static class HeaderProperties {

        private List<String> accessControlAllowOrigin;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration accessControlMaxAge = Duration.ofMinutes(30);
    }
}
