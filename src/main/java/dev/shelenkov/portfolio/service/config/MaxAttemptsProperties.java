package dev.shelenkov.portfolio.service.config;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("attempts")
@Getter
@ConstructorBinding
public class MaxAttemptsProperties {

    private final int resendConfirmationEmail;
    private final int login;
    private final int sendEmailToAdmin;

    public MaxAttemptsProperties(Integer resendConfirmationEmail,
                                 Integer login,
                                 Integer sendEmailToAdmin) {
        this.resendConfirmationEmail = ObjectUtils.firstNonNull(resendConfirmationEmail, 3);
        this.login = ObjectUtils.firstNonNull(login, 3);
        this.sendEmailToAdmin = ObjectUtils.firstNonNull(sendEmailToAdmin, 3);
    }
}
