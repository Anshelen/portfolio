package dev.shelenkov.portfolio.validation.validator;

import dev.shelenkov.portfolio.validation.PasswordMatches;
import dev.shelenkov.portfolio.web.wrappers.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, UserDTO> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // no special annotation params
    }

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
        String password = user.getPassword();
        if (password == null) {
            return false;
        }
        return password.equals(user.getMatchingPassword());
    }
}
