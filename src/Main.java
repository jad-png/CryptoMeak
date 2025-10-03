//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import config.DIContainer;
import controller.AuthController;
import controller.TransactionController;
import controller.WalletController;
import ui.CommandManager;

public class Main {
    public static void main(String[] args) {
        try {
            DIContainer DIC = DIContainer.getInstance();
            CommandManager cmdManager = DIC.getCommandManager();
            

            while (true) {
                System.out.print("/n Commande >");
                String input = cmdManager.getScanner().nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    // TODO: add method to clean up resources from DIC
                    break;
                }

                if (!cmdManager.execute(input)) {
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
                }
            }
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
            System.exit(1);
        }
    }
}