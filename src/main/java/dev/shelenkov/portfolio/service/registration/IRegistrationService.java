package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.model.Account;

import java.util.UUID;

/**
 * Service for registering new users.
 */
public interface IRegistrationService {

    /**
     * Register new user.
     *
     * @param userName user's name
     * @param email    user's email
     * @param password user's password
     */
    void registerNewUser(String userName, String email, String password);

    /**
     * Enables previously created account if token provided by user is correct.
     *
     * @param token verification token (provided by user)
     * @return user's account
     * @throws TokenNotValidException in case of token is not valid (it can be
     *                                if e.g. token wasn't given out or the
     *                                account was already activated)
     */
    Account confirmRegistration(UUID token) throws TokenNotValidException;
}
