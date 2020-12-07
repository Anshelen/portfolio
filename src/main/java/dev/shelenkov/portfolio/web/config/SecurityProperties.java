package dev.shelenkov.portfolio.web.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("PublicInnerClass")
@ConfigurationProperties("security")
@Getter
@RequiredArgsConstructor
@ConstructorBinding
public class SecurityProperties {

    private final SecurityProperties.RememberMeProperties rememberMe;
    private final SecurityProperties.HeaderProperties headers;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Autowired
    private org.springframework.boot.autoconfigure.security.SecurityProperties buildInProperties;

    public int getSecurityFilterChainOrder() {
        return buildInProperties.getFilter().getOrder();
    }

    @SuppressWarnings("WeakerAccess")
    @Getter
    @RequiredArgsConstructor
    public static class RememberMeProperties {
        private final boolean secure;
    }

    @SuppressWarnings({"WeakerAccess", "MagicNumber"})
    @Getter
    @Setter
    // TODO: make this class immutable after spring boot 2.4.0 release
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
