package utils;

import model.enums.WalletType;

import java.util.Random;
import java.util.UUID;

public class WalletUtils {
    public static String generateCryptoAddress(WalletType type) {
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
}
