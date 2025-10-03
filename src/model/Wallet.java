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
    private String ownerName;
    private String wtName;
    private Currency currency;
    private LocalDateTime createdAt;
    private String passwordHash;
    private List<Transaction> transactions;

    public Wallet(Currency currency, String address) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.balance = 0.0;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }
    private Wallet(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.balance = builder.balance != null ? builder.balance : 0.0;
        this.ownerName = builder.ownerName;
        this.wtName = builder.wtName;
        this.currency = builder.currency;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.passwordHash = builder.passwordHash;
        this.transactions = new ArrayList<>();
    }

    public static class Builder {
        private String id;
        private String address;
        private Double balance;
        private String ownerName;
        private String wtName;
        private Currency currency;
        private LocalDateTime createdAt;
        private String passwordHash;

        public Builder id(String id) { this.id = id; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder balance(Double balance) { this.balance = balance; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder wtName(String wtName) { this.wtName = wtName; return this; }
        public Builder currency(Currency currency) { this.currency = currency; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }

        public Wallet build() {
            return new Wallet(this);
        }
    }


    // getters
    public String getId() {
        return this.id;
    }

    public String getAddress() {
        return this.address;
    }

    public double getBalance() {
        return this.balance;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public String getWtName() {
        return this.wtName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // setters
    public void setAddress(String address) {
        this.address = address;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setType(Currency currency) {
        this.currency = currency;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setWtName(String wtName) {
        this.wtName = wtName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // utility method
    public void addTransaction(Transaction tx) {
        this.transactions.add(tx);
    }
}
