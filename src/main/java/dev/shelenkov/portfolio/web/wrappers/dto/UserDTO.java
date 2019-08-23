package dev.shelenkov.portfolio.web.wrappers.dto;

import dev.shelenkov.portfolio.validation.PasswordMatches;
import dev.shelenkov.portfolio.validation.UniqueEmail;

import javax.validation.constraints.Size;

@PasswordMatches
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
