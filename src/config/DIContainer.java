package config;

import factories.UtilsFactory;
import repository.AuthRepositoryImpl;
import repository.TransactionRepository;
import repository.WalletRepository;
import repository.interfaces.IAuthRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import service.impl.AuthServiceImpl;
import service.impl.TransactionService;
import service.impl.WalletService;
import utils.TransactionUtils;
import utils.WalletUtils;

import java.sql.SQLException;

import controller.TransactionController;

public class DIContainer {
    private static DIContainer instance;
    private final ITransactionRepository txrepo;
    private final IWalletRepository wtRepo;
    private final IAuthRepository authRepo;
    
    private final UtilsFactory UtFactory;

    private final TransactionService txSer;
    private final WalletService wtSer;
    private final AuthServiceImpl authSer;


    private DIContainer () throws SQLException {
        this.txrepo = new TransactionRepository();
        this.wtRepo = new WalletRepository();
        this.authRepo = new AuthRepositoryImpl();

        this.UtFactory = new UtilsFactory(txrepo, wtRepo);

        this.txSer = new TransactionService();
        this.wtSer = new WalletService();
        this.authSer = new AuthServiceImpl();
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

    public IAuthRepository getAuthRepo() { return authRepo; }
    
    public TransactionUtils getTransactionUtils() {
        return UtFactory.createTransactionUtils();
    }

    public WalletUtils getWalletUtils() {
        return UtFactory.createWalletUtils();
    }

    public UtilsFactory getUtilsFactory() {
        return UtFactory;
    }

    public TransactionService getTxSer() {
        return txSer;
    }

    public WalletService getWtSer() {
        return wtSer;
    }

    public AuthServiceImpl getAuthSer() {
        return authSer;
    }
}
