// service/impl/MempoolServiceImpl.java
package service.impl;

import model.Mempool;
import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;
import repository.interfaces.ITransactionRepository;
import service.interfaces.MempoolService;

import java.math.BigDecimal;
import java.util.List;

public class MempoolServiceImpl implements MempoolService {
    private final ITransactionRepository txRepo;
    
    public MempoolServiceImpl(ITransactionRepository txRepo) {
        this.txRepo = txRepo;
    }
    
    @Override
    public Mempool getCurrentMempool() {
        List<Transaction> pendingTxs = txRepo.findPendingTransactionsSortedByFees();
        return new Mempool(pendingTxs);
    }
    
    @Override
    public List<Transaction> getPendingTxs() {
        return txRepo.findPendingTransactionsSortedByFees();
    }
    
    @Override
    public int getTransactionPosition(String txId) {
        return txRepo.getTransactionPosition(txId);
    }
    
    @Override
    public int getMempoolSize() {
        return txRepo.countPendingTransactions();
    }
    
    @Override
    public List<Transaction> getUserPendingTransactions(String walletAddress) {
        return txRepo.findPendingTransactionsByAddress(walletAddress);
    }
    
    @Override
    public int estimateWaitTime(String transactionId) {
        int position = getTransactionPosition(transactionId);
        return position * 10;
    }
    
    @Override
    public BigDecimal calculateTotalFees(Currency currency) {
        List<Transaction> pendingTxs = getPendingTxs();
        return pendingTxs.stream()
            .filter(tx -> tx.getCurrency() == currency)
            .map(Transaction::getFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public long countTransactionsByPriority(TxPriority priority) {
        List<Transaction> pendingTxs = getPendingTxs();
        return pendingTxs.stream()
            .filter(tx -> tx.getPriority() == priority)
            .count();
    }
}