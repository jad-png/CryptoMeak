package calculators;

import model.enums.TxPriority;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EthereumCalculator implements IFeeCalculator {
    private static final EthereumCalculator INSTANCE = new EthereumCalculator();

    private static final BigDecimal BASE_FEE_GWEI = new BigDecimal("20"); // 20 Gwei
    private static final BigDecimal GWEI_TO_ETH = new BigDecimal("0.000000001");
    private static final BigDecimal GAS_LIMIT = new BigDecimal("21000"); // Gas limit standard
    private static final BigDecimal ECONOMIQUE_MULTIPLIER = new BigDecimal("0.8");
    private static final BigDecimal STANDARD_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal RAPIDE_MULTIPLIER = new BigDecimal("1.5");

    private EthereumCalculator() {
    }

    public static EthereumCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal amount, TxPriority priority) {
        BigDecimal multiplier = getPriorityMultiplier(priority);
        BigDecimal gasPriceGwei = BASE_FEE_GWEI.multiply(multiplier);

        // Calcul: gasPrice (en Gwei) * gasLimit * conversion Gwei→ETH
        BigDecimal feeInGwei = gasPriceGwei.multiply(GAS_LIMIT);
        BigDecimal feeInEth = feeInGwei.multiply(GWEI_TO_ETH);

        return feeInEth.setScale(18, RoundingMode.HALF_UP);    }

    @Override
    public BigDecimal getBaseFee() {
        return BASE_FEE_GWEI.multiply(GAS_LIMIT).multiply(GWEI_TO_ETH);
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
        }    }
}
