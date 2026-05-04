package models;

public class CoinBatch {
    private int amount;
    private final int earnedDay;
    private final int expiryDay;

    public CoinBatch(int amount, int earnedDay) {
        this.amount = amount;
        this.earnedDay = earnedDay;
        this.expiryDay = earnedDay + 30;
    }

    public int getAmount() { return amount; }
    public int getEarnedDay() { return earnedDay; }
    public int getExpiryDay() { return expiryDay; }

    public void deduct(int coins) {
        this.amount -= coins;
    }

    public boolean isExpired(int currentDay) {
        return currentDay >= expiryDay;
    }
}
