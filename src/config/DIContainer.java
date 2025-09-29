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

import controller.AuthController;
import controller.TransactionController;
import controller.WalletController;

public class DIContainer {
    private static DIContainer instance;
    private final ITransactionRepository txrepo;
    private final IWalletRepository wtRepo;
    private final IAuthRepository authRepo;
    
    private final UtilsFactory UtFactory;

    private final TransactionService txSer;
    private final WalletService wtSer;
    private final AuthServiceImpl authSer;

    private final AuthController authCon;
    private final TransactionController txCon;
    private final WalletController wtCon;


    private DIContainer () throws SQLException {
        this.txrepo = new TransactionRepository();
        this.wtRepo = new WalletRepository();
        this.authRepo = new AuthRepositoryImpl();

        this.UtFactory = new UtilsFactory(txrepo, wtRepo);

        this.txSer = new TransactionService();
        this.wtSer = new WalletService();
        this.authSer = new AuthServiceImpl();

        this.authCon = new AuthController();
        this.txCon = new TransactionController();
        this.wtCon = new WalletController();
    }

    public static synchronized DIContainer getInstance() throws SQLException {
        if (instance == null) {
            instance = new DIContainer();
        }

        return instance;
    }

    // repositories
    public ITransactionRepository getTxRepo() {
        return txrepo;
    }

    public IWalletRepository getWtRepo() {
        return wtRepo;
    }

    public IAuthRepository getAuthRepo() { return authRepo; }
    
    // utils
    public TransactionUtils getTransactionUtils() {
        return UtFactory.createTransactionUtils();
    }

    public WalletUtils getWalletUtils() {
        return UtFactory.createWalletUtils();
    }

    // factories
    public UtilsFactory getUtilsFactory() {
        return UtFactory;
    }

    // services
    public TransactionService getTxSer() {
        return txSer;
    }

    public WalletService getWtSer() {
        return wtSer;
    }

    public AuthServiceImpl getAuthSer() {
        return authSer;
    }

    // controllers
    public AuthController getAuthCon() {
        return authCon;
    }

    public TransactionController getTxCon() {
        return txCon;
    }

    public WalletController getWtCon() {
        return wtCon;
    }
}
