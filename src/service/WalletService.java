package service;

import enums.*;
import exceptions.*;
import models.*;
import strategy.*;

import java.util.*;

public class WalletService {

    private int currentDay;

    public WalletService() {
        this.currentDay = 1;
    }

    public void setCurrentDay(int day) {
        this.currentDay = day;
    }

    // EARN 

    public void earnCoins(User user, String orderId, double orderAmount) {
        Wallet wallet = user.getWallet();

        try {
            RewardStrategy strategy = RewardStrategyFactory.getStrategy(user.getTier());
            int earned = strategy.calculateCoins(orderAmount);

            Transaction txn = new Transaction(generateId(), TransactionType.CREDIT, earned, orderId, currentDay);
            wallet.addTransaction(txn);
            txn.markInProgress();

            CoinBatch batch = new CoinBatch(earned, currentDay);
            wallet.addBatch(batch);
            wallet.addLifetimeCoins(earned);

            if (user.getTier() == Tier.REGULAR && wallet.getLifetimeEarnedCoins() >= 300) {
                user.setTier(Tier.PLUS);
                System.out.println("[TIER] " + user.getName() + " upgraded to PLUS!");
            }

            txn.markCompleted();
            System.out.println("[EARN] Earned " + earned + " coins. (Expires Day " + batch.getExpiryDay() + ") Balance: " + getValidBalance(user));

        } catch (Exception e) {
            System.out.println("[ERROR] earnCoins failed: " + e.getMessage());
            throw e;
        }
    }

    // spend 

    public void spendCoins(User user, String orderId, int coinsToSpend) {
        Wallet wallet = user.getWallet();

        // First expire stale batches
        expireOldBatches(user);

        Transaction txn = new Transaction(generateId(), TransactionType.DEBIT, coinsToSpend, orderId, currentDay);
        wallet.addTransaction(txn);
        txn.markInProgress();

        int validBalance = getValidBalance(user);
        if (validBalance < coinsToSpend) {
            txn.markRejected();
            throw new InsufficientCoinsException("[SPEND] Insufficient coins. Available: " + validBalance + ", Required: " + coinsToSpend);
        }


        int remaining = coinsToSpend;
        Queue<CoinBatch> batches = wallet.getCoinBatches();
        for (CoinBatch batch : batches) {
            if (remaining <= 0) break;
            int deduct = Math.min(batch.getAmount(), remaining);
            batch.deduct(deduct);
            remaining -= deduct;
        }

        // Remove empty batches
        batches.removeIf(b -> b.getAmount() == 0);

        txn.markCompleted();
        System.out.println("[SPEND] Spent " + coinsToSpend + " coins. Balance: " + getValidBalance(user));
    }

    // cancelled transactions and refund

    public void cancelOrder(User user, String orderId) {
        Wallet wallet = user.getWallet();

        // Find the original DEBIT transaction for this orderId
        Transaction originalDebit = wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.DEBIT
                        && t.getOrderId().equals(orderId)
                        && t.getStatus() == TransactionStatus.COMPLETED)
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("No completed debit found for order: " + orderId));

        int refundAmount = originalDebit.getAmount();

        Transaction refundTxn = new Transaction(generateId(), TransactionType.REFUND, refundAmount, orderId, currentDay);
        wallet.addTransaction(refundTxn);
        refundTxn.markInProgress();

        // Fresh batch with new 30-day expiry from today
        CoinBatch refundBatch = new CoinBatch(refundAmount, currentDay);
        wallet.addBatch(refundBatch);

        refundTxn.markCompleted();
        System.out.println("[REFUND] Refunded " + refundAmount + " coins for order " + orderId + ". Balance: " + getValidBalance(user));
    }

    // balance

    public int getValidBalance(User user) {
        return user.getWallet().getCoinBatches().stream()
                .mapToInt(CoinBatch::getAmount)
                .sum();
    }

    public void checkBalance(User user) {
        int balance = getValidBalance(user);
        System.out.println("[BALANCE] " + user.getName() + " | Coins: " + balance + " | Tier: " + user.getTier());
    }

    // transcatio histroy

    public void viewTransactionHistory(User user) {
        System.out.println("\n[TRANSACTION HISTORY] " + user.getName());
        user.getWallet().getTransactions().forEach(System.out::println);
        System.out.println();
    }

    // 

    public void simulateFailedSpend(User user, String orderId, int coinsToSpend) {
        Wallet wallet = user.getWallet();
        expireOldBatches(user);

        Transaction txn = new Transaction(generateId(), TransactionType.DEBIT, coinsToSpend, orderId, currentDay);
        wallet.addTransaction(txn);
        txn.markInProgress();

        Queue<CoinBatch> snapshot = new LinkedList<>();
        for (CoinBatch b : wallet.getCoinBatches()) {
            snapshot.offer(new CoinBatch(b.getAmount(), b.getEarnedDay()));
        }

        // Simulate deduction
        int remaining = coinsToSpend;
        for (CoinBatch batch : wallet.getCoinBatches()) {
            if (remaining <= 0) break;
            int deduct = Math.min(batch.getAmount(), remaining);
            batch.deduct(deduct);
            remaining -= deduct;
        }
        wallet.getCoinBatches().removeIf(b -> b.getAmount() == 0);

        // Simulate failure — restore snapshot
        System.out.println("[SIMULATE] Failure at IN_PROGRESS. Rolling back...");
        wallet.getCoinBatches().clear();
        wallet.getCoinBatches().addAll(snapshot);

        txn.markRolledBack();
        System.out.println("[SIMULATE] Rolled back. Balance restored: " + getValidBalance(user));
    }



    private void expireOldBatches(User user) {
        Wallet wallet = user.getWallet();
        Iterator<CoinBatch> it = wallet.getCoinBatches().iterator();
        while (it.hasNext()) {
            CoinBatch batch = it.next();
            if (batch.isExpired(currentDay)) {
                Transaction expiryTxn = new Transaction(generateId(), TransactionType.EXPIRY, batch.getAmount(), null, currentDay);
                wallet.addTransaction(expiryTxn);
                expiryTxn.markCompleted();
                System.out.println("[EXPIRY] Expired " + batch.getAmount() + " coins from Day " + batch.getEarnedDay());
                it.remove();
            }
        }
    }

    private void completeTxn(Transaction txn, int actualAmount) {
        // Since amount is final, we track it in toString — already set correctly
        txn.markCompleted();
    }

    private String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}