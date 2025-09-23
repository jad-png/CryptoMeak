package service.implementation;

import db.model.CryptoType;
import db.model.FeePriority;
import db.model.Wallet;

public class BitcoinFeeCalculationImpl implements CalculationFee {
	private static final int STANDARD_TX_SIZE = 250; // bytes
	
	@Override
	public double calculateFee(double amount, FeePriority priority, Wallet wallet) {
		if (wallet.getType() != CryptoType.BITCOIN) {
            throw new IllegalArgumentException("Wallet must be Bitcoin type");
        }
		
		int satoshiPerByte = getSatoshiPerByte(priority);
		int txSize = STANDARD_TX_SIZE;
		
		long  totalSatoshi = (long) txSize * satoshiPerByte;
		double feeInBtc = totalSatoshi * 1e-8;
		
		return feeInBtc;
	}
	
	private int getSatoshiPerByte(FeePriority priority) {
        switch (priority) {
            case ECONOMY: return 10; 
            case STANDARD: return 20; 
            case FAST:    return 40;  
            default: return 20;
        }
    }
}
