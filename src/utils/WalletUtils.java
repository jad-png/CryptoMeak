package utils;

import model.enums.Currency;
import repository.interfaces.IWalletRepository;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class WalletUtils {
    private final IWalletRepository wtRepo;

    public WalletUtils(IWalletRepository wtRepo) {
        this.wtRepo = wtRepo;
    }

    public static String generateCryptoAddress(Currency type) {
        Random random = new Random();
        String baseId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        switch (type) {
            case BITCOIN:
                String btcPrefix = random.nextBoolean() ? "1" : "3";
                return btcPrefix + baseId.substring(0, 33);
            case ETHEREUM:
                return "0x" + baseId.substring(0, 40);
            default:
                throw new IllegalArgumentException("Unsupported crypto type: " + type);
        }
    }

    public void validateWalletExist(String srcAddress, String destAddress) {
        if (!wtRepo.existsByAddress(srcAddress)) {
            throw new IllegalArgumentException("Source wallet does not exist: " + srcAddress);
        }

        if (!wtRepo.existsByAddress(destAddress)) {
            throw new IllegalArgumentException("Destinaire wallet does not exist: " + destAddress);
        }
    }

    public void validateCurrencyCompatibility(String srcAddress, Currency currency) {
        Currency srcCurrency = wtRepo.findCurrencyByAddress(srcAddress);

        if (srcCurrency != currency) {
            throw new IllegalArgumentException(
                    "Source wallet currency mismatch. Expected: " + srcCurrency + ", Got: " + currency
            );
        }
    }

    public void validateSufficientBalance(String srcAddress, BigDecimal totalAmount) {
        if (!wtRepo.hasSufficientBalance(srcAddress, totalAmount)) {
            BigDecimal currentBalance = wtRepo.getBalance(srcAddress);

            throw new IllegalArgumentException(
                    "Insufficient balance. Required: " + totalAmount + ", Available: " + currentBalance
            );
        }
    }
}
