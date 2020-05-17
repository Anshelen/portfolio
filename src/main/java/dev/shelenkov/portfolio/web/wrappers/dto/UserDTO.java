package dev.shelenkov.portfolio.web.wrappers.dto;

import dev.shelenkov.portfolio.validation.PasswordMatches;
import dev.shelenkov.portfolio.validation.UniqueEmail;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@PasswordMatches
@NoArgsConstructor
@Data
public class UserDTO {

    @Size(min = 5, max = 30,
        message = "{javax.validation.constraints.Size.UserDTO.userName}")
    private String userName;

    @UniqueEmail
    @Size(max = 50,
        message = "{javax.validation.constraints.Size.UserDTO.email}")
    private String email;

    @Size(min = 5, max = 30,
        message = "{javax.validation.constraints.Size.UserDTO.password}")
    private String password;

    private String matchingPassword;
}
