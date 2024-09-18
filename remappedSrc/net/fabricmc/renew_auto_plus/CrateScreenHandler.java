package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class CrateScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private BlockPos blockPos;
    private String currentCompanyName;
 
    public CrateScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(54));
        blockPos = buf.readBlockPos();
        currentCompanyName = buf.readString();
    }
 
    public CrateScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(RenewAutoPlusInitialize.CRATE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 54);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        blockPos = null;
        currentCompanyName = "";
 
        int m;
        int l;
        for (m = 0; m < 6; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, m * 18));
            }
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18 + 38));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142 + 38));
        }
 
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        player.method_48926().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0f, player.method_48926().random.nextFloat() * 0.1f + 0.9f);
        return true;
    }
 
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
 
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
 
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
 
        return newStack;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public String getCurrentCompanyName() {
        return this.currentCompanyName;
    }
}
