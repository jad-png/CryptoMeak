package service.interfase;

import java.util.List;
import java.util.Optional;

import db.model.FeePriority;
import db.model.Transaction;
import db.model.TxStatus;
import db.model.Wallet;

public interface ITransactionService {
    Transaction createTransaction(Wallet sourceWallet, String destinationAddress, 
                                double amount, FeePriority priority);
    Optional<Transaction> findTransactionById(String id);
    List<Transaction> findTransactionsByWallet(Wallet wallet);
    List<Transaction> findTransactionsByStatus(TxStatus status);
    boolean updateTransactionStatus(String transactionId, TxStatus newStatus);
    double calculateTransactionFee(Wallet wallet, double amount, FeePriority priority);
    List<FeePriority> compareFeeLevels(Wallet wallet, double amount);
    boolean validateTransaction(Transaction transaction);
    List<Transaction> generateRandomTransactions(int count);
}
