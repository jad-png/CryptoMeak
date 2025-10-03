package ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import config.DIContainer;
import controller.AuthController;
import controller.MempoolController;
import controller.TransactionController;
import controller.WalletController;
import model.Transaction;
import model.Wallet;
import model.enums.Currency;
import ui.command.CommandContext;
import ui.command.CompareFeesCommand;
import ui.command.CreateTransactionCommand;
import ui.command.CreateWalletTransactionCommand;
import ui.command.LoginWalletCommand;
import ui.command.LogoutCommand;
import ui.command.RegisterWalletCommand;
import ui.command.ViewMempoolCommand;
import ui.command.ViewMempoolPositionCommand;
import ui.command.ViewTransactionCommand;
import ui.interfaces.Command;

public class CommandManager {
    private final Map<String, Command> commands;
    private final CommandContext context;
    private final Scanner scanner;

    private final AuthController authCon;
    private final TransactionController txCon;
    private final WalletController wtCon;
    private final MempoolController mpCon;

    public CommandManager(AuthController authCon, TransactionController txCon, WalletController wtCon,
            MempoolController mpCon) {
        this.commands = new LinkedHashMap<>();
        this.context = new CommandContext();
        this.scanner = new Scanner(System.in);

        this.authCon = authCon;
        this.txCon = txCon;
        this.wtCon = wtCon;
        this.mpCon = mpCon;


        registerCommands(authCon, txCon, wtCon, mpCon);
    }

    private void registerCommands(AuthController authCon, TransactionController txCon, WalletController wtCon,
            MempoolController mpCon) {
        // auth commands
        registerCommand(new RegisterWalletCommand(authCon, scanner));
        registerCommand(new LoginWalletCommand(authCon, scanner));
        registerCommand(new LogoutCommand(authCon, scanner));

        // transaction commands
        registerCommand(new CreateTransactionCommand(txCon));
        registerCommand(new ViewTransactionCommand(txCon));
        registerCommand(new CreateWalletTransactionCommand(authCon));

        // Mempool commands
        registerCommand(new CompareFeesCommand(mpCon));
        registerCommand(new ViewMempoolPositionCommand(mpCon));
        registerCommand(new ViewMempoolCommand(mpCon));
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public boolean execute(String commandName) {
        if (commandName.equalsIgnoreCase("help")) {
            showHelp();
            return true;
        } else if (commandName.equalsIgnoreCase("status")) {
            showStatus();
            return true;
        } else if (commandName.equalsIgnoreCase("exit")) {
            System.out.println("Exiting application...");
            System.exit(0);
            return true;
        }

        // Handle registered commands
        Command command = commands.get(commandName.toLowerCase());
        if (command != null) {
            command.execute(context);
            return true;
        }
        try {
            List<Wallet> demoWallets = wtCon.generateInitialWallets();
            // Optionally, save them to DB if needed
            for (Wallet w : demoWallets) {
                wtCon.createWallet(w);
            }

            // Generate demo transactions
            List<Transaction> demoTxs = txCon.generateRandomTransactions(5);
            // Optionally, save them to DB if needed
            for (Transaction tx : demoTxs) {
                txCon.createTransaction(
                    tx.getSourceAddress(),
                    tx.getDestinationAddress(),
                    tx.getAmount(),
                    tx.getPriority(),
                    tx.getCurrency()
                );
            }

            System.out.println("Demo wallets and transactions generated.");
        } catch (Exception e) {
            System.err.println("Failed to generate demo wallets/transactions: " + e.getMessage());
        }
        return false;
    }

    public void showHelp() {
        System.out.println("\n" + "======================================================================");
        System.out.println("CRYPTO WALLET SIMULATOR - HELP MENU");
        System.out.println("=======================================================================");

        System.out.println("\nAVAILABLE COMMANDS:");
        System.out.println("-----------------------------------------------------------------------------");

        // Group commands by category
        System.out.println("\nAUTHENTICATION COMMANDS:");
        commands.values().stream()
                .filter(cmd -> cmd.getName().toLowerCase().contains("login") ||
                        cmd.getName().toLowerCase().contains("logout") ||
                        cmd.getName().toLowerCase().contains("register"))
                .forEach(cmd -> System.out.printf("  %-25s - %s%n", cmd.getName(), cmd.getDescription()));

        System.out.println("\nWALLET COMMANDS:");
        commands.values().stream()
                .filter(cmd -> cmd.getName().toLowerCase().contains("wallet") &&
                        !cmd.getName().toLowerCase().contains("transaction"))
                .forEach(cmd -> System.out.printf("  %-25s - %s%n", cmd.getName(), cmd.getDescription()));

        System.out.println("\nTRANSACTION COMMANDS:");
        commands.values().stream()
                .filter(cmd -> cmd.getName().toLowerCase().contains("transaction"))
                .forEach(cmd -> System.out.printf("  %-25s - %s%n", cmd.getName(), cmd.getDescription()));

        System.out.println("\nMEMPOOL COMMANDS:");
        commands.values().stream()
                .filter(cmd -> cmd.getName().toLowerCase().contains("mempool") ||
                        cmd.getName().toLowerCase().contains("fee") ||
                        cmd.getName().toLowerCase().contains("position"))
                .forEach(cmd -> System.out.printf("  %-25s - %s%n", cmd.getName(), cmd.getDescription()));

        System.out.println("\nUSAGE TIPS:");
        System.out.println("  Type command name or number to execute");
        System.out.println("  Login required for most operations");
        System.out.println("  Check mempool status before creating transactions");
        System.out.println("  Compare fees to optimize cost vs speed");
        System.out.println("-----------------------------------------------------------------------------");
    }

    public void showWelcome() {
        System.out.println("\n" + "**********************************************************");
        System.out.println("***                   CRYPTO WALLET SIMULATOR                    ***");
        System.out.println("*********************************************************************");
        System.out.println("A realistic blockchain transaction simulator with mempool analysis");
        System.out.println("Features:");
        System.out.println("  - Create Bitcoin and Ethereum wallets");
        System.out.println("  - Send transactions with different fee priorities");
        System.out.println("  - View real-time mempool status");
        System.out.println("  - Analyze transaction positions and wait times");
        System.out.println("  - Compare fee levels for optimal strategy");
        System.out.println("\nType 'help' to see available commands");
        System.out.println("**********************************************************************");
    }

    // TODO: implement methos (showStatus, isAuthenticated, printCommand)

    public void showStatus() {
        Optional<Wallet> currentWallet = getAuthenticatedWallet();

        System.out.println("\n" + "------------------------------------------------------");
        System.out.println("CURRENT SESSION STATUS");
        System.out.println("--------------------------------------------------------");

        if (currentWallet.isPresent()) {
            Wallet wallet = currentWallet.get();
            System.out.println("Logged in as: " + wallet.getOwnerName());
            System.out.println("Wallet: " + wallet.getWtName());
            System.out.println("Address: " + wallet.getAddress());
            System.out.println("Currency: " + wallet.getCurrency());
            System.out.println("Balance: " + wallet.getBalance() + " " + wallet.getCurrency());

            // Show mempool stats
            try {
                DIContainer DIC = DIContainer.getInstance();
                MempoolController mpCon = DIC.getMpCon();
                int mempoolSize = mpCon.getMempoolSize();
                System.out.println("Mempool size: " + mempoolSize + " pending transactions");
            } catch (Exception e) {
                System.out.println("Mempool status: Unavailable");
            }
        } else {
            System.out.println("Status: Not logged in");
            System.out.println("Please login to access wallet features");
        }
        System.out.println("--------------------------------------------------------");
    }

    public boolean isAuthenticated() {
        return getAuthenticatedWallet().isPresent();
    }

    public void printCommands() {
        System.out.println("\nQUICK COMMAND LIST:");
        System.out.println("-----------------------------------------------------------------");

        int index = 1;
        for (Command command : commands.values()) {
            System.out.printf("%2d. %s%n", index++, command.getName());
        }

        System.out.println("-----------------------------------------------------------------");
        System.out.println("Enter command name or number to execute");
        System.out.println("Type 'help' for detailed descriptions");
        System.out.println("Type 'status' to check current session");
        System.out.println("Type 'exit' to quit the application");
    }

    private Optional<model.Wallet> getAuthenticatedWallet() {
        return Optional.ofNullable(context.get("currentWallet"));
    }

    public CommandContext getContext() {
        return context;
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
