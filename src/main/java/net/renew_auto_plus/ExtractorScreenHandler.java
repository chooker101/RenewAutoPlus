package net.renew_auto_plus;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ExtractorScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
 
    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2),  new ArrayPropertyDelegate(4));
    }
 
    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(RenewAutoPlusInitialize.EXTRACTOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        checkDataCount(propertyDelegate, 4);
        this.propertyDelegate = propertyDelegate;
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
 
        this.addSlot(new Slot(inventory, 0, 56, 53));
        this.addSlot(new NonInsertSlot(inventory, 1, 116, 35));
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
            if (invSlot == 1) {
                if (!this.insertItem(itemStack2, inventory.size(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (invSlot == 0 ? !this.insertItem(itemStack2, inventory.size(), slots.size(), false) : (this.isFuel(itemStack2) ? !this.insertItem(itemStack2, 0, 1, false) : (invSlot >= 2 && invSlot < 29 ? !this.insertItem(itemStack2, 29, 38, false) : invSlot >= 29 && invSlot < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
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

    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }

    public int getBreakingProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 22 / j; //Why the fuck is the conversion on this side?
    }

    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.propertyDelegate.get(0) * 13 / i;
    }

    protected boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

    
    public boolean canInsertIntoSlot(int index) {
        return index != 1;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot != this.slots.get(1);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot != this.slots.get(1);
    }
}
