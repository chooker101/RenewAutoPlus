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

public class ClockBlockScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(1); //I guess there is nothing to do about this, Needed for player
    private final PropertyDelegate propertyDelegate;
 
    public ClockBlockScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new ArrayPropertyDelegate(9));
    }
 
    public ClockBlockScreenHandler(int syncId, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(RenewAutoPlusInitialize.CLOCK_BLOCK_SCREEN_HANDLER, syncId);
        checkDataCount(propertyDelegate, 9);
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
        if(id < 0 || id > 8) {
            return false;
        }
        switch (id) {
            case 0: {
                setHours(getHours() - 1);
                break;
            }
            case 1: {
                setMinutes(getMinutes() - 1);
                break;
            }
            case 2: {
                setSeconds(getSeconds() - 1);
                break;
            }
            case 3: {
                setTwentieths(getTwentieths() - 1);
                break;
            }
            case 4: {
                setHours(getHours() + 1);
                break;
            }
            case 5: {
                setMinutes(getMinutes() + 1);
                break;
            }
            case 6: {
                setSeconds(getSeconds() + 1);
                break;
            }
            case 7: {
                setTwentieths(getTwentieths() + 1);
                break;
            }
            case 8: {
                if(getMode() == 0) {
                    setMode(1);
                }
                else {
                    setMode(0);
                }
                break;
            }
        }
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0f, player.getWorld().random.nextFloat() * 0.1f + 0.9f);
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

    public int getHours(){
        return this.propertyDelegate.get(0);
    }

    public int getMinutes(){
        return this.propertyDelegate.get(1);
    }

    public int getSeconds(){
        return this.propertyDelegate.get(2);
    }

    public int getTwentieths(){
        return this.propertyDelegate.get(3);
    }

    public int getDispHours(){
        return this.propertyDelegate.get(4);
    }

    public int getDispMinutes(){
        return this.propertyDelegate.get(5);
    }

    public int getDispSeconds(){
        return this.propertyDelegate.get(6);
    }

    public int getDispTwentieths(){
        return this.propertyDelegate.get(7);
    }

    public int getMode(){
        return this.propertyDelegate.get(8);
    }

    public void setHours(int i){
        this.propertyDelegate.set(0, i);
    }

    public void setMinutes(int i){
        this.propertyDelegate.set(1, i);
    }

    public void setSeconds(int i){
        this.propertyDelegate.set(2, i);
    }

    public void setTwentieths(int i){
        this.propertyDelegate.set(3, i);
    }

    public void setMode(int i){
        this.propertyDelegate.set(8, i);
    }
}
