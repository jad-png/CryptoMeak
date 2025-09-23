package db.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;


public class Mempool {
	private PriorityQueue<Transaction> queue;
	
	public Mempool() {
		queue = new PriorityQueue<>(Comparator.comparingDouble(Transaction::getFees).reversed());	
	}
	
	public void addTransaction(Transaction tx) {
		queue.add(tx);
	}
	
	public Transaction getNextTransaction() {
		return queue.poll();
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	// pending requests in order
	public List<Transaction> getPendingTransactions() {
		return new ArrayList<>(queue);
	}
	
	public int getTransactionPosition(Transaction tx) {
		List<Transaction> sorted = new ArrayList<>(queue);
		sorted.sort(Comparator.comparingDouble(Transaction::getFees).reversed());
		return sorted.indexOf(tx) +1;
	}
	
	public int estimateWaitingTime(Transaction tx) {
		int position = getTransactionPosition(tx);
		return position * 10;
	}
	
	// Generate random transactions to simulate network activity
	public void generateRandomTransactions(int count) {
	    Random random = new Random();
	    for (int i = 0; i < count; i++) {
	        String id = UUID.randomUUID().toString().substring(0, 8);
	        double fees = 0.5 + (10 - 0.5) * random.nextDouble(); // fees between 0.5 and 10
	        double amount = random.nextDouble() * 5;
	        
	        Transaction tx = new Transaction(
	                id, // String id
	                "0x" + id + "SRC", // String srcAddress
	                "0x" + id + "DST", // String desAddress
	                amount, // double amount
	                fees, // double fees
	                FeePriority.values()[random.nextInt(FeePriority.values().length)], // FeePriority priority
	                TxStatus.PENDING, // TxStatus status (default to PENDING)
	                LocalDateTime.now(), // LocalDateTime createdAt
	                null // Wallet wallet (set to null for random transactions)
	        );
	        queue.add(tx);
	    }
	}
}
