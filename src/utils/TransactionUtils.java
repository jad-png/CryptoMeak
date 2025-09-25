package utils;

import model.Transaction;
import model.enums.Currency;
import repository.TransactionRepository;
import repository.interfaces.ITransactionRepository;

import java.math.BigDecimal;

public class TransactionUtils {
    private final ITransactionRepository txRepo;

    public TransactionUtils(ITransactionRepository txRepo) {
        this.txRepo = txRepo;
    }

    public void validateTransactionParameters(String srcAddress, String destaddress, BigDecimal amount, Currency currency) {
        if (!AddressValidator.isValidFormat(srcAddress, currency)) {
            throw new IllegalArgumentException("Invalid source address format for currency: " + currency);
        }

        if (!AddressValidator.isValidFormat(destaddress, currency)) {
            throw new IllegalArgumentException("Invalid destination address format for currency: " + currency);
        }

        if (srcAddress.equals(destaddress)) {
            throw new IllegalArgumentException("Source and destination addresses cannot be the same");
        }

        AmountValidator.validate(amount);
    }

    public void validateNoPendingTransaction(String srcAddress) {
        if (txRepo.existingPendingTx(srcAddress)) {
            throw new IllegalArgumentException(
                    "There is already a pending transaction for this source address"
            );
        }
    }
}
