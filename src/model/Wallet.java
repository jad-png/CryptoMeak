package model;

import model.enums.WalletType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
    private final String id;
    private String address;
    private double balance;
    private WalletType type;
    private LocalDateTime createdAt;
    private List<Transaction> transactions;


    public Wallet(WalletType type, String address) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.balance = 0.0;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    // getters
    public String getId() { return this.id; }
    public String getAddress() { return this.address; }
    public double getBalance() { return this.balance; }
    public WalletType getType() { return this.type; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public List<Transaction> getTransactions() { return this.transactions; }

    // setters
    public void setAddress(String address) { this.address = address; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setType(WalletType type) { this.type = type; }

    // utility method
    public void addTransaction(Transaction tx) {
        this.transactions.add(tx);
    }
}
