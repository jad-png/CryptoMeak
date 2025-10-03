// ui/command/CompareFeesCommand.java
package ui.command;

import config.DIContainer;
import controller.MempoolController;
import model.Wallet;
import model.enums.Currency;
import ui.interfaces.Command;

import java.util.Optional;
import java.util.Scanner;

public class CompareFeesCommand implements Command {
    private final MempoolController mempoolController;
    private final Scanner scanner = new Scanner(System.in);
    
    public CompareFeesCommand(MempoolController mempoolController) {
        this.mempoolController = mempoolController;
    }
    
    public String getName() {
        return "Compare Fee Levels";
    }
    
    public String getDescription() {
        return "Compare different fee levels and their impact on processing time";
    }
    
    public void execute(CommandContext context) {
        System.out.println("\n" + "===================================================");
        System.out.println("COMPARE FEE LEVELS");
        System.out.println("====================================================");
        
        Optional<Wallet> currentWallet = context.get("currentWallet");
        if (!currentWallet.isPresent()) {
            System.out.println("Error: Please login first to compare fee levels.");
            return;
        }
        
        try {
            Wallet wallet = currentWallet.get();
            double amount = collectAmount();
            
            String comparison = mempoolController.compareFeeLevels(amount, wallet.getCurrency());
            System.out.println(comparison);
            
            // Show recommendation
            showRecommendation();
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
    
    private double collectAmount() {
        System.out.print("Enter transaction amount to compare fees: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number for amount");
        }
    }
    
    private void showRecommendation() {
        System.out.println("\nRECOMMENDATIONS:");
        System.out.println("• ECONOMICAL: Best for non-urgent transactions (savings > 50%)");
        System.out.println("• STANDARD: Good balance of cost and speed (recommended for most cases)");
        System.out.println("• FAST: Use for time-sensitive transactions (urgent transfers)");
        System.out.println("• Your position depends on current mempool congestion");
    }
}