package net.fabricmc.renew_auto_plus;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class StallTrade {
    private final ItemStack tradedItem;
    private final int emeraldAmount;
    private final int emeraldChange;
    private int itemAmount;
    private boolean isActive; //Set on stall to determine if village has suplies for trade
    private boolean isSellable; //Set on abacus to determine if company has item to sell

    public StallTrade(ItemStack tradedItem, int emeraldAmount, int emeraldChange) {
        this.tradedItem = tradedItem;
        this.emeraldAmount = emeraldAmount;
        this.emeraldChange = emeraldChange;
        itemAmount = 0;
        isActive = true;
        isSellable = true;
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

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int amount) {
        itemAmount = amount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public void setSellable(boolean sell) {
        isSellable = sell;
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeItemStack(this.getTradedItem());
        buf.writeInt(this.getItemAmount());
        buf.writeBoolean(this.isActive());
        buf.writeBoolean(this.isSellable());
        buf.writeInt(this.getEmeraldAmount());
        buf.writeInt(this.getEmeraldChange());
    }

    public static StallTrade fromPacket(PacketByteBuf buf) {
        ItemStack itemStack = buf.readItemStack();
        int itemAmount = buf.readInt();
        boolean isActive = buf.readBoolean();
        boolean isSellable = buf.readBoolean();
        int emeraldAmount = buf.readInt();
        int emeraldChange = buf.readInt();
        StallTrade stallTrade = new StallTrade(itemStack, emeraldAmount, emeraldChange);
        stallTrade.setItemAmount(itemAmount);
        stallTrade.setActive(isActive);
        stallTrade.setSellable(isSellable);
        return stallTrade;
    }
    
}
