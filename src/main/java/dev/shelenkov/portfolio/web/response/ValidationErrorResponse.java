package dev.shelenkov.portfolio.web.response;

import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationErrorResponse {

    private List<ValidationErrorResponse.Violation> violations = new ArrayList<>();

    public void addViolation(String fieldName, String message) {
        violations.add(new ValidationErrorResponse.Violation(fieldName, message));
    }

    public List<ValidationErrorResponse.Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public void setViolations(List<? extends ValidationErrorResponse.Violation> violations) {
        this.violations = new ArrayList<>(violations);
    }

    @Value
    public static class Violation {

        private final String fieldName;
        private final String message;
    }
}
