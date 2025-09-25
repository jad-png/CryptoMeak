package calculators;

import model.enums.TxPriority;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BitcoinFeeCalculator implements IFeeCalculator{
    private static final BitcoinFeeCalculator INSTANCE = new BitcoinFeeCalculator();

    private static final BigDecimal BASE_FEE = new BigDecimal("0.0001");
    private static final BigDecimal ECONOMIQUE_MULTIPLIER = new BigDecimal("0,5");
    private static final BigDecimal STANDARD_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal RAPIDE_MULTIPLIER = new BigDecimal("2.0");

    private BitcoinFeeCalculator() {

    }

    public static BitcoinFeeCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal amount, TxPriority priority) {
        BigDecimal multiplier = getPriorityMultiplier(priority);
        BigDecimal fee = BASE_FEE.multiply(multiplier);

        BigDecimal percentageFee = amount.multiply(new BigDecimal(0.0001));
        fee = fee.add(percentageFee);
        return fee.setScale(8, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getBaseFee() {
        return BASE_FEE;
    }

    @Override
    public BigDecimal getPriorityMultiplier(TxPriority priority) {
        switch (priority) {
            case ECONOMY:
                return ECONOMIQUE_MULTIPLIER;
            case STANDARD:
                return STANDARD_MULTIPLIER;
            case FAST:
                return RAPIDE_MULTIPLIER;
            default:
                return STANDARD_MULTIPLIER;
        }
    }
}
