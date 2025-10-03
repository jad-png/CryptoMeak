package ui.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

import config.DIContainer;
import controller.AuthController;
import model.Wallet;
import model.enums.Currency;
import service.result.AuthResult;
import ui.interfaces.Command;
import utils.AddressGenerator;

public class CreateWalletTransactionCommand implements Command {
    private final AuthController authCon;
    private final Scanner scanner = new Scanner(System.in);

    public CreateWalletTransactionCommand(AuthController authCon) {
        this.authCon = authCon;
    }

    public String getName() {
        return "create wallet";
    }

    public String getDescription() {
        return "create a new wallet";
    }

    public void execute(CommandContext context) {
        System.out.println("\n" + "==========================================================================");
        System.out.println("CREATE NEW WALLET");
        System.out.println("=============================================================================");

        try {
            // STEP 1
            // collect all wallet creation details
            // ownerName
            // walletName
            // currency
            // password

            String ownerName = collectOwnerName();
            String walletName = collectWalletName();
            Currency currency = selectCurrency();
            String password = collectPassword();

            // step 2
            // generate walletAddress
            Wallet tempWallet = new Wallet.Builder()
                    .id(UUID.randomUUID().toString())
                    .ownerName(ownerName)
                    .wtName(walletName)
                    .currency(currency)
                    .balance(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .build();
            String walletAddress = AddressGenerator.generateAddress(tempWallet);

            // step 3
            // create wallet object using builder pattern
            Wallet wallet = new Wallet.Builder()
                    .id(tempWallet.getId())
                    .address(walletAddress)
                    .ownerName(ownerName)
                    .wtName(walletName)
                    .currency(currency)
                    .balance(BigDecimal.ZERO)
                    .createdAt(tempWallet.getCreatedAt())
                    .build();

            displayWalletSummary(wallet);

            if (confirmWalletCreation()) {
                AuthResult result = authCon.registerWallet(wallet, password);

                // Handle registration result
                handleRegistrationResult(result, context, walletAddress, password);
            } else {
                System.out.println("Wallet creation cancelled by user.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String collectOwnerName() {
        System.out.println("\nOWNER INFORMATION");
        System.out.println("Enter owner's full name: ");
        String ownerName = scanner.nextLine().trim();

        if (ownerName.isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty");
        }

        if (ownerName.length() < 2) {
            throw new IllegalArgumentException("Owner name must be at least 2 characters long");
        }

        if (!ownerName.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException("Owner name can only contain letters and spaces.");
        }

        return ownerName;
    }

     private String collectWalletName() {
        System.out.println("\nWALLET INFORMATION");
        System.out.print("Enter wallet name (optional): ");
        String walletName = scanner.nextLine().trim();

        if (walletName.isEmpty()) {
            return "My Crypto Wallet";
        }

        if (walletName.length() > 50) {
            throw new IllegalArgumentException("Wallet name cannot exceed 50 characters.");
        }

        return walletName;
    }

    private Currency selectCurrency() {
        System.out.println("\nCURRENCY SELECTION");
        System.out.println("Available cryptocurrencies: ");

        Currency[] currencies = Currency.values();
        for (int i = 0; i < currencies.length; i++) {
            // get currency description
            String description = getCurrencyDescription(currencies[i]);
            System.out.printf("%d. %s - %s%n", i + 1, currencies[i], description);
        }

        System.out.println("Select currency (1-" + currencies.length + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice < 1 || choice > currencies.length) {
                throw new IllegalArgumentException("Invalid currency selection");
            }

            Currency selectedCurrency = currencies[choice - 1];

            // display currency Info
            return selectedCurrency;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input. Please enter a number.");
        }
    }

    private String collectPassword() {
        System.out.println("\nWALLET SECURITY");
        System.out.println("Password requirements: ");
        System.out.println("• Minimum 6 characters");
        System.out.println("• Recommended: mix of letters, numbers, and symbols");

        String password = collectPasswordInput("Enter password: ");
        String confirmPassword = collectPasswordInput("Confirm password: ");

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match. Please try again.");
        }

        validatePasswordStrength(password);

        return password;
    }

    private String collectPasswordInput(String prompt) {
        System.out.print(prompt);
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        return password;
    }

    private void validatePasswordStrength(String password) {
        // Basic password strength validation
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int strengthScore = 0;
        if (hasLetter)
            strengthScore++;
        if (hasDigit)
            strengthScore++;
        if (hasSpecial)
            strengthScore++;
        if (password.length() >= 8)
            strengthScore++;

        if (strengthScore < 2) {
            System.out.println("Warning: Your password is weak. Consider using a stronger password.");
        } else if (strengthScore >= 3) {
            System.out.println("Password strength: Good");
        }
    }

    private void displayCurrencyInfo(Currency currency) {
        String info = getCurrencyInformation(currency);
        System.out.println("" + info);
    }

    private void displayWalletSummary(Wallet wallet) {
        System.out.println("\n" + "=======================================================================");
        System.out.println("WALLET SUMMARY");
        System.out.println("======================================================================");

        System.out.printf("Owner: %s%n", wallet.getOwnerName());
        System.out.printf("Wallet Name: %s%n", wallet.getWtName());
        System.out.printf("Currency: %s%n", wallet.getCurrency());
        System.out.printf("Address: %s%n", wallet.getAddress());
        System.out.printf("Initial Balance: %s %s%n", wallet.getBalance(), wallet.getCurrency());
        System.out.println("======================================================================");
        System.out.println("Important: Save your wallet address and password securely!");
        System.out.println("   You will need them to access your wallet.");
    }

    private boolean confirmWalletCreation() {
        System.out.print("\nConfirm wallet creation? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        return confirmation.equals("yes") || confirmation.equals("y");
    }

    private void handleRegistrationResult(AuthResult result, CommandContext context,
            String walletAddress, String password) {
        if (result.isSuccess()) {
            displaySuccessMessage(result, walletAddress);

            // Auto-login after successful registration
            performAutoLogin(context, walletAddress, password);

        } else {
            displayErrorMessage(result);
        }
    }

    private void displaySuccessMessage(AuthResult result, String walletAddress) {
        System.out.println("\nWALLET CREATED SUCCESSFULLY!");
        System.out.println("===========================================================================");
        System.out.printf("%s%n", result.getMessage());
        System.out.printf("Wallet Address: %s%n", walletAddress);
        System.out.println("===========================================================================");
        System.out.println("Important Information:");
        System.out.println("• Save your wallet address - you'll need it to receive funds");
        System.out.println("• Keep your password secure - it cannot be recovered");
        System.out.println("• Your wallet is ready to receive " + result.getWallet().getCurrency() + " deposits");
        System.out.println("============================================================================");
    }

    private void displayErrorMessage(AuthResult result) {
        System.out.println("\nWALLET CREATION FAILED");
        System.out.println("============================================================================");
        System.out.printf("Error: %s%n", result.getMessage());
        System.out.println("============================================================================");
        System.out.println("Possible solutions:");
        System.out.println("• Try a different wallet name");
        System.out.println("• Ensure the wallet address is not already in use");
        System.out.println("• Check your internet connection");
    }

    private void performAutoLogin(CommandContext context, String walletAddress, String password) {
        System.out.println("\nAttempting auto-login...");

        try {
            AuthResult loginResult = authCon.login(walletAddress, password);

            if (loginResult.isSuccess()) {
                context.put("currentWallet", loginResult.getWallet());
                context.put("sessionId", loginResult.getSessionId());
                System.out.println("Auto-login successful! You are now connected to your new wallet.");
            } else {
                System.out.println("Auto-login failed. Please login manually with your wallet address and password.");
            }

        } catch (Exception e) {
            System.out.println("Auto-login failed: " + e.getMessage());
        }
    }

    // Utility methods
    private String getCurrencyDescription(Currency currency) {
        switch (currency) {
            case BITCOIN:
                return "Bitcoin (BTC) - Original cryptocurrency";
            case ETHEREUM:
                return "Ethereum (ETH) - Smart contract platform";
            default:
                return "Cryptocurrency";
        }
    }

    private String getCurrencyInformation(Currency currency) {
        switch (currency) {
            case BITCOIN:
                return "Bitcoin: Decentralized digital currency, no central authority";
            case ETHEREUM:
                return "Ethereum: Blockchain platform for decentralized applications";
            default:
                return "Cryptocurrency wallet";
        }
    }
}
