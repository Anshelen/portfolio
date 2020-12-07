package dev.shelenkov.portfolio.support.validation;

import dev.shelenkov.portfolio.service.account.AccountService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final AccountService accountService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        // no special annotation params
    }

    @Override
    public boolean isValid(String obj, ConstraintValidatorContext context) {
        return !accountService.existsByEmail(obj);
    }
}
