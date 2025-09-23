package service.interfase;

import java.util.List;
import java.util.Optional;

import db.model.Mempool;
import db.model.Transaction;

public interface IMempoolService {
    Mempool getCurrentMempool();
    void addTransactionToMempool(Transaction transaction);
    boolean removeTransactionFromMempool(String transactionId);
    int calculateTransactionPosition(Transaction transaction);
    int estimateWaitTime(int position);
    List<Transaction> getMempoolTransactionsSortedByFee();
    Optional<Transaction> findTransactionInMempool(String transactionId);
    void generateRandomMempoolTransactions(int count);
//    MempoolSnapshot getMempoolSnapshot();
    void processMempoolBatch(int batchSize);
}