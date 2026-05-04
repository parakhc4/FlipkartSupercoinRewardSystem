package strategy;

public class PlusRewardStrategy implements RewardStrategy {
    private static final int COINS_PER_100 = 4;
    private static final int MAX_COINS = 100;
    // PLUS Tier Rule: Earns 4 coins for every 100 Rs spent. Maximum 100 coins per order.
    @Override
    public int calculateCoins(double orderAmount) {
        int earned = (int)(orderAmount / 100) * COINS_PER_100;
        return Math.min(earned, MAX_COINS);
    }
}