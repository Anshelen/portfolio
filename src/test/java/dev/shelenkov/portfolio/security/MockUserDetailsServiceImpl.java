package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.model.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

public class MockUserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    private final HashMap<String, Account> users = new HashMap<>();

    public MockUserDetailsServiceImpl() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Account user = new Account(
            "user",
            "user@mail.ru",
            passwordEncoder.encode("user"),
            Role.USER);
        user.setEnabled(true);
        users.put(user.getEmail(), user);

        Account disabledUser = new Account(
            "disabled",
            "disabled@mail.ru",
            passwordEncoder.encode("disabled"),
            Role.USER);
        users.put(disabledUser.getEmail(), disabledUser);

        Account admin = new Account(
            "admin",
            "admin@mail.ru",
            passwordEncoder.encode("admin"),
            Role.ADMIN);
        admin.setEnabled(true);
        users.put(admin.getEmail(), admin);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return users.get(email);
    }
}
