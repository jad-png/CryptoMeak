package repository.interfaces;

import model.Transaction;
import model.enums.Currency;
import model.enums.TxStatus;

import java.util.List;

public interface ITransactionRepository extends Repository<Transaction> {
    List<Transaction> findBySrcAddress(String srcAddress);
    List<Transaction> findByDestAddress(String destAddress);
    List<Transaction> findByStatus(TxStatus status);
    List<Transaction> findByCurrency(Currency currency);
    boolean existingPendingTx(String srcAddress);
}
