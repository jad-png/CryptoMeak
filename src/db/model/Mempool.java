package db.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
	public List<Transaction> getAllTransactions() {
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
}
