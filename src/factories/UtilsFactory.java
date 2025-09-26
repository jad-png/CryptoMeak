package factories;

import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import utils.PasswordUtil;
import utils.TransactionUtils;
import utils.WalletUtils;

public class UtilsFactory {
    private final ITransactionRepository txRepo;
    private final IWalletRepository wtRepo;

    public UtilsFactory(ITransactionRepository txRepo, IWalletRepository wtRepo) {
        this.txRepo = txRepo;
        this.wtRepo = wtRepo;
    }

    public TransactionUtils createTransactionUtils() {
        return new TransactionUtils(txRepo);
    }

    public WalletUtils createWalletUtils() {
        return new WalletUtils(wtRepo);
    }

    public static UtilsFactory getInstance(ITransactionRepository txRepo, IWalletRepository wtRepo) {
        return new UtilsFactory(txRepo, wtRepo);
    }
}
