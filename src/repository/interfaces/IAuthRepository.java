package repository.interfaces;

import java.util.Optional;

public interface IAuthRepository {
    void saveWalletAuth(String wtAddress, String passwordHash);
    Optional<String> getPasswordHash(String wtAddress);
    boolean walletExists(String wtAddress);
    void updateLastLogin(String wtAddress);
    void updatePassword(String wtAddress, String newHashPassword);
    boolean deleteWtAuth(String wtAddress);
}
