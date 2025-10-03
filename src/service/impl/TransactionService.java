package service.impl;

import calculators.IFeeCalculator;
import factories.FeeCalculatorFactory;
import factories.UtilsFactory;
import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;
import model.enums.TxStatus;
import repository.TransactionRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import service.interfaces.ITransactionService;
import utils.TransactionUtils;
import utils.WalletUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionService implements ITransactionService {
    private final ITransactionRepository txRepo;
    private final IWalletRepository wtRepo;
    private final TransactionUtils txUtils;
    private final WalletUtils wtUtils;


    public TransactionService(ITransactionRepository txRepo, IWalletRepository wtRepo, TransactionUtils txUtils, WalletUtils wtUtils) {

        this.txRepo = txRepo;
        this.wtRepo = wtRepo;
        this.txUtils = txUtils;
        this.wtUtils = wtUtils;
    }

    @Override
    public Transaction createTransaction(String srcAddress, String desAddress, BigDecimal amount, TxPriority priority, Currency currency) {

        txUtils.validateTransactionParameters(srcAddress, desAddress, amount, currency);

        wtUtils.validateWalletExist(srcAddress, desAddress);

        wtUtils.validateCurrencyCompatibility(srcAddress, currency);

        BigDecimal fee = calculateFee(amount, priority, currency);
        BigDecimal totalAmount = amount.add(fee);

        wtUtils.validateSufficientBalance(srcAddress, totalAmount);

        txUtils.validateNoPendingTransaction(srcAddress);


        Transaction tx = new Transaction(
                UUID.randomUUID(),
                srcAddress,
                desAddress,
                amount,
                fee,
                TxStatus.PENDING,
                priority,
                currency,
                LocalDateTime.now(),
                null);

        txRepo.save(tx);

        return tx;
    }

    // read methods
    @Override
    public Optional<Transaction> getTransactionById(UUID id) {
        return txRepo.findById(id.toString());
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return txRepo.findAll();
    }
    @Override
    public List<Transaction> getTxsBySrcAddress(String srcAddress) {
        return txRepo.findBySrcAddress(srcAddress);
    }

    public List<Transaction> getTxsByDestAddress(String destAddress) {
        return txRepo.findByDestAddress(destAddress);
    }

    public List<Transaction> getTxsByCurrency(Currency currency) {
        return txRepo.findByCurrency(currency);
    }

    @Override
    public boolean confirmTx(UUID txId) {
        Optional<Transaction> txOpt = txRepo.findById(txId.toString());

        if (txOpt.isPresent()) {
            Transaction tx = txOpt.get();

            if (tx.getStatus() != TxStatus.PENDING) {
                throw new IllegalStateException("Only pending transactions can be confirmed");
            }

            BigDecimal totalAmount = tx.getAmount().add(tx.getFee());

            if (!wtRepo.hasSufficientBalance(tx.getSourceAddress(), totalAmount)) {
                throw new IllegalStateException("Insufficient balance to confirm transaction");
            }

            Transaction confirmedTx = createConfirmedTx(tx);
            txRepo.update(confirmedTx);

            updateWalletBalance(tx);
            return true;
        }
        return false;
    }

    @Override
    public boolean rejectTx(UUID txId) {
        Optional<Transaction> txOpt = txRepo.findById(txId.toString());

        if (txOpt.isPresent()) {
            Transaction tx = txOpt.get();

            if (tx.getStatus() != TxStatus.PENDING) {
                throw new IllegalStateException("Only pending transactions can be rejected");
            }

            Transaction rejectedTx = createRejectedTx(tx);
            txRepo.update(rejectedTx);
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal amount, TxPriority priority, Currency currency) {
        IFeeCalculator calculator = FeeCalculatorFactory.getCalculator(currency);
        return calculator.calculateFee(amount, priority);
    }


    // private utilities methods
    private Transaction createConfirmedTx(Transaction original) {
        return new Transaction(
                original.getId(),
                original.getSourceAddress(),
                original.getDestinationAddress(),
                original.getAmount(),
                original.getFee(),
                TxStatus.CONFIRMED,
                original.getPriority(),
                original.getCurrency(),
                original.getCreatedAt(),
                LocalDateTime.now()
        );
    }

    private Transaction createRejectedTx(Transaction original) {
        return new Transaction(
                original.getId(),
                original.getSourceAddress(),
                original.getDestinationAddress(),
                original.getAmount(),
                original.getFee(),
                TxStatus.REJECTED,
                original.getPriority(),
                original.getCurrency(),
                original.getCreatedAt(),
                null
        );
    }

    private void updateWalletBalance(Transaction tx) {
        try {
            BigDecimal totalDebit = tx.getAmount().add(tx.getFee());
            wtRepo.subtractBalance(tx.getSourceAddress(), totalDebit);

            wtRepo.addBalance(tx.getDestinationAddress(), tx.getAmount());
        } catch (Exception e) {
            Transaction pendingTx = createRejectedTx(tx);
            txRepo.update(pendingTx);
            throw new RuntimeException("Failed to update wallet balance" + e.getMessage());
        }
    }

    public BigDecimal getTotalFees(Currency currency) {
        List<Transaction> txs = txRepo.findByCurrency(currency);
        return txs.stream()
                .map(Transaction::getFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
