package dev.shelenkov.portfolio.mail.config;

import dev.shelenkov.portfolio.support.validation.ValidEmail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("mail")
@Getter
@RequiredArgsConstructor
@ConstructorBinding
@Validated
public class MailProperties {
    @ValidEmail
    private final String adminAddress;
    @NotBlank
    private final String adminName;
}
