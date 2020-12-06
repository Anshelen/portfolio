package dev.shelenkov.portfolio.web.wrappers.dto;

import dev.shelenkov.portfolio.validation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResendConfirmationEmailDTO {

    @NotNull
    @ValidEmail
    private String email;
}
