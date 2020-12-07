package dev.shelenkov.portfolio.service.registration;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.service.exception.TokenNotValidException;

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
     * Register new GitHub user. Account is enabled immediately.
     *
     * @param userName user's login
     * @param email    user's email
     * @param githubId user's github id
     * @return created account
     */
    Account registerNewGitHubUser(String userName, String email, String githubId);

    /**
     * Register new Google user. Account is enabled immediately.
     *
     * @param userName user's login
     * @param email    user's email
     * @param googleId user's google id
     * @return created account
     */
    Account registerNewGoogleUser(String userName, String email, String googleId);

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

    /**
     * Sends email with a link to confirm email ownership.
     *
     * @param email user's email
     */
    void sendConfirmationEmail(String email);

    /**
     * Checks if a confirmation email can be sent.
     *
     * @param email user's email
     */
    boolean isSendConfirmationEmailForbidden(String email);
}
