package system;

import enums.Tier;
import models.User;
import service.AuthService;
import service.WalletService;
import java.util.HashMap;
import java.util.Map;

public class FlipkartSystem {
    private final Map<String, User> users = new HashMap<>();
    private final AuthService authService;
    private final WalletService walletService;

    public FlipkartSystem() {
        this.authService = new AuthService(users);
        this.walletService = new WalletService();
    }

    public void preloadUsers(String[][] userData) {
        for (String[] u : userData) {
            Tier tier = u[1].equalsIgnoreCase("PLUS") ? Tier.PLUS : Tier.REGULAR;
            users.put(u[0], new User(u[0], tier));
        }
    }

    public void setCurrentDay(int day)                        { walletService.setCurrentDay(day); }
    public void login(String name)                            { authService.login(name); }
    public void logout()                                      { authService.logout(); }
    public void earnCoins(String orderId, double amount)      { walletService.earnCoins(authService.getLoggedInUser(), orderId, amount); }
    public void spendCoins(String orderId, int coins)         { walletService.spendCoins(authService.getLoggedInUser(), orderId, coins); }
    public void cancelOrder(String orderId)                   { walletService.cancelOrder(authService.getLoggedInUser(), orderId); }
    public void checkBalance()                                { walletService.checkBalance(authService.getLoggedInUser()); }
    public void viewTransactionHistory()                      { walletService.viewTransactionHistory(authService.getLoggedInUser()); }
    public void simulateFailedSpend(String orderId, int coins){ walletService.simulateFailedSpend(authService.getLoggedInUser(), orderId, coins); }
}