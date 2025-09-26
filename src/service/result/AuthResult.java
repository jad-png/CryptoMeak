package service.result;

import model.Wallet;

public class AuthResult {
    private final boolean success;
    private final String message;
    private final Wallet wallet;
    private final String sessionId;

    private AuthResult(boolean success, String message, Wallet wallet, String sessionId) {
        this.success = success;
        this.message = message;
        this.wallet = wallet;
        this.sessionId = sessionId;
    }

    public static AuthResult success(Wallet wallet, String message) {
        return new AuthResult(true, message, wallet, null);
    }

    public static AuthResult error(String message) {
        return new AuthResult(false, message, null, null);
    }

    public AuthResult withSessionId(String sessionId) {
        return new AuthResult(success, message, wallet, sessionId);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Wallet getWallet() { return wallet; }
    public String getSessionId() { return sessionId; }
}
