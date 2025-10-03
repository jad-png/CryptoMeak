package utils;

import model.Wallet;
import model.enums.Currency;

import java.security.SecureRandom;

public class AddressGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String HEX_CHARS = "0123456789ABCDEF";

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            int v = b & 0xFF;
            hexString.append(HEX_CHARS.charAt(v >>> 4))
                     .append(HEX_CHARS.charAt(v & 0xF));
        }
        return hexString.toString();
    }

    public static String generateBitcoinAddress() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return "1" + bytesToHex(bytes).substring(0, 39);
    }

    public static String generateEthereumAddress() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return "0x" + bytesToHex(bytes).substring(0, 40).toLowerCase();
    }


    public static String generateAddress(Wallet w) {
        switch (w.getCurrency()) {
            case BITCOIN:
                return generateBitcoinAddress();
            case ETHEREUM:
                return generateEthereumAddress();
            default:
                throw new IllegalArgumentException("Unknown currency: " + w.getCurrency());
        }
    }
// TODO: validation
//    public static boolean isValidAddress(String address, WalletType walletType) {
//        if (address == null || address.trim().isEmpty()) {
//            return false;
//        }
//
//        switch (walletType) {
//            case BITCOIN:
//                return address.startsWith("1") && address.length() == 40 &&
//                        address.matches("[0-9A-F]+");
//            case ETHEREUM:
//                return address.startsWith("0x") && address.length() == 42 &&
//                        address.matches("0x[0-9a-fA-F]+");
//            default:
//                return false;
//        }
//    }
}
