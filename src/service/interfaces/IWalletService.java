package service.interfaces;

import model.Wallet;
import model.enums.WalletType;

import java.util.Optional;

public interface IWalletService {
    Wallet createWallet(WalletType type);
    Optional<Wallet> getWalletById(String id);
    void deposit(String walletId, double amount);
    void withdraw(String walletId, double amount);
}
