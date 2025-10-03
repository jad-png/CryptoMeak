package service.interfaces;

import model.Wallet;
import model.enums.Currency;

import java.util.Optional;

public interface IWalletService {
    Wallet createWallet(Wallet w);
    Optional<Wallet> getWalletById(String id);
    void deposit(String walletId, double amount);
    void withdraw(String walletId, double amount);
}
