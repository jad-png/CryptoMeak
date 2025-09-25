package model;

import model.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
    private final String id;
    private String address;
    private double balance;
    private Currency currency;
    private LocalDateTime createdAt;
    private List<Transaction> transactions;


    public Wallet(Currency currency, String address) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.balance = 0.0;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public Wallet(UUID id, String address, Currency currency, String ownerName, BigDecimal balance, LocalDateTime createdAt) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.balance = balance.doubleValue();
        this.currency = currency;
        this.createdAt = createdAt;
    }

    // getters
    public String getId() { return this.id; }
    public String getAddress() { return this.address; }
    public double getBalance() { return this.balance; }
    public Currency getType() { return this.currency; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public List<Transaction> getTransactions() { return this.transactions; }

    // setters
    public void setAddress(String address) { this.address = address; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setType(Currency currency) { this.currency = currency; }

    // utility method
    public void addTransaction(Transaction tx) {
        this.transactions.add(tx);
    }
}
