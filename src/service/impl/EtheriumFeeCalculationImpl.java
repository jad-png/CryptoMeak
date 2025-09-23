package service.impl;

import db.model.FeePriority;
import db.model.Wallet;
import db.model.CryptoType;

public class EtheriumFeeCalculationImpl implements CalculationFee {
    
    private static final int STANDARD_GAS_LIMIT = 21000;
    
    public double calculateFee(double amount, FeePriority priority, Wallet wallet) {
        // Validate wallet type
        if (wallet.getType() != CryptoType.ETHERIUM) {
            throw new IllegalArgumentException("Wallet must be Ethereum type");
        }
        
        int gasPrice = getGasPrice(priority);
        int gasLimit = STANDARD_GAS_LIMIT;
        
        double feeInGwei = gasLimit * gasPrice;
        double feeInEth = feeInGwei * 1e-9; 
        
        return feeInEth;
    }
    
    private int getGasPrice(FeePriority priority) {
        switch (priority) {
            case ECONOMY: return 5;   // 5 gwei
            case STANDARD: return 10;  // 10 gwei
            case FAST:    return 15;  // 15 gwei
            default: return 10; // Default to standard
        }
    }
}