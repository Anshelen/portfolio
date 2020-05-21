package dev.shelenkov.portfolio.service.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mail")
@Data
public class MailProperties {
    private String adminAddress;
    private String adminName;
}
