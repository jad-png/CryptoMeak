package config;

import factories.UtilsFactory;
import repository.TransactionRepository;
import repository.WalletRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import utils.TransactionUtils;
import utils.WalletUtils;

import java.sql.SQLException;

public class DIContainer {
    private static DIContainer instance;
    private final ITransactionRepository txrepo;
    private final IWalletRepository wtRepo;
    private final UtilsFactory UtFactory;

    private DIContainer () throws SQLException {
        this.txrepo = new TransactionRepository();
        this.wtRepo = new WalletRepository();

        this.UtFactory = new UtilsFactory(txrepo, wtRepo);
    }

    public static synchronized DIContainer getInstance() throws SQLException {
        if (instance == null) {
            instance = new DIContainer();
        }

        return instance;
    }

    public ITransactionRepository getTxRepo() {
        return txrepo;
    }

    public IWalletRepository getWtRepo() {
        return wtRepo;
    }

    public TransactionUtils getTransactionUtils() {
        return UtFactory.createTransactionUtils();
    }

    public WalletUtils getWalletUtils() {
        return UtFactory.createWalletUtils();
    }

    public UtilsFactory getUtilsFactory() {
        return UtFactory;
    }
}
