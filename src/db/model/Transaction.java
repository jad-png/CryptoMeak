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
    
    private Integer size; // Transaction size in bytes (for BTC)
    private Integer gasLimit; // Gas limit (for ETH)
    private Integer gasPrice; // Gas price in gwei (for ETH)
    private Integer satoshiPerByte; // Satoshi per byte (for BTC)

    public Transaction(String id, String srcAddress, String desAddress, double amount, 
                     double fees, FeePriority priority, TxStatus status, 
                     LocalDateTime createdAt, Wallet wallet) {
        this(id, srcAddress, desAddress, amount, fees, priority, status, createdAt, wallet, 
             null, null, null, null);
    }
    
    // Full constructor with fee parameters
    public Transaction(String id, String srcAddress, String desAddress, double amount, 
                     double fees, FeePriority priority, TxStatus status, 
                     LocalDateTime createdAt, Wallet wallet, Integer size, 
                     Integer gasLimit, Integer gasPrice, Integer satoshiPerByte) {
        this.id = id;
        this.srcAddress = srcAddress;
        this.desAddress = desAddress;
        this.amount = amount;
        this.fees = fees;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.wallet = wallet;
        this.size = size;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.satoshiPerByte = satoshiPerByte;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getSourceAddress() { return srcAddress; }
    public String getDesAddress() { return desAddress; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getFees() { return fees; }
    public void setFees(double fees) { this.fees = fees; }
    public FeePriority getPriority() { return priority; }
    public TxStatus getStatus() { return status; }
    public void setStatus(TxStatus status) { this.status = status; }
    public Wallet getWallet() { return wallet; }
    public Integer getSize() { return size; }
    public Integer getGasLimit() { return gasLimit; }
    public Integer getGasPrice() { return gasPrice; }
    public Integer getSatoshiPerByte() { return satoshiPerByte; }
}