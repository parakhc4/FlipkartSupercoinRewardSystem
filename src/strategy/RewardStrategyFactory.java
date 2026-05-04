package strategy;

import enums.Tier;

public class RewardStrategyFactory {
    public static RewardStrategy getStrategy(Tier tier) {
        switch (tier) {
            case PLUS:    return new PlusRewardStrategy();
            case REGULAR:  return new RegularRewardStrategy();
            // case GOLD: return new GoldRewardStrategy();
            default:      return new RegularRewardStrategy();
        }
    }
}