// controller/MempoolController.java
package controller;

import model.Mempool;
import model.Transaction;
import model.Wallet;
import model.enums.Currency;
import model.enums.TxPriority;
import service.interfaces.MempoolService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class MempoolController {
    private final MempoolService mempoolService;
    
    public MempoolController(MempoolService mempoolService) {
        this.mempoolService = mempoolService;
    }
    
    // Get current mempool state
    public Mempool getCurrentMempool() {
        return mempoolService.getCurrentMempool();
    }
    
    // Get all pending transactions sorted by fees
    public List<Transaction> getAllPendingTransactions() {
        return mempoolService.getPendingTxs();
    }
    
    // Get user's pending transactions
    public List<Transaction> getUserPendingTransactions(Wallet wallet) {
        return mempoolService.getUserPendingTransactions(wallet.getAddress());
    }
    
    public List<Transaction> getUserPendingTransactions(String walletAddress) {
        return mempoolService.getUserPendingTransactions(walletAddress);
    }
    
    // Get transaction position in mempool
    public int getTransactionPosition(String transactionId) {
        return mempoolService.getTransactionPosition(transactionId);
    }
    
    // Get mempool size
    public int getMempoolSize() {
        return mempoolService.getMempoolSize();
    }
    
    // Estimate wait time for transaction
    public int estimateWaitTime(String transactionId) {
        return mempoolService.estimateWaitTime(transactionId);
    }
    
    // Calculate total fees for a specific currency
    public BigDecimal getTotalFeesByCurrency(Currency currency) {
        return mempoolService.calculateTotalFees(currency);
    }
    
    // Count transactions by priority level
    public long getTransactionsCountByPriority(TxPriority priority) {
        return mempoolService.countTransactionsByPriority(priority);
    }
    
    // Get comprehensive position info
    public String getPositionInfo(String transactionId) {
        int position = getTransactionPosition(transactionId);
        int totalTransactions = getMempoolSize();
        int waitTime = estimateWaitTime(transactionId);
        
        return String.format("Position: %d/%d | Estimated wait: %d minutes", 
                           position, totalTransactions, waitTime);
    }
    
    // Format mempool for display
    public String formatMempoolDisplay(Optional<Wallet> currentWallet) {
        Mempool mempool = getCurrentMempool();
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n=== MEMPOOL STATUS ===\n");
        sb.append("Pending transactions: ").append(mempoolService.getMempoolSize()).append("\n\n");
        
        if (mempool.getPendingTxs().isEmpty()) {
            sb.append("No pending transactions in mempool.\n");
            return sb.toString();
        }
        
        // ASCII Table Header
        sb.append("┌──────────────────────────────────┬──────────┬────────────┬──────────┐\n");
        sb.append("│ Transaction ID                   │ Amount   │ Fees       │ Priority │\n");
        sb.append("├──────────────────────────────────┼──────────┼──────────┼────────────┤\n");
        
        mempool.getPendingTxs().forEach(tx -> {
            String txIdDisplay = formatTransactionId(tx, currentWallet);
            String amountDisplay = String.format("%.6f", tx.getAmount());
            String feesDisplay = String.format("%.6f", tx.getFee());
            String priorityDisplay = tx.getPriority().toString();
            
            sb.append(String.format("│ %-32s │ %-8s │ %-8s │ %-10s │\n", 
                txIdDisplay, amountDisplay, feesDisplay, priorityDisplay));
        });
        
        sb.append("└──────────────────────────────────┴──────────┴──────────┴────────────┘\n");
        
        return sb.toString();
    }
    
    // Compare fee levels for a specific amount and currency
    public String compareFeeLevels(double amount, Currency currency) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nFEE COMPARISON FOR ").append(amount).append(" ").append(currency).append("\n");
        sb.append("═════════════════════════════════════════════════════════════").append("\n");
        
        sb.append("┌──────────────┬──────────────┬────────────┬────────────────┐\n");
        sb.append("│ Priority     │ Estimated Fee│ Position   │ Wait Time      │\n");
        sb.append("├──────────────┼──────────────┼────────────┼────────────────┤\n");
        
        for (TxPriority priority : TxPriority.values()) {
            BigDecimal estimatedFee = estimateFeeForPriority(amount, priority, currency);
            int estimatedPosition = estimatePositionForFee(estimatedFee);
            int waitTime = estimatedPosition * 10;
            
            sb.append(String.format("│ %-12s │ %-12.6f │ %-10d │ %-14d │\n",
                priority, estimatedFee, estimatedPosition, waitTime));
        }
        
        sb.append("└──────────────┴──────────────┴────────────┴────────────────┘\n");
        
        return sb.toString();
    }
    
    // Get mempool statistics
    public String getMempoolStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nMEMPOOL STATISTICS\n");
        sb.append("────────────────────────────────────────────────────────────────────────").append("\n");
        
        int totalTxs = getMempoolSize();
        BigDecimal totalBtcFees = getTotalFeesByCurrency(Currency.BITCOIN);
        BigDecimal totalEthFees = getTotalFeesByCurrency(Currency.ETHEREUM);
        
        long economicalCount = getTransactionsCountByPriority(TxPriority.ECONOMY);
        long standardCount = getTransactionsCountByPriority(TxPriority.STANDARD);
        long fastCount = getTransactionsCountByPriority(TxPriority.FAST);
        
        sb.append("Total transactions: ").append(totalTxs).append("\n");
        sb.append("BTC total fees: ").append(totalBtcFees).append("\n");
        sb.append("ETH total fees: ").append(totalEthFees).append("\n");
        sb.append("Priority distribution:\n");
        sb.append("  • ECONOMICAL: ").append(economicalCount).append(" transactions\n");
        sb.append("  • STANDARD: ").append(standardCount).append(" transactions\n");
        sb.append("  • FAST: ").append(fastCount).append(" transactions\n");
        sb.append("────────────────────────────────────────────────────────────────────────").append("\n");
        
        return sb.toString();
    }
    
    // Private helper methods
    private String formatTransactionId(Transaction tx, Optional<Wallet> currentWallet) {
        String shortId = tx.getId().toString().substring(0, 8) + "...";
        
        if (currentWallet.isPresent() && tx.getSourceAddress().equals(currentWallet.get().getAddress())) {
            return ">>> " + shortId;
        }
        
        return shortId;
    }
    
    private BigDecimal estimateFeeForPriority(double amount, TxPriority priority, Currency currency) {
        // Simplified fee estimation - in real app, use your FeeCalculatorFactory
        BigDecimal bdAmount = BigDecimal.valueOf(amount);
        switch (priority) {
            case ECONOMY: return bdAmount.multiply(new BigDecimal("0.01"));
            case STANDARD: return bdAmount.multiply(new BigDecimal("0.02"));
            case FAST: return bdAmount.multiply(new BigDecimal("0.05"));
            default: return bdAmount.multiply(new BigDecimal("0.02"));
        }
    }
    
    private int estimatePositionForFee(BigDecimal fee) {
        List<Transaction> pendingTxs = getAllPendingTransactions();
        
        for (int i = 0; i < pendingTxs.size(); i++) {
            if (fee.compareTo(pendingTxs.get(i).getFee()) > 0) {
                return i + 1;
            }
        }
        return pendingTxs.size() + 1;
    }
}