package dev.shelenkov.portfolio.web.support.validation;

import dev.shelenkov.portfolio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueEmailValidator
    implements ConstraintValidator<UniqueEmail, String> {

    private final AccountRepository accountRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        // no special annotation params
    }

    @Override
    public boolean isValid(String obj, ConstraintValidatorContext context) {
        return !accountRepository.existsByEmail(obj);
    }
}
