package factories;

import calculators.BitcoinFeeCalculator;
import calculators.EthereumCalculator;
import calculators.IFeeCalculator;
import model.enums.Currency;

public class FeeCalculatorFactory {
    public static IFeeCalculator getCalculator(Currency currency) {
        switch (currency) {
            case BITCOIN: return BitcoinFeeCalculator.getInstance();
            case ETHEREUM: return EthereumCalculator.getInstance();
            default: throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }
}
