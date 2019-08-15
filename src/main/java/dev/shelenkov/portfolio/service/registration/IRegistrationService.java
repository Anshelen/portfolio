package dev.shelenkov.portfolio.service.registration;

/**
 * Service for registering new users.
 */
public interface IRegistrationService {

    /**
     * Register new user.
     *
     * @param userName user's name
     * @param email user's email
     * @param password user's password
     */
    void registerNewUser(String userName, String email, String password);
}
