package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Wallet {
    private final Queue<CoinBatch> coinBatches;  
    private final List<Transaction> transactions;
    // for bonus : In order to manage automatic tier upradation
    private int lifetimeEarnedCoins;

    public Wallet() {
        this.coinBatches = new LinkedList<>();
        this.transactions = new ArrayList<>();
        this.lifetimeEarnedCoins = 0;
    }


    public Queue<CoinBatch> getCoinBatches()      { return coinBatches; }
    public List<Transaction> getTransactions()    { return transactions; }
    public int getLifetimeEarnedCoins()           { return lifetimeEarnedCoins; }
    public void addLifetimeCoins(int coins)       { this.lifetimeEarnedCoins += coins; }

    public void addTransaction(Transaction txn)   { transactions.add(txn); }
    public void addBatch(CoinBatch batch)         { coinBatches.offer(batch); }
}