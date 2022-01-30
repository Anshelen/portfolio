package dev.shelenkov.portfolio.geo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("ipstack")
@Getter
@RequiredArgsConstructor
@ConstructorBinding
@Validated
public class IpstackProperties {

    @NotBlank
    private final String key;
    @NotBlank
    private final String url;
}
