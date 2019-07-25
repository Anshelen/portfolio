package dev.shelenkov.portfolio.web.wrappers.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationErrorResponse {

    private List<Violation> violations = new ArrayList<>();

    public void addViolation(Violation violation) {
        violations.add(violation);
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public void setViolations(List<? extends Violation> violations) {
        this.violations = new ArrayList<>(violations);
    }
}
