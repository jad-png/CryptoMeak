package controller;

import model.Wallet;
import model.enums.Currency;
import service.impl.WalletService;

import java.sql.SQLException;
import java.util.Optional;

import config.DIContainer;

public class WalletController {
    private WalletService walletSer;

    public WalletController() throws SQLException {
        DIContainer DIC = DIContainer.getInstance();
        this.walletSer = DIC.getWtSer();
    }

    public Wallet createWallet(Currency type) {
        return walletSer.createWallet(type);
    }

    public Optional<Wallet> getWalletById(String id) {
        return walletSer.getWalletById(id);
    }

    public void deposit(String id, double amount) {
        walletSer.deposit(id, amount);
    }

    public void withdraw(String id, double amount) {
        walletSer.withdraw(id, amount);
    }
}
