package dev.shelenkov.portfolio.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("PublicInnerClass")
@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Autowired
    private org.springframework.boot.autoconfigure.security.SecurityProperties buildInProperties;

    private SecurityProperties.RememberMeProperties rememberMe;

    private SecurityProperties.HeaderProperties headers;

    public int getSecurityFilterChainOrder() {
        return buildInProperties.getFilter().getOrder();
    }

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

        private String contentSecurityPolicy = "default-src 'self'";

        private ReferrerPolicyHeaderWriter.ReferrerPolicy referrerPolicy
            = ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;

        private String featurePolicy = "geolocation 'none'";
    }
}
