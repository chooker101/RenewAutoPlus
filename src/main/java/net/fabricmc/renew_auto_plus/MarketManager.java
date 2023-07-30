package net.fabricmc.renew_auto_plus;

import java.util.ArrayList;
import java.util.HashMap;

public class MarketManager {
    private static MarketManager static_instance = null;

    public final static int MAX_MARKET_DISTANCE = 500;
    public final static int MAX_COMPANY_DISTANCE = 100;
    public final static int MAX_COMPANIES = 100;
    public final static int MAX_ATTACHED_CRATES = 20;
    public final static int MAX_AUTO_TRADES = 100;
    public final static int MAX_ABACUS_OWNERS = 10;
    public final static int MAX_MARKET_ITEM_STACK = 10000;
    public ArrayList<StallBlockEntity> allMarkets;
    public HashMap<String, AbacusBlockEntity> allCompanies;
    
    private MarketManager() {
        allMarkets = new ArrayList<>();
        allCompanies = new HashMap<>();
    }

    public static synchronized MarketManager instance()
    {
        if (static_instance == null) {
            static_instance = new MarketManager();
        }
        return static_instance;
    }

    public void addCompany(String name, AbacusBlockEntity blockEntity) {
        allCompanies.put(name, blockEntity);
    }

    public void removeCompany(String name) {
        allCompanies.remove(name);
    }

    public int addMarket(StallBlockEntity blockEntity) {
        allMarkets.add(blockEntity);
        return allMarkets.size() - 1;
    }

    public void removeMarket(int index) {
        allMarkets.remove(index);
    }
}
