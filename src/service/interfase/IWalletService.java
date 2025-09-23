package service.interfase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import db.model.CryptoType;
import db.model.Transaction;
import db.model.Wallet;

public interface IWalletService {
    Wallet createWallet(CryptoType type);
    Optional<Wallet> findWalletById(UUID id);
    Optional<Wallet> findWalletByAddress(String address);
    List<Wallet> findAllWallets();
    boolean deleteWallet(UUID id);
    double getWalletBalance(UUID walletId);
    boolean transferFunds(UUID sourceWalletId, String destinationAddress, double amount);
    boolean validateWalletAddress(String address, CryptoType expectedType);
    List<Transaction> getWalletTransactions(UUID walletId);
}