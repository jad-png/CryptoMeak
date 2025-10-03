package ui.command;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

import model.Wallet;
import model.enums.Currency;
import java.util.Scanner;
import java.util.UUID;

import config.DIContainer;
import ui.interfaces.Command;
import utils.AddressGenerator;
import utils.PasswordUtil;
import controller.AuthController;
import service.result.AuthResult;

public class RegisterWalletCommand implements Command {
    private final AuthController authCon;
    private final Scanner scanner;

    public RegisterWalletCommand(AuthController authCon, Scanner scanner) {
        this.authCon = authCon;
        this.scanner = new Scanner(System.in);
    }

    public String getName() {
        return "Register";
    }

    public String getDescription() {
        return "Register a new wallet";
    }

    public void execute(CommandContext context) {
        // todo: implement ui menu for registration;
        System.out.println("\n=== WALLET REGISTRATION ===");

        String ownerName = collectOwnerName();
        Currency currency = selectCurrency();
        String wtName = collectWalletName();
        String password = collectPassword();

        String address = AddressGenerator.generateAddress(currency);

        Wallet wt = new Wallet.Builder()
                .id(UUID.randomUUID().toString())
                .address(address)
                .ownerName(ownerName)
                .wtName(wtName)
                .currency(currency)
                .balance(0.0)
                .createdAt(LocalDateTime.now())
                .build();

        AuthResult rs = authCon.registerWallet(wt, password);

        if (rs.isSuccess()) {
            System.out.println("\nSuccess" + rs.getMessage());
            System.out.println("Wallet Address: " + address);
            System.out.println("Keep your password safe!");

            AuthResult loginRs = authCon.login(address, password);

            if (loginRs.isSuccess()) {
                context.put("currentWallet", loginRs.getWallet());
                context.put("sessionId", loginRs.getSessionId());

                System.out.println("You were Auto-logged in!");
            }
        } else {
            System.out.println("\nError: " + rs.getMessage());
        }
    }

    private String collectOwnerName() {
        System.out.print("Enter owner name: ");
        String ownerName = scanner.nextLine().trim();

        if (ownerName.isEmpty()) {
            System.out.println("Owner name cannot be empty. Please try again.");
            return collectOwnerName();
        }

        return ownerName;
    }

    public String collectWalletName() {
        System.out.print("Enter Wallet name: ");
        String wtName = scanner.next().trim();

        if (wtName.isEmpty()) {
            return "My Crypto Wallet";
        }

        return wtName;
    }

    public String collectPassword() {
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters. Please try again.");
            return collectPassword();
        }

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return collectPassword();
        }

        return password;
    }

    public Currency selectCurrency() {
        System.out.println("\nSelect cryptocurrency:");
        System.out.println("1. BITCOIN");
        System.out.println("2. ETHEREUM");
        System.out.print("Your choice (1 or 2): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    return Currency.BITCOIN;
                case 2:
                    return Currency.ETHEREUM;
                default:
                    System.out.println("Invalid choice. Please select 1 or 2.");
                    return selectCurrency();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return selectCurrency();
        }
    }
}