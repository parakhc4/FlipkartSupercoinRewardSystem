package strategy;

public class RegularRewardStrategy implements RewardStrategy{
    // REGULAR Tier Rule: Earns 2 coins for every 100 Rs spent. Maximum 50 coins per order.
    // Using strategy design pattern to respect SRP/OC and introduce extensibility
    private static final int COINS_PER_100 = 2;
    private static final int MAX_COINS = 100;

    @Override
    public int calculateCoins(double orderAmount){
        int earned = (int)(orderAmount / 100) * COINS_PER_100;
        return Math.min(earned, MAX_COINS);
    }
}
