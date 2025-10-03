package controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import config.DIContainer;
import model.Transaction;
import model.Wallet;
import model.enums.Currency;
import model.enums.TxPriority;
import model.enums.TxStatus;
import service.impl.AuthServiceImpl;
import service.impl.TransactionService;

public class TransactionController {
    private final TransactionService txSer;
    private final AuthServiceImpl authSer;

    public TransactionController(TransactionService txSer, AuthServiceImpl authSer) {
            this.txSer = txSer;
            this.authSer = authSer;
    }


    // TODO: add method that show menu
    public Transaction createTransaction(String srcAddress, String destAddress, BigDecimal amount, TxPriority priority, Currency currency) {
        UUID id = UUID.randomUUID();
        BigDecimal fee = txSer.calculateFee(amount, priority, currency);
        TxStatus status = TxStatus.PENDING;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt;
        Transaction tx = new Transaction(id, srcAddress, destAddress, amount, fee, status, priority, currency, createdAt, updatedAt);
        return txSer.createTransaction(tx);
    }

    // retrieve data methods
    public Optional<Transaction> getTransactionById(UUID id) {
        return txSer.getTransactionById(id);
    }

    public List<Transaction> getAllTransactions() {
        return txSer.getAllTransactions();
    }

    public List<Transaction> getTxsBySrcAddress(String srcAddress) {
        return txSer.getTxsBySrcAddress(srcAddress);
    }

    public List<Transaction> getTxsByDesAddress(String desAddress) {
        return txSer.getTxsByDestAddress(desAddress);
    }

    public List<Transaction> getTxsByCurrency(Currency currency) {
        return txSer.getTxsByCurrency(currency);
    }

    // state management methods 
    public boolean confirmTxs(UUID txId) {
        return txSer.confirmTx(txId);
    }

    public boolean rejectTxs(UUID txId) {
        return txSer.rejectTx(txId);
    }

    // Calculation methods
    public BigDecimal calculateFee(BigDecimal amount, TxPriority priority, Currency currency) {
        return txSer.calculateFee(amount, priority, currency);
    }

    public BigDecimal getTotalFees(Currency currency) {
        return txSer.getTotalFees(currency);
    }

    // auth methodes
    public boolean isAuthenticated(String sessionId) {
        return authSer.isAuthenticated(sessionId);
    }

    public Optional<Wallet> getAuthenticatedWallet(String sessionId) {
        return authSer.getAuthenticatedWallet(sessionId);
    } 

    public List<Transaction> generateRandomTransactions(int c) {
        return txSer.generateRandomTransactions(c);
    }
}


