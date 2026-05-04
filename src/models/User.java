package models;

import enums.Tier;

public class User {
    private final String name;
    private Tier tier;
    private final Wallet wallet;

    public User(String name, Tier tier){
        this.name = name;
        this.tier = tier;
        this.wallet = new Wallet();
    }


    public String getName(){
        return name;
    }
    public Tier getTier()
    { return tier; }
    public Wallet getWallet()   { return wallet; }
    public void setTier(Tier tier) { this.tier = tier; }
}
