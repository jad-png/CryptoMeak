package db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Wallet {
	private UUID id;
	private String address;
	private double balance;
	private CryptoType type;
	private List<Transaction> txs;
	
	// default construct
	public Wallet(CryptoType type) {
		this.id = UUID.randomUUID();
		this.address = generateCryptoAddress(type);
		this.balance = 0.0;
		this.type = type;
		this.txs = new ArrayList<>();
	}
	
	// construct for loading existing wallets
	public Wallet(UUID id, CryptoType type, String address) {
		this.id = id;
		this.address = address;
		this.balance = 0.0;
		this.type = type;
		this.txs = new ArrayList<>();	
	}
	
	// construct with balance
	public Wallet(UUID id, CryptoType type, String address, double balance) {
		this.id = id;
		this.address = address;
		this.balance = balance;
		this.type = type;
		this.txs = new ArrayList<>();	
	}
	
	// getters
	public UUID getId() { return id; }
	public CryptoType getType() { return type; }
	public String getAddress() { return address; }
	public double getBalance() { return balance; }
	public List<Transaction> getTransactions() { return new ArrayList<>(txs); }
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	// add transaction and auto update balance based on transaction
	public void addTransaction(Transaction tx) {
		if (txs != null) {
			txs.add(tx);
			
			
			if (tx.getSourceAddress().equals(this.address)) {
				this.balance -= (tx.getAmount() + tx.getFees());
			} else if (tx.getDesAddress().equals(this.address)) {
				this.balance += tx.getAmount();
			}
		}
	}
	
	// generate wallet address
	public String generateCryptoAddress(CryptoType type) {
		Random random = new Random();
		String baseId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
		
		switch (type) {
		case BITCOIN: 
			String btcPrefix = random.nextBoolean() ? "1" : "3";
			return btcPrefix + baseId.substring(0, 33);
		case ETHERIUM: 
			return "0x" + baseId.substring(0, 40);
		default: 
            throw new IllegalArgumentException("Unsupported crypto type: " + type);
		}
	}
	
//	public void deposit(double amount) {
//		if (amount <= 0) {
//            throw new IllegalArgumentException("Deposit amount must be positive");
//		}
//		
//		this.balance += amount;
//	}
//	
//	public boolean withdraw(double amount) {
//		
//	      if (amount <= 0) {
//	            throw new IllegalArgumentException("Withdrawal amount must be positive");
//	        }
//	      
//		if (amount > balance) {
//			return false;
//		}
//		
//		this.balance -= amount;
//		
//		return true;
//	}
}
