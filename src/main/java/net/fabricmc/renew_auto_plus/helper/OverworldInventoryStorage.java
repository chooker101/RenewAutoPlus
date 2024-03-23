package net.fabricmc.renew_auto_plus.helper;

import net.minecraft.inventory.Inventory;

public interface OverworldInventoryStorage {
    public void storeCurrentInventory();
    public Inventory getStoredInventory();
    public boolean hasStoredInventory();
    public void setHasStoredInventory(boolean hasStored);
}
