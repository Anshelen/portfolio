package dev.shelenkov.portfolio.validation.validator;

import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.validation.UniqueEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator
    implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        // no special annotation params
    }

    @Override
    public boolean isValid(String obj, ConstraintValidatorContext context) {
        return !accountRepository.existsByEmail(obj);
    }
}
