package dev.shelenkov.portfolio.web.support.validation;

import dev.shelenkov.portfolio.web.request.RegisterUserRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, RegisterUserRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // no special annotation params
    }

    @Override
    public boolean isValid(RegisterUserRequest user, ConstraintValidatorContext context) {
        String password = user.getPassword();
        if (password == null) {
            return false;
        }
        return password.equals(user.getMatchingPassword());
    }
}
