package service.interfaces;

import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITransactionService {
    Transaction createTransaction(Transaction tx);
    Optional<Transaction> getTransactionById(UUID id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTxsBySrcAddress(String srcAddress);
    boolean confirmTx(UUID txId);
    boolean rejectTx(UUID txId);
    BigDecimal calculateFee(BigDecimal amount, TxPriority priority, Currency currency);
    public BigDecimal getTotalFees(Currency currency);
    }
