package net.renew_auto_plus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CapacitorScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(1);
    private final PropertyDelegate propertyDelegate;
 
    public CapacitorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new ArrayPropertyDelegate(2));
    }
 
    public CapacitorScreenHandler(int syncId, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(RenewAutoPlusInitialize.CAPACITOR_SCREEN_HANDLER, syncId);
        checkDataCount(propertyDelegate, 2);
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
        this.addProperties(this.propertyDelegate);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if(id < 0 || id > 1) {
            return false;
        }
        switch (id) {
            case 0: {
                if(getCapacitance() <= 1){
                    setCapacitance(99);
                }
                else {
                    setCapacitance(getCapacitance() - 1);
                }
                break;
            }
            case 1: {
                if(getCapacitance() >= 99){
                    setCapacitance(1);
                }
                else {
                    setCapacitance(getCapacitance() + 1);
                }
                break;
            }
        }
        player.method_48926().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0f, player.method_48926().random.nextFloat() * 0.1f + 0.9f);
        return true;
    }
 
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
 
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    public int getCapacitance(){
        return this.propertyDelegate.get(0);
    }

    public int getCharge(){
        return this.propertyDelegate.get(1);
    }

    public int getChargeProgress() {
        return (int)((float)(this.propertyDelegate.get(1)) / ((float)(this.propertyDelegate.get(0)) / 40.0f));
    }

    public void setCapacitance(int i){
        this.propertyDelegate.set(0, i);
    }

    public void setCharge(int i){
        this.propertyDelegate.set(1, i);
    }
}
