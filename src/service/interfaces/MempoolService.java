package service.interfaces;

import java.math.BigDecimal;
import java.util.List;

import model.Mempool;
import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;

public interface MempoolService {
    Mempool getCurrentMempool();
    List<Transaction> getPendingTxs();
    int getTransactionPosition(String txId);
    int getMempoolSize();
    List<Transaction> getUserPendingTransactions(String walletAddress);
    int estimateWaitTime(String transactionId);
    BigDecimal calculateTotalFees(Currency currency);
    long countTransactionsByPriority(TxPriority priority);
}
