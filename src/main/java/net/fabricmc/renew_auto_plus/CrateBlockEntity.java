package net.fabricmc.renew_auto_plus;

import java.util.Objects;
import java.util.Map.Entry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;


public class CrateBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory, AutoCloseable {
    private DefaultedList<ItemStack> inventory;
    public String companyName = "";

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.CRATE_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    }

    @Override
    public void close() throws Exception {
        AbacusBlockEntity abacus = MarketManager.instance().allCompanies.get(companyName);
        if(abacus != null) {
            abacus.removeCrate(this.pos);
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.companyName = nbt.getString("CompanyName");
        
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putString("CompanyName", this.companyName);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for(int i = 0; i < inventory.size(); ++i) {
            inventory.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrateScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    public void tryToAttachToAbacus() {
        Entry<String, AbacusBlockEntity> currentClosest = null;
        int closestDistance = MarketManager.MAX_COMPANY_DISTANCE;
        if(MarketManager.instance().allCompanies.isEmpty()) return;
        for (Entry<String, AbacusBlockEntity> company : MarketManager.instance().allCompanies.entrySet()) {
            if(Objects.equals(company.getKey(), this.companyName)) {
                if(this.getPos().isWithinDistance(company.getValue().getPos(), closestDistance)){
                    currentClosest = company;
                    closestDistance = (int)this.getPos().getSquaredDistance(currentClosest.getValue().getPos(), true);
                }
            }
        }
        if(currentClosest != null) {
            currentClosest.getValue().attachCrate(this);
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeString(companyName);
    }
}
