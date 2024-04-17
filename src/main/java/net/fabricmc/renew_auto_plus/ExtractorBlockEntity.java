package net.fabricmc.renew_auto_plus;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ExtractorBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    private DefaultedList<ItemStack> inventory;
    public int currentBreakingProgress;
    public int totalBreakingTime;
    public int burnTime;
    public int fuelTime;
    public int soundTime;
    public final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return ExtractorBlockEntity.this.burnTime;
                }
                case 1: {
                    return ExtractorBlockEntity.this.fuelTime;
                }
                case 2: {
                    return ExtractorBlockEntity.this.currentBreakingProgress;
                }
                case 3: {
                    return ExtractorBlockEntity.this.totalBreakingTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    ExtractorBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    ExtractorBlockEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    ExtractorBlockEntity.this.currentBreakingProgress = value;
                    break;
                }
                case 3: {
                    ExtractorBlockEntity.this.totalBreakingTime = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.EXTRACTOR_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        totalBreakingTime = 1600;
        currentBreakingProgress = 0;
        burnTime = 0;
        fuelTime = 0;
        soundTime = 80;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.fuelTime = nbt.getShort("FuelTime");
        this.currentBreakingProgress = nbt.getShort("CurrentBreakingProgress");
        this.totalBreakingTime = nbt.getShort("TotalBreakingTime");
        this.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(this.inventory.get(1), 0);
     }
  
     public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("FuelTime", (short)this.fuelTime);
        nbt.putShort("CurrentBreakingProgress", (short)this.currentBreakingProgress);
        nbt.putShort("TotalBreakingTime", (short)this.totalBreakingTime);
        Inventories.writeNbt(nbt, this.inventory);
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
        boolean bl = !stack.isEmpty() && ItemStack.canCombine(stack, itemStack);
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
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 1) {
            return false;
        }
        if (slot == 0) {
            ItemStack itemStack = this.inventory.get(0);
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.isOf(Items.LAVA_BUCKET) && !itemStack.isOf(Items.LAVA_BUCKET);
        }
        return true;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{1};
        }
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction var3) {
        if (slot == 1) {
            return false;
        }
        if (slot == 0) {
            ItemStack itemStack = this.inventory.get(0);
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 1) {
            return true;
        }
        return false;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ExtractorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    //Need to add Glowstone/Nether shit
    public static boolean isAnOre(BlockState blockState) {
        return blockState.isIn(BlockTags.GOLD_ORES) || blockState.isIn(BlockTags.IRON_ORES) || blockState.isIn(BlockTags.DIAMOND_ORES) || blockState.isIn(BlockTags.REDSTONE_ORES) || blockState.isIn(BlockTags.LAPIS_ORES) || blockState.isIn(BlockTags.COAL_ORES) || blockState.isIn(BlockTags.EMERALD_ORES) || blockState.isIn(BlockTags.COPPER_ORES);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtractorBlockEntity blockEntity) {
        BlockPos frontBlock = pos.offset(state.get(ExtractorBlock.FACING));
        BlockState frontBlockState = world.getBlockState(frontBlock);
        boolean blockUpdate = false;
        if(!isAnOre(frontBlockState)) {
            return;
        }
        blockEntity.soundTime = blockEntity.soundTime + 1;
        if(blockEntity.burnTime > 0) {
            blockEntity.burnTime = blockEntity.burnTime - 1;
        }
        else if(blockEntity.getStack(1).isEmpty()) {
            ItemStack fuelStack = blockEntity.getStack(0);
            if((!fuelStack.isEmpty()) && AbstractFurnaceBlockEntity.canUseAsFuel(fuelStack)) {
                blockUpdate = true;
                blockEntity.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
                blockEntity.burnTime = blockEntity.fuelTime;
                fuelStack.decrement(1);
                if (fuelStack.isEmpty()) {
                    blockEntity.inventory.set(0, ItemStack.EMPTY);
                }
            }
        }

        if (frontBlockState != null) {
            if(!blockEntity.getStack(1).isEmpty()) {
                return;
            }
   
            float hardness = frontBlockState.getHardness(world, frontBlock);
            if (hardness == -1.0F) {
               return;
            } else {
                if(blockEntity.burnTime > 0 && blockEntity.currentBreakingProgress < blockEntity.totalBreakingTime) {
                    blockEntity.currentBreakingProgress = blockEntity.currentBreakingProgress + 1;
                    if(blockEntity.soundTime >= 80) {
                        world.playSound(null, pos, RenewAutoPlusInitialize.EXTRACTOR_ON_SE, SoundCategory.BLOCKS, 0.7f, 1.0f);
                        blockEntity.soundTime = 0;
                    }
                }
            }
            
            if (blockEntity.currentBreakingProgress >= blockEntity.totalBreakingTime) {
                if(world instanceof ServerWorld) {
                    blockUpdate = true;
                    blockEntity.currentBreakingProgress = 0;
                    List<ItemStack> droppedStacks = Block.getDroppedStacks(frontBlockState, (ServerWorld)world, frontBlock, null);
                    if(blockEntity.getStack(1).isEmpty()){
                        blockEntity.setStack(1, droppedStacks.get(0));
                    }
                }
            }
            world.setBlockBreakingInfo(blockEntity.hashCode(), frontBlock, (int)((float)blockEntity.currentBreakingProgress * 10.0f / (float)blockEntity.totalBreakingTime) - 1);
        }
        if(blockUpdate) {
           ExtractorBlockEntity.markDirty(world, pos, state);
        }
        if(blockEntity.soundTime > 80) {
           blockEntity.soundTime = 80;
        }
        return;
    }
}
