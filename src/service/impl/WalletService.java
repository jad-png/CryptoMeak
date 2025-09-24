package service.impl;

import model.Wallet;
import model.enums.WalletType;
import repository.WalletRepository;
import service.interfaces.IWalletService;
import utils.WalletUtils;

import java.util.Optional;

public class WalletService implements IWalletService {
    private WalletRepository repo;

    public void WalletRepository(WalletRepository WalletRepo) {
        this.repo = WalletRepo;
    }

    @Override
    public Wallet createWallet(WalletType type) {
        Wallet wallet = new Wallet(type, WalletUtils.generateCryptoAddress(type));
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
