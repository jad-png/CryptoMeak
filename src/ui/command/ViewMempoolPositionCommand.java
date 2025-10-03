// ui/command/ViewMempoolPositionCommand.java
package ui.command;

import config.DIContainer;
import controller.MempoolController;
import model.Transaction;
import model.Wallet;
import ui.interfaces.Command;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewMempoolPositionCommand implements Command {
    private final MempoolController mempoolController;
    private final Scanner scanner = new Scanner(System.in);
    
    public ViewMempoolPositionCommand(MempoolController mempoolController) {
        this.mempoolController = mempoolController;
    }
    
    public String getName() {
        return "view my position in mempool";
    }
    
    public String getDescription() {
        return "Check your transaction position and estimated wait time";
    }
    
    public void execute(CommandContext context) {
        System.out.println("\n" + "=======================================================");
        System.out.println("VIEW POSITION IN MEMPOOL");
        System.out.println("========================================================");
        
        Optional<Wallet> currentWallet = context.get("currentWallet");
        if (!currentWallet.isPresent()) {
            System.out.println("Error: Please login first to view your position.");
            return;
        }
        
        try {
            Wallet wallet = currentWallet.get();
            List<Transaction> userTxs = mempoolController.getUserPendingTransactions(wallet);
            
            if (userTxs.isEmpty()) {
                System.out.println("No pending transactions found for your wallet.");
                return;
            }
            
            displayUserTransactions(userTxs);
            
            if (userTxs.size() == 1) {
                showTransactionPosition(userTxs.get(0));
            } else {
                Transaction selectedTx = selectTransaction(userTxs);
                if (selectedTx != null) {
                    showTransactionPosition(selectedTx);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void displayUserTransactions(List<Transaction> transactions) {
        System.out.println("\nYour pending transactions:");
        System.out.println("┌───┬──────────────────┬──────────┬──────────┬────────────┐");
        System.out.println("│ # │ Transaction ID   │ Amount   │ Fees     │ Priority    │");
        System.out.println("├───┼──────────────────┼──────────┼──────────┼────────────┤");
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction tx = transactions.get(i);
            String shortId = tx.getId().toString().substring(0, 8) + "...";
            System.out.printf("│ %d │ %-16s │ %-8.4f │ %-8.4f │ %-10s │%n",
                i + 1, shortId, tx.getAmount(), tx.getFee(), tx.getPriority());
        }
        System.out.println("└───┴──────────────────┴──────────┴──────────┴────────────┘");
    }
    
    private Transaction selectTransaction(List<Transaction> transactions) {
        System.out.print("\nSelect transaction to check position (1-" + transactions.size() + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice < 1 || choice > transactions.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            
            return transactions.get(choice - 1);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }
    
    private void showTransactionPosition(Transaction transaction) {
        String positionInfo = mempoolController.getPositionInfo(transaction.getId().toString());
        
        System.out.println("\n📊 TRANSACTION POSITION ANALYSIS");
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.printf("Transaction: %s...%n", transaction.getId().toString().substring(0, 12));
        System.out.println(positionInfo);
        System.out.printf("Fees paid: %.6f%n", transaction.getFee());
        System.out.printf("Priority: %s%n", transaction.getPriority());
        System.out.println("──────────────────────────────────────────────────────────────");
        
        // Provide insights
        int position = mempoolController.getTransactionPosition(transaction.getId().toString());
        if (position <= 3) {
            System.out.println("Excellent! Your transaction is in top 3!");
        } else if (position <= 10) {
            System.out.println("Good position! Should be processed soon.");
        } else if (position <= 20) {
            System.out.println("Moderate wait expected.");
        } else {
            System.out.println("Long wait expected. Consider higher fees for next time.");
        }
    }
}