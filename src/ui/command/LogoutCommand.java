package ui.command;

import java.sql.SQLException;
import java.util.Scanner;

import config.DIContainer;
import controller.AuthController;
import ui.interfaces.Command;

public class LogoutCommand implements Command {
    private final AuthController authCon;
    private final Scanner scanner;

    public LogoutCommand(AuthController authCon, Scanner scanner) {
        this.authCon = authCon;
        this.scanner = new Scanner(System.in);
    }

    public String getName() {
        return "logout";
    }

    public String getDescription() {
        return "Logout from your wallet";
    }

    public void execute(CommandContext context) {
        String sessionId = (String) context.get("sessionId");

        if (sessionId == null) {
            System.out.println("You are not logged in.");
            return;
        }

        boolean success = authCon.logout(sessionId);

        if (success) {
            context.remove("sessionId");
            context.remove("currentWallet");
            System.out.println("You have been logged out successfully.");
        } else {
            System.out.println("Logout failed. Invalid session.");
        }
    }
}
