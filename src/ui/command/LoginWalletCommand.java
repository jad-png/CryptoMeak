package ui.command;

import java.sql.SQLException;
import java.util.Scanner;

import config.DIContainer;
import controller.AuthController;
import service.result.AuthResult;
import ui.interfaces.Command;

public class LoginWalletCommand implements Command {
    private final AuthController authCon;
    private final Scanner scanner;

    public LoginWalletCommand(AuthController authCon, Scanner scanner) {
        this.authCon = authCon;
        this.scanner = new Scanner(System.in);
    }

    public String getName() {
        return "Login";
    }

    public String getDescription() {
        return "Login to your wallet";
    }

    @Override
    public void execute(CommandContext context) {
        System.out.println("\n=== WALLET LOGIN ===");

        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            attempts++;

            String walletAddress = collectWalletAddress();
            if (walletAddress == null)
                continue;

            String password = collectPassword();
            

            if (password == null)
                continue;

            AuthResult result = authCon.login(walletAddress, password);

            if (result.isSuccess()) {
                handleSuccessfulLogin(result, context);
                return;
            } else {
                handleFailedLogin(result, attempts, MAX_ATTEMPTS);
            }
        }

        System.out.println("\nToo many failed attempts. Please try again later.");
    }

    private String collectWalletAddress() {
        System.out.print("Enter wallet address: ");
        String address = scanner.nextLine().trim();

        if (address.isEmpty()) {
            System.out.println("Wallet address cannot be empty");
            return null;
        }

        return address;
    }

    private String collectPassword() {
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty");
            return null;
        }

        return password;
    }

    private void handleSuccessfulLogin(AuthResult result, CommandContext context) {
        System.out.println("\nSuccess " + result.getMessage());
        System.out.println("-Login successful!");
        System.out.println("-Welcome, " + result.getWallet().getOwnerName());
        System.out.println("-Wallet: " + result.getWallet().getAddress());
        System.out.println("-Currency: " + result.getWallet().getCurrency());
        System.out.println("-Balance: " + result.getWallet().getBalance());

        // Stocker dans le contexte
        context.put("currentWallet", result.getWallet());
        context.put("sessionId", result.getSessionId());
        context.put("isAuthenticated", true);
    }

    private void handleFailedLogin(AuthResult result, int attempts, int maxAttempts) {
        System.out.println("\nError " + result.getMessage());

        if (attempts < maxAttempts) {
            System.out.println("Attempt " + attempts + " of " + maxAttempts);
            System.out.println("Please check your credentials and try again");
            System.out.println("---");
        }
    }
}
