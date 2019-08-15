package dev.shelenkov.portfolio.web.wrappers.dto;

import dev.shelenkov.portfolio.validation.PasswordMatches;
import dev.shelenkov.portfolio.validation.UniqueEmail;

import javax.validation.constraints.Size;

@PasswordMatches
public class UserDTO {

    @Size(min = 5, max = 30, message = "{Register.UserName}")
    private String userName;

    @UniqueEmail
    @Size(max = 50, message = "{Register.Email}")
    private String email;

    @Size(min = 5, max = 30, message = "{Register.Password}")
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
