package controller;

import java.sql.SQLException;
import java.util.Optional;

import config.DIContainer;
import model.Wallet;
import service.impl.AuthServiceImpl;
import service.result.AuthResult;

public class AuthController {
    private final AuthServiceImpl authSer;

    public AuthController(AuthServiceImpl authSer) {
            this.authSer = authSer;
    }

    public AuthResult registerWallet(Wallet wt, String pswrd) {
        return authSer.registerWallet(wt, pswrd);
    }

    public AuthResult login(String wtAddress, String pswrd) {
        return authSer.login(wtAddress, pswrd);
    }

    public boolean logout(String sessionId) {
        return authSer.logout(sessionId);
    }

    public boolean isAuthenticated(String sessionId) {
        return authSer.isAuthenticated(sessionId);
    }

    public Optional<Wallet> getAuthenticatedWallet(String sessionId) {
        return authSer.getAuthenticatedWallet(sessionId);
    }

    // acc management methods
    public AuthResult changePassword(String sessionId, String currentPswrd, String newPassword) {
        return authSer.changePassword(sessionId, currentPswrd, newPassword);
    }

    public AuthResult deleteWallet(String sessionId, String pswrd) {
        return authSer.deleteWallet(sessionId, pswrd);
    }
}
