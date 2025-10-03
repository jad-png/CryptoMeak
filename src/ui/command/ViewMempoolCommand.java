// ui/command/ViewMempoolCommand.java
package ui.command;

import config.DIContainer;
import controller.MempoolController;
import model.Wallet;
import ui.interfaces.Command;

import java.util.Optional;

public class ViewMempoolCommand implements Command {
    private final MempoolController mempoolController;
    
    public ViewMempoolCommand(MempoolController mempoolController) {
        this.mempoolController = mempoolController;
    }
    
    public String getName() {
        return "view mempool status";
    }
    
    public String getDescription() {
        return "Display all pending transactions in the mempool";
    }
    
    public void execute(CommandContext context) {
        System.out.println("\n" + "====================================================");
        System.out.println("MEMPOOL STATUS");
        System.out.println("=====================================================");
        
        Optional<Wallet> currentWallet = context.get("currentWallet");
        
        try {
            String mempoolDisplay = mempoolController.formatMempoolDisplay(currentWallet);
            System.out.println(mempoolDisplay);
            
            // Show statistics
            String statistics = mempoolController.getMempoolStatistics();
            System.out.println(statistics);
            
        } catch (Exception e) {
            System.out.println("Error retrieving mempool data: " + e.getMessage());
        }
    }
}