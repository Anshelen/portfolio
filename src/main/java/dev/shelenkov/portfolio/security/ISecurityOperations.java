package dev.shelenkov.portfolio.security;

import dev.shelenkov.portfolio.domain.Account;

public interface ISecurityOperations {

    String encryptPassword(String password);

    void loginAccount(Account account);
}
