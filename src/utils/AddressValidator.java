package utils;

import model.enums.Currency;

public class AddressValidator {
    private AddressValidator() {}

    public static boolean isValidFormat(String address, Currency currency) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        switch (currency) {
            case BITCOIN:
                return address.startsWith("1") && address.length() == 40 &&
                        address.matches("[0-9A-F]+");
            case ETHEREUM:
                return address.startsWith("0x") && address.length() == 42 &&
                        address.matches("0x[0-9a-fA-F]+");
            default:
                return false;
        }
    }

    public static void validateFormat(String address, Currency currency) {
        if (!isValidFormat(address, currency)) {
            throw new IllegalArgumentException("Invalid address format for " + currency + ": " + address);
        }
    }
}
