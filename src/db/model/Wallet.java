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
	
	public UUID getId() { return id; }
	public CryptoType getType() { return type; }
	public String getAddress() { return address; }
	public double getBalance() { return balance; }
	
	// TODO: add addTrasaction() method that implement one parametre tx type of Transaction entity 
	// that add transatction to list of transaction
	
	public void addTransaction(Transaction tx) {
		txs.add(tx);
	}
	
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
}
