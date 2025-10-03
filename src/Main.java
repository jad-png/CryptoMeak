//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import config.DIContainer;
import controller.AuthController;
import controller.TransactionController;
import controller.WalletController;
import model.Transaction;
import model.Wallet;
import model.enums.Currency;
import model.enums.TxPriority;
import model.enums.TxStatus;
import ui.CommandManager;

public class Main {
    public static void main(String[] args) {

        List<Transaction> txs = Arrays.asList(
            new Transaction(
                UUID.randomUUID(),
                "src1",
                "dest1",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(10),
                TxStatus.PENDING,
                TxPriority.FAST,
                Currency.ETHEREUM,
                LocalDateTime.now(),
                null
            ),
            new Transaction(
                UUID.randomUUID(),
                "src2",
                "dest2",
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(20),
                TxStatus.CONFIRMED,
                TxPriority.STANDARD,
                Currency.BITCOIN,
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            new Transaction(
                UUID.randomUUID(),
                "src3",
                "dest1",  // same as the first one (to test distinct)
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(30),
                TxStatus.PENDING,
                TxPriority.FAST,
                Currency.ETHEREUM,
                LocalDateTime.now(),
                null
            )
        );

        // Stream: extract distinct destination addresses
                // txs.stream()
                // .map(Transaction::getDestinationAddress) // extract destination
                // .distinct()                              // keep unique only
                // .forEach(System.out::println);           // collect to list

        // filter pending transactions of a wallet
        // List<Transaction> pendingTxs = wallet
    

        try {
            
            DIContainer DIC = DIContainer.getInstance();
            CommandManager cmdManager = DIC.getCommandManager();
            
            List<Wallet> wts = DIC.getWtSer().generateInitialWallets();

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