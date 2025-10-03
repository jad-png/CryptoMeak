package ui.command;

import controller.TransactionController;
import model.Transaction;
import model.Wallet;
import model.enums.Currency;
import model.enums.TxStatus;
import ui.interfaces.Command;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class ViewTransactionCommand implements Command {
    private final TransactionController transCon;
    private final Scanner scanner;

    public ViewTransactionCommand(TransactionController transCon) {
        this.transCon = transCon;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getName() {
        return "view transactions";
    }

    @Override
    public String getDescription() {
        return "View transaction history and details";
    }

    @Override
    public void execute(CommandContext context) {
        System.out.println("\n" + "=============================================================");
        System.out.println("VIEW TRANSACTIONS");
        System.out.println("=============================================================");

        try {
            // Display view options
            int choice = displayViewOptions();

            switch (choice) {
                case 1:
                    viewAllTransactions();
                    break;
                case 2:
                    viewMyTransactions(context);
                    break;
                case 3:
                    viewTransactionById();
                    break;
                // case 4:
                //     viewPendingTransactions();
                //     break;
                case 4:
                    viewTransactionsByCurrency();
                    break;
                default:
                    System.out.println("Invalid selection.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private int displayViewOptions() {
        System.out.println("\nSELECT VIEW OPTION:");
        System.out.println("1. View All Transactions");
        System.out.println("2. View My Transactions (requires login)");
        System.out.println("3. View Transaction by ID");
        System.out.println("4. View Pending Transactions");
        System.out.println("5. View Transactions by Currency");
        System.out.print("Select option (1-5): ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number (1-5).");
        }
    }

    private void viewAllTransactions() {
        System.out.println("\n" + "=============================================================");
        System.out.println("ALL TRANSACTIONS");
        System.out.println("=============================================================");

        List<Transaction> transactions = transCon.getAllTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions found in the system.");
            return;
        }

        displayTransactionTable(transactions, "All Transactions");
        displayTransactionStats(transactions);
    }

    private void viewMyTransactions(CommandContext context) {
        if (!isAuthenticated(context)) {
            System.out.println("Please login first to view your transactions.");
            return;
        }

        String sessionId = context.get("sessionId");
        Optional<Wallet> walletOpt = transCon.getAuthenticatedWallet(sessionId);

        // if (walletOpt.isEmpty()) {
        //     System.out.println("No wallet found for current session.");
        //     return;
        // }

        Wallet wallet = walletOpt.get();
        String walletAddress = wallet.getAddress();

        System.out.println("\n" + "=============================================================");
        System.out.println("MY TRANSACTIONS - " + shortenAddress(walletAddress));
        System.out.println("=============================================================");

        // Get sent transactions
        List<Transaction> sentTransactions = transCon.getTxsBySrcAddress(walletAddress);

        // Get received transactions
        List<Transaction> receivedTransactions = transCon.getTxsByDesAddress(walletAddress);

        if (sentTransactions.isEmpty() && receivedTransactions.isEmpty()) {
            System.out.println("No transactions found for your wallet.");
            return;
        }

        // Display sent transactions
        if (!sentTransactions.isEmpty()) {
            System.out.println("\nSENT TRANSACTIONS:");
            displayTransactionTable(sentTransactions, "Sent");
        }

        // Display received transactions
        if (!receivedTransactions.isEmpty()) {
            System.out.println("\nRECEIVED TRANSACTIONS:");
            displayTransactionTable(receivedTransactions, "Received");
        }

        displayPersonalTransactionStats(sentTransactions, receivedTransactions, wallet);
    }

    private void viewTransactionById() {
        System.out.println("\nVIEW TRANSACTION BY ID");
        System.out.print("Enter Transaction ID: ");
        String transactionId = scanner.nextLine().trim();

        if (transactionId.isEmpty()) {
            System.out.println("Transaction ID cannot be empty.");
            return;
        }

        try {
            UUID id = UUID.fromString(transactionId);
            Optional<Transaction> transactionOpt = transCon.getTransactionById(id);

            if (transactionOpt.isPresent()) {
                displayTransactionDetails(transactionOpt.get());
            } else {
                System.out.println("Transaction not found with ID: " + transactionId);
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Transaction ID format.");
        }
    }

    // private void viewPendingTransactions() {
    //     System.out.println("\n" + "=============================================================");
    //     System.out.println("PENDING TRANSACTIONS");
    //     System.out.println("=============================================================");

    //     List<Transaction> pendingTransactions = transCon.getPendingTxs();

    //     if (pendingTransactions.isEmpty()) {
    //         System.out.println("No pending transactions found.");
    //         return;
    //     }

    //     displayTransactionTable(pendingTransactions, "Pending");
    //     System.out.printf("\nTotal Pending Transactions: %d%n", pendingTransactions.size());
    // }

    private void viewTransactionsByCurrency() {
        System.out.println("\nVIEW TRANSACTIONS BY CURRENCY");

        Currency currency = selectCurrency();
        if (currency == null)
            return;

        System.out.println("\n" + "============================================================");
        System.out.println("Currency: " + currency + " TRANSACTIONS");
        System.out.println("=============================================================");

        List<Transaction> currencyTransactions = transCon.getTxsByCurrency(currency);

        if (currencyTransactions.isEmpty()) {
            System.out.println("No transactions found for " + currency);
            return;
        }

        displayTransactionTable(currencyTransactions, currency.toString());

        // Display currency-specific stats
        BigDecimal totalFees = transCon.getTotalFees(currency);
        System.out.printf("\nTotal Fees for %s: %s %s%n",
                currency, totalFees.toPlainString(), currency);
    }

    private Currency selectCurrency() {
        System.out.println("Available currencies:");
        Currency[] currencies = Currency.values();
        for (int i = 0; i < currencies.length; i++) {
            System.out.printf("%d. %s%n", i + 1, currencies[i]);
        }

        System.out.print("Select currency (1-" + currencies.length + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= currencies.length) {
                return currencies[choice - 1];
            } else {
                System.out.println("Invalid currency selection.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    private void displayTransactionTable(List<Transaction> transactions, String title) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        // Table header
        System.out
                .println("┌──────────────────────────────────┬──────────┬──────────┬────────────┬──────────┬────────┐");
        System.out
                .println("│ ID                               │ From     │ To       │ Amount     │ Fee      │ Status │");
        System.out
                .println("├──────────────────────────────────┼──────────┼──────────┼────────────┼──────────┼────────┤");

        // Table rows
        for (Transaction tx : transactions) {
            String shortId = tx.getId().toString().substring(0, 8) + "...";
            String from = shortenAddress(tx.getSourceAddress());
            String to = shortenAddress(tx.getDestinationAddress());
            String amount = String.format("%.4f", tx.getAmount().doubleValue());
            String fee = String.format("%.4f", tx.getFee().doubleValue());
            String status = tx.getStatus().name() + " " + tx.getStatus().toString();

            System.out.printf("│ %-32s │ %-8s │ %-8s │ %-10s │ %-8s │ %-6s │%n",
                    shortId, from, to, amount, fee, status);
        }

        // Table footer
        System.out
                .println("└──────────────────────────────────┴──────────┴──────────┴────────────┴──────────┴────────┘");

        System.out.printf("Total: %d transactions%n", transactions.size());
    }

    private void displayTransactionDetails(Transaction transaction) {
        System.out.println("\n" + "============================================");
        System.out.println("TRANSACTION DETAILS");
        System.out.println("===========================================");

        System.out.printf("Transaction ID: %s%n", transaction.getId());
        System.out.printf("Status: %s %s%n", transaction.getStatus().name(), transaction.getStatus());
        System.out.printf("From: %s%n", transaction.getSourceAddress());
        System.out.printf("To: %s%n", transaction.getDestinationAddress());
        System.out.printf("Amount: %s %s%n", transaction.getAmount().toPlainString(), transaction.getCurrency());
        System.out.printf("Fee: %s %s%n", transaction.getFee().toPlainString(), transaction.getCurrency());
        System.out.printf("Total: %s %s%n", transaction.getTotalAmount().toPlainString(), transaction.getCurrency());
        System.out.printf("Priority: %s%n", transaction.getPriority());
        System.out.printf("Currency: %s%n", transaction.getCurrency());
        System.out.printf("Created: %s%n", transaction.getCreatedAt());

        if (transaction.getConfirmedAt() != null) {
            System.out.printf("Confirmed: %s%n", transaction.getConfirmedAt());
        }

        System.out.println("=============================================================");
    }

    private void displayTransactionStats(List<Transaction> transactions) {
        if (transactions.isEmpty())
            return;

        long totalCount = transactions.size();
        long pendingCount = transactions.stream()
                .filter(tx -> tx.getStatus() == TxStatus.PENDING)
                .count();
        long confirmedCount = transactions.stream()
                .filter(tx -> tx.getStatus() == TxStatus.CONFIRMED)
                .count();
        long rejectedCount = transactions.stream()
                .filter(tx -> tx.getStatus() == TxStatus.REJECTED)
                .count();

        System.out.println("\nTRANSACTION STATISTICS:");
        System.out.printf("• Total Transactions: %d%n", totalCount);
        System.out.printf("• Pending: %d (%.1f%%)%n", pendingCount, (pendingCount * 100.0 / totalCount));
        System.out.printf("• Confirmed: %d (%.1f%%)%n", confirmedCount, (confirmedCount * 100.0 / totalCount));
        System.out.printf("• Rejected: %d (%.1f%%)%n", rejectedCount, (rejectedCount * 100.0 / totalCount));
    }

    private void displayPersonalTransactionStats(List<Transaction> sent, List<Transaction> received, Wallet wallet) {
        int totalSent = sent.size();
        int totalReceived = received.size();

        System.out.println("\nPERSONAL TRANSACTION STATISTICS:");
        System.out.printf("• Wallet: %s%n", shortenAddress(wallet.getAddress()));
        System.out.printf("• Currency: %s%n", wallet.getCurrency());
        System.out.printf("• Transactions Sent: %d%n", totalSent);
        System.out.printf("• Transactions Received: %d%n", totalReceived);
        System.out.printf("• Total Activity: %d transactions%n", totalSent + totalReceived);

        if (totalSent > 0) {
            BigDecimal totalSentAmount = sent.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.printf("• Total Sent: %s %s%n", totalSentAmount.toPlainString(), wallet.getCurrency());
        }
    }

    // Utility methods
    private boolean isAuthenticated(CommandContext context) {
        String sessionId = context.get("sessionId");
        return sessionId != null && transCon.isAuthenticated(sessionId);
    }

    private String shortenAddress(String address) {
        if (address == null || address.length() <= 16) {
            return address;
        }
        return address.substring(0, 8) + "..." + address.substring(address.length() - 8);
    }

    private boolean isDebugMode() {
        return System.getProperty("debug", "false").equalsIgnoreCase("true");
    }
}