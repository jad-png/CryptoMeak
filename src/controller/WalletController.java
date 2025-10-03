package controller;

import model.Wallet;
import model.enums.Currency;
import service.impl.WalletService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import config.DIContainer;

public class WalletController {
    private WalletService walletSer;

    public WalletController(WalletService wtSer) {
        this.walletSer = wtSer;
    }

    public Wallet createWallet(Wallet w) {
        return walletSer.createWallet(w);
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

    public List<Wallet> generateInitialWallets() {
        return walletSer.generateInitialWallets();
    }
}
