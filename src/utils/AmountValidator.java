package utils;

import java.math.BigDecimal;

public class AmountValidator {
    private AmountValidator() {}

    public static void validate(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        if (amount.scale() > 8) {
            throw new IllegalArgumentException("Amount cannot have more than 8 decimal places");
        }
    }

    public static boolean isValid(BigDecimal amount) {
        try {
            validate(amount);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
