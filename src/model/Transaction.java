package model;

import model.enums.Currency;
import model.enums.TxPriority;
import model.enums.TxStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final String srcAddress;
    private final String destAddress;
    private final BigDecimal amount;
    private final BigDecimal fee;
    private final TxStatus status;
    private final TxPriority priority;
    private final Currency currency;
    private final LocalDateTime createdAt;
    private final LocalDateTime confirmedAt;

    public Transaction(UUID id, String srcAddress, String destAddress, BigDecimal amount, BigDecimal fee,
                       TxStatus status, TxPriority priority, Currency currency, LocalDateTime createdAt, LocalDateTime confirmedAt) {
        this.id = id;
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.amount = amount;
        this.fee = fee;
        this.status = status;
        this.priority = priority;
        this.currency = currency;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }


    // getters
    public UUID getId() { return id; }
    public String getSourceAddress() { return srcAddress; }
    public String getDestinationAddress() { return destAddress; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getFee() { return fee; }
    public BigDecimal getTotalAmount() { return amount.add(fee); }
    public TxStatus getStatus() { return status; }
    public TxPriority getPriority() { return priority; }
    public Currency getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }

}
