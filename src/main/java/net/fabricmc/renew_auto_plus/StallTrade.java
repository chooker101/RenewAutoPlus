package net.fabricmc.renew_auto_plus;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class StallTrade {
    private final ItemStack tradedItem;
    private final int emeraldAmount;
    private final int emeraldChange;
    private final int amountBeforeCommon;
    private int companyItemAmount;
    private int marketItemAmount;
    private boolean isActive; //Set on stall to determine if village has suplies for trade, repurposed by auto trade for UI
    private boolean isSellable; //Set on abacus to determine if company has item to sell
    private boolean isAutoTradeEnabled;

    public StallTrade(ItemStack tradedItem, int emeraldAmount, int emeraldChange, int amountBeforeCommon) {
        this.tradedItem = tradedItem;
        this.emeraldAmount = emeraldAmount;
        this.emeraldChange = emeraldChange;
        this.amountBeforeCommon = amountBeforeCommon;
        companyItemAmount = 0;
        marketItemAmount = 0;
        isActive = true;
        isSellable = true;
        isAutoTradeEnabled = false;
    }

    public ItemStack getTradedItem() {
        return tradedItem;
    }

    public int getEmeraldAmount() {
        return emeraldAmount;
    }

    public int getEmeraldChange() {
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

    public boolean isActive() {
        return isActive;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public boolean isAutoTradeEnabled() {
        return isAutoTradeEnabled;
    }

    public void setCompanyItemAmount(int amount) {
        companyItemAmount = amount;
    }

    public void setMarketItemAmount(int amount) {
        marketItemAmount = amount;
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
