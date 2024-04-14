package net.fabricmc.renew_auto_plus;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class StallTrade {
    public enum StockState {
        LOW,
        MEDIUM,
        HIGH
    }
    private final ItemStack tradedItem;
    private final int emeraldAmount;
    private final int fifthEmeraldAmount;
    private final int emeraldChange;
    private final int fifthEmeraldChange;
    private final int amountBeforeCommon;
    private StockState stockState;
    private int companyItemAmount;
    private int marketItemAmount;
    private boolean isActive; //Set on stall to determine if village has suplies for trade, repurposed by auto trade for UI
    private boolean isSellable; //Set on abacus to determine if company has item to sell
    private boolean isAutoTradeEnabled;

    public StallTrade(ItemStack tradedItem, int emeraldAmount, int emeraldChange, int amountBeforeCommon) {
        this.tradedItem = tradedItem;
        this.emeraldAmount = emeraldAmount;
        this.fifthEmeraldAmount = emeraldAmount / 5;
        this.emeraldChange = emeraldChange;
        this.fifthEmeraldChange = Math.round((((float)emeraldChange) / 5.0f) + ((((float)emeraldAmount) / 5.0f) % 1.0f) * 100.0f);
        this.amountBeforeCommon = amountBeforeCommon;
        int halfAmountBeforeCommon = amountBeforeCommon / 2;
        if(marketItemAmount <= halfAmountBeforeCommon) {
            stockState = StockState.LOW;
        }
        else if (marketItemAmount > (amountBeforeCommon + halfAmountBeforeCommon)) {
            stockState = StockState.HIGH;
        }
        else {
            stockState = StockState.MEDIUM;
        }
        companyItemAmount = 0;
        marketItemAmount = 0;
        isActive = true;
        isSellable = true;
        isAutoTradeEnabled = false;
    }

    public ItemStack getTradedItem() {
        return tradedItem;
    }

    public int getBuyEmeraldAmount() {
        if(emeraldAmount < 0) {
            return 0;
        }
        if(stockState == StockState.LOW) { 
            return emeraldAmount + (fifthEmeraldAmount * 2) + ((emeraldChange + (fifthEmeraldChange * 2)) / 100); //One for low stock, one to offset buy from sell
        } else if(stockState == StockState.MEDIUM) {
            return emeraldAmount + fifthEmeraldAmount + ((emeraldChange + fifthEmeraldChange) / 100);
        }
        else if(stockState == StockState.HIGH) {
            return emeraldAmount; 
        }
        return emeraldAmount;
    }

    public int getBuyEmeraldChange() {
        if(emeraldChange < 0) {
            return 0;
        }
        if(stockState == StockState.LOW) {
            return (emeraldChange + (fifthEmeraldChange * 2)) % 100;
        } else if(stockState == StockState.MEDIUM) {
            return (emeraldChange + fifthEmeraldChange) % 100;
        }
        else if(stockState == StockState.HIGH) {
            return emeraldChange; 
        }
        return emeraldChange;
    }

    public int getSellEmeraldAmount() {
        if(emeraldAmount < 0) {
            return 0;
        }
        if(stockState == StockState.LOW) {
            return emeraldAmount + fifthEmeraldAmount + ((emeraldChange + fifthEmeraldChange) / 100);
        } else if(stockState == StockState.MEDIUM) {
            return emeraldAmount;
        }
        else if(stockState == StockState.HIGH) {
            return emeraldAmount - fifthEmeraldAmount - (((100 - emeraldChange) + fifthEmeraldChange) / 100);
        }
        return emeraldAmount;
    }

    public int getSellEmeraldChange() {
        if(emeraldChange < 0) {
            return 0;
        }
        if(stockState == StockState.LOW) {
            return (emeraldChange + fifthEmeraldChange) % 100;
        } else if(stockState == StockState.MEDIUM) {
            return emeraldChange;
        }
        else if(stockState == StockState.HIGH) {
            return (emeraldChange - fifthEmeraldChange) % 100;
        }
        return emeraldChange;
    }

    public int getAmountBeforeCommon() {
        return amountBeforeCommon;
    }

    public int getCompanyItemAmount() {
        return companyItemAmount;
    }

    public int getMarketItemAmount() {
        return marketItemAmount;
    }

    public StockState getStockState() {
        return stockState;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public boolean isAutoTradeEnabled() {
        return isAutoTradeEnabled;
    }

    public void updateStockState() {
        int halfAmountBeforeCommon = amountBeforeCommon / 2;
        if(marketItemAmount <= halfAmountBeforeCommon) {
            stockState = StockState.LOW;
        }
        else if (marketItemAmount > (amountBeforeCommon + halfAmountBeforeCommon)) {
            stockState = StockState.HIGH;
        }
        else {
            stockState = StockState.MEDIUM;
        }
    }

    public void setCompanyItemAmount(int amount) {
        companyItemAmount = amount;
    }

    public void setMarketItemAmount(int amount) {
        marketItemAmount = amount;
        updateStockState();
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }

    public void setSellable(boolean sell) {
        isSellable = sell;
    }

    public void setAutoTradeEnabled(boolean enabled) {
        isAutoTradeEnabled = enabled;
    }

    public void setStockState(StockState stockState) {
        this.stockState = stockState;
    }

    protected int getEmeraldChange() {
        return emeraldChange;
    }

    protected int getEmeraldAmount() {
        return emeraldAmount;
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeItemStack(this.getTradedItem());
        buf.writeInt(this.getEmeraldAmount());
        buf.writeInt(this.getEmeraldChange());
        buf.writeInt(this.getAmountBeforeCommon());
        buf.writeInt(this.getCompanyItemAmount());
        buf.writeInt(this.getMarketItemAmount());
        buf.writeBoolean(this.isActive());
        buf.writeBoolean(this.isSellable());
        buf.writeBoolean(this.isAutoTradeEnabled());

    }

    public static StallTrade fromPacket(PacketByteBuf buf) {
        ItemStack itemStack = buf.readItemStack();
        int emeraldAmount = buf.readInt();
        int emeraldChange = buf.readInt();
        int amountBeforeCommon = buf.readInt();
        int companyItemAmount = buf.readInt();
        int marketItemAmount = buf.readInt();
        boolean isActive = buf.readBoolean();
        boolean isSellable = buf.readBoolean();
        boolean isAutoTradeEnabled = buf.readBoolean();
        StallTrade stallTrade = new StallTrade(itemStack, emeraldAmount, emeraldChange, amountBeforeCommon);
        stallTrade.setCompanyItemAmount(companyItemAmount);
        stallTrade.setMarketItemAmount(marketItemAmount);
        stallTrade.setActive(isActive);
        stallTrade.setSellable(isSellable);
        stallTrade.setAutoTradeEnabled(isAutoTradeEnabled);
        return stallTrade;
    }
    
}
