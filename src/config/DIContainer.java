package config;

import factories.UtilsFactory;
import repository.AuthRepositoryImpl;
import repository.TransactionRepository;
import repository.WalletRepository;
import repository.interfaces.IAuthRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import service.impl.AuthServiceImpl;
import service.impl.MempoolServiceImpl;
import service.impl.TransactionService;
import service.impl.WalletService;
import service.interfaces.MempoolService;
import ui.CommandManager;
import utils.TransactionUtils;
import utils.WalletUtils;

import java.sql.SQLException;

import controller.AuthController;
import controller.MempoolController;
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
    private final MempoolServiceImpl mpSer;

    private final AuthController authCon;
    private final TransactionController txCon;
    private final WalletController wtCon;
    private final MempoolController mpCon;

    private final CommandManager cmdManager;

    private DIContainer() throws SQLException {
        this.txrepo = new TransactionRepository();
        this.wtRepo = new WalletRepository();
        this.authRepo = new AuthRepositoryImpl();

        this.UtFactory = new UtilsFactory(txrepo, wtRepo);

        this.txSer = new TransactionService(this.txrepo,
                this.wtRepo,
                this.UtFactory.createTransactionUtils(),
                this.UtFactory.createWalletUtils());

        this.wtSer = new WalletService(this.wtRepo);
        this.authSer = new AuthServiceImpl(this.authRepo, this.wtRepo);
        this.mpSer = new MempoolServiceImpl(txrepo);

        this.authCon = new AuthController(this.authSer);
        this.txCon = new TransactionController(this.txSer, this.authSer);
        this.wtCon = new WalletController(this.wtSer);
        this.mpCon = new MempoolController(mpSer);

        this.cmdManager = new CommandManager(authCon, txCon, wtCon, mpCon);
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

    public IAuthRepository getAuthRepo() {
        return authRepo;
    }

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

    public MempoolService getMempoolService() {
        return mpSer;
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

    public MempoolController getMpCon() {
        return mpCon;
    }

    // managers
    public CommandManager getCommandManager() {
        return cmdManager;
    }
}
