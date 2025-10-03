package model;

import java.util.ArrayList;
import java.util.List;

public class Mempool {
    private List<Transaction> pendingTxs;
    private static final int ESTIMATED_MINUTES_PER_POSITION = 10;

    public Mempool() {
        this.pendingTxs = new ArrayList<>();
    }


    public Mempool(List<Transaction> pendingTxs) {
        this.pendingTxs = pendingTxs;
    }

    public List<Transaction> getPendingTxs() { return this.pendingTxs; }

    public void setPendingTxs(List<Transaction> pendingTxs) { this.pendingTxs = pendingTxs; }
}
