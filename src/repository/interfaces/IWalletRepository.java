package repository.interfaces;

import model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import model.enums.Currency;

import javax.swing.text.html.Option;

public interface IWalletRepository extends Repository<Wallet>{
    // Methodes for Tx service
    boolean existsByAddress(String address);
    Currency findCurrencyByAddress(String address);
    BigDecimal getBalance(String address);
    void subtractBalance(String address, BigDecimal amount);
    void addBalance(String address, BigDecimal amount);

    // suplements utils methods
    Optional<Wallet> findByAddress(String address);
    List<Wallet> findByCurrency(Currency currency);
    boolean hasSufficientBalance(String address, BigDecimal requiredAmount);
}
