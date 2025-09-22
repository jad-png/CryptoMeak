package db.model;

import java.time.LocalDateTime;

public class Transaction {
	private String id;
	private String srcAddress;
	private String desAddress;
	private double amount;
	private double fees;
	private FeePriority priority;
	private TxStatus status;
	private LocalDateTime createdAt;
	
	private Wallet wallet;

	public Transaction(String id, String srcAddress, String desAddress, double amount, double fees, FeePriority priority, TxStatus status, LocalDateTime createdAt, Wallet wallet) {
		this.id = id;
		this.srcAddress = srcAddress;
		this.desAddress = desAddress;
		this.amount = amount;
		this.fees = fees;
		this.priority = priority;
		this.createdAt = createdAt;
		this.wallet = wallet;
	}
	
	public String getId() { return id; }
    public String getSourceAddress() { return srcAddress; }
    public String getDestinationAddress() { return desAddress; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getFees() { return fees; }
    public void setFees(double fees) { this.fees = fees; }
    public FeePriority getPriority() { return priority; }
    public TxStatus getStatus() { return status; }
    public void setStatus(TxStatus status) { this.status = status; }
    public Wallet getWallet() { return wallet; }
}
