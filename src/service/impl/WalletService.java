package service.impl;

import config.DIContainer;
import model.Wallet;
import model.enums.Currency;
import repository.interfaces.IWalletRepository;
import service.interfaces.IWalletService;
import utils.AddressGenerator;
import utils.WalletUtils;

import java.sql.SQLException;
import java.util.Optional;

public class WalletService implements IWalletService {
    private IWalletRepository repo;


    public void WalletRepository() throws SQLException {
        DIContainer DIC = DIContainer.getInstance();
        this.repo = DIC.getWtRepo();
    }

    @Override
    public Wallet createWallet(Currency type) {
        String address = AddressGenerator.generateAddress(type);
        Wallet wallet = new Wallet(type, address);
        repo.save(wallet);
        return wallet;
    }

    @Override
    public Optional<Wallet> getWalletById(String id) {
        return repo.findById(id);
    }

    @Override
    public void deposit(String walletId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        Optional<Wallet> walletOpt = repo.findById(walletId);
        walletOpt.ifPresent(wallet -> {
            wallet.setBalance(wallet.getBalance() + amount);
            repo.update(wallet);
        });
    }

    @Override
    public void withdraw(String walletId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        Optional<Wallet> walletOpt = repo.findById(walletId);
        walletOpt.ifPresent(wallet -> {
            wallet.setBalance(wallet.getBalance() - amount);
            repo.update(wallet);
        });
    }
}
