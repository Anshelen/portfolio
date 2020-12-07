package dev.shelenkov.portfolio.web.request;

import dev.shelenkov.portfolio.web.support.validation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResendConfirmationEmailRequest {

    @NotNull
    @ValidEmail
    private String email;
}
