package service.impl;

import config.DIContainer;
import model.Wallet;
import model.enums.Currency;
import repository.interfaces.IWalletRepository;
import service.interfaces.IWalletService;
import utils.AddressGenerator;
import utils.WalletUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WalletService implements IWalletService {
    private IWalletRepository repo;

    public WalletService(IWalletRepository wtRepo) {
        this.repo = wtRepo;
    }

    @Override
    public Wallet createWallet(Wallet w) {
        String address = AddressGenerator.generateAddress(w);
        Wallet wallet = new Wallet(w.getCurrency(), address);
        repo.save(wallet);
        return wallet;
    }

    @Override
    public Optional<Wallet> getWalletById(String id) {
        return repo.findById(id);
    }

    @Override
    public void deposit(String walletId, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit must be positive");
        Optional<Wallet> walletOpt = repo.findById(walletId);
        walletOpt.ifPresent(wallet -> {
            wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
            repo.update(wallet);
        });
    }

    @Override
    public void withdraw(String walletId, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit must be positive");
        Optional<Wallet> walletOpt = repo.findById(walletId);
        walletOpt.ifPresent(wallet -> {
            wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
            repo.update(wallet);
        });
    }

    public List<Wallet> generateInitialWallets() {
        List<Wallet> wallets = new ArrayList<>();
        Wallet wallet1 = new Wallet(Currency.BITCOIN, "wallet1");
        wallet1.setBalance(BigDecimal.valueOf(5000));
        wallets.add(wallet1);

        Wallet wallet2 = new Wallet(Currency.ETHEREUM, "wallet2");
        wallet2.setBalance(BigDecimal.valueOf(8000));
        wallets.add(wallet2);
        return wallets;
    }
}
