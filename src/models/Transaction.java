package models;

import enums.TransactionStatus;
import enums.TransactionType;

public class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final int amount;
    private final String orderId;
    private final int day;
    private TransactionStatus status;

    public Transaction(String transactionId, TransactionType type, int amount, String orderId, int day) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.orderId = orderId;
        this.day = day;
        this.status = TransactionStatus.PENDING;
    }
    public void markInProgress()  { this.status = TransactionStatus.IN_PROGRESS; }
    public void markCompleted()   { this.status = TransactionStatus.COMPLETED; }
    public void markRejected()    { this.status = TransactionStatus.REJECTED; }
    public void markRolledBack()  { this.status = TransactionStatus.ROLLED_BACK; }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public int getAmount()           { return amount; }
    public String getOrderId()       { return orderId; }
    public int getDay()              { return day; }
    public TransactionStatus getStatus() { return status; }

    @Override
    public String toString() {
        String orderInfo = (orderId != null) ? " (" + orderId + ")" : "";
        return "[Day " + day + "] " + type + " " + amount + orderInfo + " - " + status;
    }

}
