package db.model;

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
}
