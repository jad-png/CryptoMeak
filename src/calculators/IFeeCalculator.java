package calculators;

import model.enums.TxPriority;

import java.math.BigDecimal;

public interface IFeeCalculator {
    BigDecimal calculateFee(BigDecimal amount, TxPriority priority);
    BigDecimal getBaseFee();
    BigDecimal getPriorityMultiplier(TxPriority priority);
}
