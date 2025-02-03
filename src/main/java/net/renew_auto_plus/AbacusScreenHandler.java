package net.renew_auto_plus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class AbacusScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    private final PropertyDelegate propertyDelegate;
    private Boolean tradeListWasUpdated = false;
    private BlockPos blockPos;
    private String currentCompanyName;
    private StallTradeList stallTradeList;
    private DefaultedList<String> ownerNameList;
    private DefaultedList<BlockPos> attachedCratesList;
    private StallTradeList autoTradeList;
 
    public AbacusScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(27),  new ArrayPropertyDelegate(2));
        blockPos = buf.readBlockPos();
        currentCompanyName = buf.readString();
        boolean isTradeListNotNull = buf.readBoolean();
        if(isTradeListNotNull) {
            stallTradeList = StallTradeList.fromPacket(buf);
        }

        int ownerListSize = buf.readByte() & 0xFF;
        ownerNameList = DefaultedList.ofSize(ownerListSize);
        for(int i = 0; i < ownerListSize; i++) {
            ownerNameList.add(i, buf.readString()); // Not sure I like this, likely not clean insertion
        }

        int crateListSize = buf.readByte() & 0xFF;
        attachedCratesList = DefaultedList.ofSize(crateListSize);
        for(int i = 0; i < crateListSize; i++) {
            attachedCratesList.add(i, buf.readBlockPos()); // Not sure I like this, likely not clean insertion
        }

        autoTradeList = StallTradeList.fromPacket(buf);
    }
 
    public AbacusScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(RenewAutoPlusInitialize.ABACUS_SCREEN_HANDLER, syncId);
        checkSize(inventory, 27);
        checkDataCount(propertyDelegate, 2);
        this.propertyDelegate = propertyDelegate;
        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.blockPos = BlockPos.ORIGIN;
        this.currentCompanyName = "";
        this.stallTradeList = null;
        inventory.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, 18 + m * 18));
            }
        }
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
            if (invSlot < 27 ? !this.insertItem(itemStack2, 27, this.slots.size(), true) : !this.insertItem(itemStack2, 0, 27, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0f, player.getWorld().random.nextFloat() * 0.1f + 0.9f);
        return true;
    }

    public int getEmeraldAmount() {
        return propertyDelegate.get(0);
    }

    public int getEmeraldChange() {
        return propertyDelegate.get(1);
    }

    public void setEmeraldAmount(int value) {
        propertyDelegate.set(0, value);
    }

    public void setEmeraldChange(int value) {
        propertyDelegate.set(1, value);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getCurrentCompanyName() {
        return currentCompanyName;
    }

    public StallTradeList getStallTradeList() {
        return stallTradeList;
    }

    public DefaultedList<String> getOwnerNameList() {
        return ownerNameList;
    }

    public DefaultedList<BlockPos> getAttachedCratesList() {
        return attachedCratesList;
    }

    public StallTradeList getAutoTradeList() {
        return autoTradeList;
    }

    public void addOwnerName(String name) {
        if(ownerNameList.size() < MarketManager.MAX_ABACUS_OWNERS) {
            ownerNameList.add(name);
        }
    }

    public void removeOwnerName(String name) {
        int i = 0;
        for(String owner : ownerNameList) {
            if(owner.equals(name)) {
                ownerNameList.remove(i);
                break;
            }
            ++i;
        }
    }

    public String getEmeraldString() {
        int emeraldAmount = this.getEmeraldAmount() >= 999 ? 999 : this.getEmeraldAmount();
        int emeraldChange = this.getEmeraldChange();
        return Integer.toString(emeraldAmount) + "." + (emeraldChange >= 10 ? Integer.toString(emeraldChange) : "0" + Integer.toString(emeraldChange));
    }

    public String getEmeraldString(int emeraldAmount, int emeraldChange, int tradeAmount) {
        if(emeraldChange == 0){
            emeraldAmount = emeraldAmount * tradeAmount;
        }
        else {
            emeraldChange = emeraldChange * tradeAmount;
            emeraldAmount += emeraldChange / 100;
            emeraldChange = emeraldChange % 100;
        }
        return Integer.toString(emeraldAmount) + "." + (emeraldChange >= 10 ? Integer.toString(emeraldChange) : "0" + Integer.toString(emeraldChange));
    }

    public int getEmeraldFromChangeAmount(int emeraldAmount, int emeraldChange, int tradeAmount) {
        if(emeraldChange == 0){
            emeraldAmount = emeraldAmount * tradeAmount;
        }
        else {
            emeraldChange = emeraldChange * tradeAmount;
            emeraldAmount += emeraldChange / 100;
        }
        return emeraldAmount;
    }

    public boolean canBuyWith(int emeraldAmount, int emeraldChange) {
        if(this.getEmeraldAmount() > emeraldAmount) {
            return true;
        }
        else if(this.getEmeraldChange() >= emeraldChange && this.getEmeraldAmount() >= emeraldAmount) {
            return true;
        }
        return false;
    }

    public boolean canBuyWith(int emeraldAmount, int emeraldChange, int tradeAmount) {
        return AbacusBlockEntity.canBuyWithAmount(emeraldAmount, emeraldChange, tradeAmount, this.getEmeraldAmount(), this.getEmeraldChange());
    }

    public int getMaxCanBuy(int emeraldAmount, int emeraldChange) {
        int myEmAmount = this.getEmeraldAmount() * 100 + this.getEmeraldChange();
        emeraldAmount = emeraldAmount * 100 + emeraldChange;
        return myEmAmount / emeraldAmount;
    }

    public boolean shouldUpdateLoadedTrade() {
        return tradeListWasUpdated;
    }

    public void updateLoadedTradeCompleted() {
        tradeListWasUpdated = false;
    }

    public void updateStallTradeList(StallTradeList stallTradeList) {
        if(stallTradeList == null) return;
        this.stallTradeList = stallTradeList; //Wish there was some way to know if copying or moving, might effect performance
        tradeListWasUpdated = true;
    }

    public void updateOwnerNameList(DefaultedList<String> ownerNameList) {
        if(ownerNameList == null) return;
        this.ownerNameList = ownerNameList;
    }

    public void updateAttachedCrateList(DefaultedList<BlockPos> attachedCratesList) {
        if(attachedCratesList == null) return;
        this.attachedCratesList = attachedCratesList;
    }

    public void updateAutoTradeList(StallTradeList autoTradeList) {
        if(autoTradeList == null) return;
        this.autoTradeList = autoTradeList;
    }

    public void addSlots() {
        inventory.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, 18 + m * 18));
            }
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }
}
