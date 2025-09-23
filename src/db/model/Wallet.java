package db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
	private UUID id;
	private String address;
	private double balance;
	private CryptoType type;
	private List<Transaction> txs;
	
	public Wallet(UUID id, CryptoType type, String address) {
		this.id = id;
		this.address = address;
		this.balance = 0;
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
}
