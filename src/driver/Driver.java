package driver;

import system.FlipkartSystem;

public class Driver {
    public static void main(String[] args) {
        FlipkartSystem system = new FlipkartSystem();

        system.preloadUsers(new String[][]{{"u1", "REGULAR"}, {"u2", "PLUS"}});

        // User 1 TEST CASES
        system.setCurrentDay(1);
        system.login("u1");
        system.earnCoins("O-101", 3000);   // Earn 50, expires Day 31

        system.setCurrentDay(10);
        system.earnCoins("O-102", 5000);   // Earn 50, expires Day 40

        system.setCurrentDay(15);
        system.spendCoins("O-103", 30);    // Spend 30, balance 70

        system.setCurrentDay(35);
        system.checkBalance();             // 50 (20 from batch1 expired)
        try {
            system.spendCoins("O-104", 100);  
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        system.setCurrentDay(36);
        system.cancelOrder("O-105");       // Refund 40

        system.viewTransactionHistory();

        system.logout();

        // User 2 TEST CASES 
        system.login("u2");
        system.earnCoins("O-201", 2000);   // PLUS: 80 coins
        system.checkBalance();

        // bonus
        system.simulateFailedSpend("O-202", 20);

        system.logout();
    }
}