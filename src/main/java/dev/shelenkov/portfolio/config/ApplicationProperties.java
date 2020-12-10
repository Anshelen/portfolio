package dev.shelenkov.portfolio.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("application")
@Getter
@RequiredArgsConstructor
@ConstructorBinding
@Validated
public class ApplicationProperties {

    @NotBlank
    private final String baseUrl;
    private final String rootUrl;
}
