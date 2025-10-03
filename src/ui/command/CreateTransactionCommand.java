package ui.command;

import java.math.BigDecimal;
import java.util.Scanner;

import config.DIContainer;
import controller.TransactionController;
import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;
import ui.interfaces.Command;

public class CreateTransactionCommand implements Command {
    private final TransactionController transCon;
    private final Scanner scanner;

    public CreateTransactionCommand(TransactionController txCon) {
        this.transCon = txCon;
        this.scanner = new Scanner(System.in);
    }

    public String getName() {
        return "create transaction";
    }

    public String getDescription() {
        return "Create a new Transaction";
    }

    public void execute(CommandContext context) {
        System.out.println("\n" + "================================================");
        System.out.println("CREATE NEW TRANSACTION");
        System.out.println("=================================================");

        try {
            // STEP 1: create needed variables
            // init sourceAddress collector
            String sourceAddress = collectSrcAddress();

            // ... dest address
            String destAddress = collectDestAddress();
            // ... amount
            BigDecimal amount = collectAmount();
            // ... currency
            Currency currency = collectCurrency();
            // ... priority
            TxPriority priority = collectPriority();

            // STEP 2: implement displayTrransactionSummary a method displays a summary for
            displayTransactionSummary(sourceAddress, destAddress, amount, currency, priority);

            // STEP 3: consfirm transaction first
            if (confirmTransaction()) {
                // STEP 4: then create transaction inside of condition
                Transaction tx = transCon.createTransaction(sourceAddress, destAddress, amount, priority, currency);

                // STEP 5: display success message
                displaySuccessMessage(tx);
                
                // STEP 6: store trnasaction in context for potential future use
                context.put("LastTransaction", tx);
            } else {
                System.out.println("Transaction cancelled by user.");
            }


        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    public String collectSrcAddress() {
        System.out.println("\n SOURCE WALLET ADDRESS");
        System.out.println("Enter source wallet address: ");
        String address = scanner.nextLine().trim();
        System.out.println(address);
        if (address.isEmpty()) {
            throw new IllegalArgumentException("Source address connt be empty.");
        }

        // if (!isValidAddressFormat(address)) {
        //     throw new IllegalArgumentException("Invalid source address format.");
        // }

        return address;
    }

    public String collectDestAddress() {
        System.out.println("\n DESTINATION WALLET ADDRESS");
        System.out.println("Enter destination wallet address: ");
        String address = scanner.nextLine().trim();

        if (address.isEmpty()) {
            throw new IllegalArgumentException("Destination address connt be empty.");
        }

        // if (!isValidAddressFormat(address)) {
        //     throw new IllegalArgumentException("Invalid destination address format.");
        // }

        return address;
    }

    // implements following methods:
    // collectSrcAddress
    // collectDestAddress
    // collectAmount
    public BigDecimal collectAmount() {
        System.out.println("\nTRANSACTION AMOUNT");
        System.out.print("Enter amount to send: ");

        try {
            String amountInput = scanner.nextLine().trim();

            if (amountInput.isEmpty()) {
                throw new IllegalArgumentException("Amount cannot be empty.");
            }

            BigDecimal amount = new BigDecimal(amountInput);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }

            if (amount.scale() > 8) {
                throw new IllegalArgumentException("Amount cannot have more than 8 decimal places.");
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format.");
        }
    }

    // collectCurrency
    public Currency collectCurrency() {
        System.out.println("\nSELECT CURRENCY");
        System.out.println("Available cryptocurrencies:");

        Currency[] currencies = Currency.values();

        for (int i = 0; i < currencies.length; i++) {
            System.out.printf("%d. %s%n", i + 1, currencies[i]);
        }

        System.out.print("Select currency (1-" + currencies.length + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice < 1 || choice > currencies.length) {
                throw new IllegalArgumentException("Invalid currency selection.");
            }

            return currencies[choice - 1];
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input. Please enter a number corresponding to the currency.");
        }
    }

    // collectPriority
    public TxPriority collectPriority() {
        System.out.println("\nSELECT TRANSACTION PRIORITY");
        System.out.println("Priority levels affect transaction speed and fees:");

        TxPriority[] priorities = TxPriority.values();

        for (int i = 0; i < priorities.length; i++) {
            // implement method that display priority description
            System.out.printf("%d. %s%n", i + 1, priorities[i]);
        }
        System.out.print("Select priority (1-" + priorities.length + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice < 1 || choice > priorities.length) {
                throw new IllegalArgumentException("Invalid priority selection.");
            }

            TxPriority selectedPriority = priorities[choice - 1];

            // display fee estimate

            return selectedPriority;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input. Please enter a number corresponding to the priority.");
        }
    }

    // displayFeeEstimate
    private void displayFeeEstimate(TxPriority priority) {
        String feeInfo = getFeeInformation(priority);
        System.out.println("💡 " + feeInfo);
    }

    // displayTransactionSummary
    private void displayTransactionSummary(String source, String destination,
            BigDecimal amount, Currency currency,
            TxPriority priority) {
        System.out.println("\n" + "============================================================================================================================");
        System.out.println("TRANSACTION SUMMARY");
        System.out.println("============================================================================================================================");

        System.out.printf("From: %s%n", shortenAddress(source));
        System.out.printf("To: %s%n", shortenAddress(destination));
        System.out.printf("Amount: %s %s%n", amount.toPlainString(), currency);
        System.out.printf("Priority: %s%n", priority);
        System.out.printf("Estimated Fee: %s%n", getFeeInformation(priority));
        System.out.println("============================================================================================================================");
    }

    // confirmTransaction
    private boolean confirmTransaction() {
        System.out.print("\nConfirm transaction? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        return confirmation.equals("yes") || confirmation.equals("y");
    }

    // displaySuccessMessage
    private void displaySuccessMessage(Transaction transaction) {
        System.out.println("\nTRANSACTION CREATED SUCCESSFULLY!");
        System.out.println("===========================================================================================================================");
        System.out.printf("Transaction ID: %s%n", transaction.getId());
        System.out.printf("Status: %s%n", transaction.getStatus());
        System.out.printf("Amount: %s %s%n", transaction.getAmount().toPlainString(), transaction.getCurrency());
        System.out.printf("Fee: %s %s%n", transaction.getFee().toPlainString(), transaction.getCurrency());
        System.out.printf("Total: %s %s%n", transaction.getTotalAmount().toPlainString(), transaction.getCurrency());
        System.out.printf("Priority: %s%n", transaction.getPriority());
        System.out.printf("Created: %s%n", transaction.getCreatedAt());
        System.out.println("============================================================================================================================");
        System.out.println(" Your transaction is now pending confirmation.");
    }

    // isValidAddressFormat
    private boolean isValidAddressFormat(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        // Basic crypto address validation
        // Bitcoin addresses start with 1, 3, or bc1 and are alphanumeric
        // Ethereum addresses start with 0x and are 42 characters hex
        return address.matches("^(1|3|bc1)[a-zA-Z0-9]{25,34}$") ||
                address.matches("^0x[a-fA-F0-9]{40}$");
    }

    // shortenAddress/
    private String shortenAddress(String address) {
        if (address == null || address.length() <= 16) {
            return address;
        }
        return address.substring(0, 8) + "..." + address.substring(address.length() - 8);
    }
    // getPriorityDescription

    private String getPriorityDescription(TxPriority priority) {
        switch (priority) {
            case ECONOMY:
                return "Lower fees, slower confirmation (10-30 minutes)";
            case STANDARD:
                return "Moderate fees, normal confirmation (5-15 minutes)";
            case FAST:
                return "Higher fees, faster confirmation (1-5 minutes)";
            default:
                return "Standard processing";
        }
    }

    // getFeeInformation
    private String getFeeInformation(TxPriority priority) {
        switch (priority) {
            case ECONOMY:
                return "Low fee (economy rate)";
            case STANDARD:
                return "Standard fee (normal rate)";
            case FAST:
                return "High fee (priority rate)";
            default:
                return "Standard fee";
        }
    }
}