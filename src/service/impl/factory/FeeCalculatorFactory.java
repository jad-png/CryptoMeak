package service.impl.factory;

import db.model.CryptoType;
import db.model.Wallet;
import service.impl.*;

public class FeeCalculatorFactory {
	
	public static CalculationFee getCalculator(CryptoType cryptoType) {
        if (cryptoType == null) {
            throw new IllegalArgumentException("CryptoType cannot be null");
        }
        
        switch (cryptoType) {
            case BITCOIN:
                return new BitcoinFeeCalculationImpl();
            case ETHERIUM:
                return new EtheriumFeeCalculationImpl();
            default:
                throw new IllegalArgumentException("Unsupported cryptocurrency type: " + cryptoType);
        }
    }
	
	 public static CalculationFee getCalculator(Wallet wallet) {
	        if (wallet == null) {
	            throw new IllegalArgumentException("Wallet cannot be null");
	        }
	        return getCalculator(wallet.getType());
	    }
}
