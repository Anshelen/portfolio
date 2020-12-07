package dev.shelenkov.portfolio.support;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.security.internal.AbstractExtendedUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_NAME;
import static dev.shelenkov.portfolio.support.SecurityConstants.ADMIN_PASSWORD;
import static dev.shelenkov.portfolio.support.SecurityConstants.DISABLED_USER_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.DISABLED_USER_NAME;
import static dev.shelenkov.portfolio.support.SecurityConstants.DISABLED_USER_PASSWORD;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_EMAIL;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_NAME;
import static dev.shelenkov.portfolio.support.SecurityConstants.ENABLED_USER_PASSWORD;

public class MockUserDetailsServiceImpl extends AbstractExtendedUserDetailsService {

    private final HashMap<String, Account> users = new HashMap<>();

    public MockUserDetailsServiceImpl() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Account user = new Account(
            ENABLED_USER_NAME,
            ENABLED_USER_EMAIL,
            passwordEncoder.encode(ENABLED_USER_PASSWORD),
            Role.USER);
        user.setEnabled(true);
        users.put(user.getEmail(), user);

        Account disabledUser = new Account(
            DISABLED_USER_NAME,
            DISABLED_USER_EMAIL,
            passwordEncoder.encode(DISABLED_USER_PASSWORD),
            Role.USER);
        users.put(disabledUser.getEmail(), disabledUser);

        Account admin = new Account(
            ADMIN_NAME,
            ADMIN_EMAIL,
            passwordEncoder.encode(ADMIN_PASSWORD),
            Role.ADMIN);
        admin.setEnabled(true);
        users.put(admin.getEmail(), admin);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return users.get(email);
    }
}
