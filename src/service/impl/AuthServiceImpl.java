package service.impl;

import config.DIContainer;
import model.Wallet;
import repository.interfaces.IAuthRepository;
import repository.interfaces.IWalletRepository;
import service.result.AuthResult;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import utils.PasswordUtil;

public class AuthServiceImpl {
    private final IAuthRepository authRepo;
    private final IWalletRepository wtRepo;

    // save and stock active session, sessionId -> wtAddress
    private final ConcurrentHashMap<String, String> activeSessions;

    // authenticated wallets cache, sessionId -> Wallet
    private final ConcurrentHashMap<String, Wallet> authenticatedWallets;

    public AuthServiceImpl(IAuthRepository authRepo, IWalletRepository wtRepo) {

        this.authRepo = authRepo;
        this.wtRepo = wtRepo;

        this.activeSessions = new ConcurrentHashMap<>();
        this.authenticatedWallets = new ConcurrentHashMap<>();
    }

    public AuthResult registerWallet(Wallet wt, String pswrd) {
        try {
            if (wtRepo.existsByAddress(wt.getAddress())) {
                return AuthResult.error("A wallet with this address already exists");
            }

            if (authRepo.walletExists(wt.getAddress())) {
                return AuthResult.error("this address walet already saved");
            }

            if (pswrd == null || pswrd.length() < 4) {
                return AuthResult.error("Password must have been more than 4 digits");
            }

            String passwordHash = PasswordUtil.hashPassword(pswrd);

            wt.setPasswordHash(passwordHash);

            wtRepo.save(wt);

            return AuthResult.success(wt, "Wallet save successfully");
        } catch (Exception e) {
            return AuthResult.error("Error while saving: " + e.getMessage());
        }
    }

    public AuthResult login(String wtAddress, String pswrd) {
        try {
            Optional<Wallet> wtOpt = wtRepo.findByAddress(wtAddress);

            // Optional<Wallet> wtOpt2 = wtRepo.findById(wtOpt.get().getId());

            if (!wtOpt.isPresent()) {
                return AuthResult.error("Wallet not found (address)");
            }

            System.out.println(wtOpt.get().getPasswordHash());

            if (!PasswordUtil.verifyPassword(pswrd, wtOpt.get().getPasswordHash())) {
                return AuthResult.error("Wrong password");
            }

            // authRepo.updateLastLogin(wtAddress);

            // create new session
            String sessionId = generateSessionId();
            activeSessions.put(sessionId, wtAddress);
            authenticatedWallets.put(sessionId, wtOpt.get());

            return AuthResult.success(wtOpt.get(), "Login Successfully")
                    .withSessionId(sessionId);
        } catch (Exception e) {
            return AuthResult.error("Error while login: " + e.getMessage());
        }
    }

    public boolean logout(String sessionId) {
        if (activeSessions.containsKey(sessionId)) {
            activeSessions.remove(sessionId);
            authenticatedWallets.remove(sessionId);
            return true;
        }
        return false;
    }

    // check if session is active
    public boolean isAuthenticated(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    // get the authenticated wallet
    public Optional<Wallet> getAuthenticatedWallet(String sessionId) {
        return Optional.ofNullable(authenticatedWallets.get(sessionId));
    }

    public AuthResult changePassword(String sessionId, String currentPassword, String newPassword) {
        try {
            Wallet wt = getAuthenticatedWallet(sessionId).orElse(null);

            if (wt == null) {
                return AuthResult.error("Invalid session");
            }

            Optional<String> storedHashOpt = authRepo.getPasswordHash(wt.getAddress());

            if (!storedHashOpt.isPresent() || !PasswordUtil.verifyPassword(currentPassword, storedHashOpt.get())) {
                return AuthResult.error("Wrong password");
            }

            if (newPassword == null || newPassword.length() < 4) {
                return AuthResult.error("Password must have been more than 4 digits");
            }

            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            authRepo.updatePassword(wt.getAddress(), newPasswordHash);

            return AuthResult.success(wt, "password changed successfully");
        } catch (Exception e) {
            return AuthResult.error("Error while changing password: " + e.getMessage());
        }
    }

    public AuthResult deleteWallet(String sessionId, String pswrd) {
        try {
            Wallet wt = getAuthenticatedWallet(sessionId).orElse(null);

            if (wt == null) {
                return AuthResult.error("Invalid session");
            }

            Optional<String> storedHashOpt = authRepo.getPasswordHash(wt.getAddress());

            if (!storedHashOpt.isPresent() ||
                    !PasswordUtil.verifyPassword(pswrd, storedHashOpt.get())) {
                return AuthResult.error("Mot de passe incorrect");
            }

            authRepo.deleteWtAuth(wt.getAddress());
            wtRepo.delete(wt);

            logout(sessionId);

            return AuthResult.success(null, "Wallet deleted.");
        } catch (Exception e) {
            return AuthResult.error("Error while deleting: " + e.getMessage());
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
