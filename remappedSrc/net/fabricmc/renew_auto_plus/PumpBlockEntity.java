package net.fabricmc.renew_auto_plus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    public static final int WATER_TYPE = 0;
    public static final int LAVA_TYPE = 1;

    private DefaultedList<ItemStack> inventory;
    private boolean wasFullLastFrame;

    public int currentPumpingProgress;
    public int totalPumpingTime;
    public int burnTime;
    public int fuelTime;
    public int liquidType;
    //public int soundTime;
    public float angleCounter = 180.0f;
    public final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return PumpBlockEntity.this.burnTime;
                }
                case 1: {
                    return PumpBlockEntity.this.fuelTime;
                }
                case 2: {
                    return PumpBlockEntity.this.currentPumpingProgress;
                }
                case 3: {
                    return PumpBlockEntity.this.totalPumpingTime;
                }
                case 4: {
                    return PumpBlockEntity.this.liquidType;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    PumpBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    PumpBlockEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    PumpBlockEntity.this.currentPumpingProgress = value;
                    break;
                }
                case 3: {
                    PumpBlockEntity.this.totalPumpingTime = value;
                    break;
                }
                case 4: {
                    PumpBlockEntity.this.liquidType = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.PUMP_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        wasFullLastFrame = false;
        totalPumpingTime = 1600;
        currentPumpingProgress = 0;
        burnTime = 0;
        fuelTime = 0;
        //soundTime = 80;
        liquidType = WATER_TYPE;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.fuelTime = nbt.getShort("FuelTime");
        this.currentPumpingProgress = nbt.getShort("CurrentPumpingProgress");
        this.totalPumpingTime = nbt.getShort("TotalPumpingTime");
        this.liquidType = nbt.getShort("LiquidType");
        this.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(this.inventory.get(0), 0);
     }
  
     public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("FuelTime", (short)this.fuelTime);
        nbt.putShort("CurrentPumpingProgress", (short)this.currentPumpingProgress);
        nbt.putShort("TotalPumpingTime", (short)this.totalPumpingTime);
        nbt.putShort("LiquidType", (short)this.liquidType);
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
        if(slot == 2) {
            wasFullLastFrame = true;
        }
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
        if (slot == 2) {
            return !this.inventory.get(2).isEmpty();
        }
        if (slot == 1) {
            ItemStack itemStack = this.inventory.get(1);
            return stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
        }
        if (slot == 0) {
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
        }
        return false;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        if (side == facing.rotateYClockwise()) {
            return new int[]{2};
        }
        if (side == Direction.UP) {
            return new int[]{1};
        }
        if (side == facing.rotateYCounterclockwise()){
            return new int[]{0};
        }
        return new int[]{};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction var3) {
        if (slot == 2) {
            return false;
        }
        if (slot == 1) {
            ItemStack itemStack = this.inventory.get(1);
            return stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
        }
        if (slot == 0) {
            ItemStack itemStack = this.inventory.get(0);
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.isOf(Items.LAVA_BUCKET) && !itemStack.isOf(Items.LAVA_BUCKET);
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 2) {
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
        return new PumpScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public boolean isPumping() {
        return burnTime > 0;
    }

    public static boolean isLiquid(BlockState state) {
        return state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
    }

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity blockEntity) {
        BlockPos downBlock = pos.offset(state.get(PumpBlock.FACING)).down();
        BlockState downBlockState = world.getBlockState(downBlock);
        if(!isLiquid(downBlockState)) {
            return;
        }

        ItemStack fuelStack = blockEntity.getStack(0);
        ItemStack inputStack = blockEntity.getStack(1);
        ItemStack resultStack = blockEntity.getStack(2);
        boolean blockUpdate = false;

        if (!resultStack.isEmpty()) {
            blockEntity.wasFullLastFrame = true;
        }
        else if(blockEntity.wasFullLastFrame) {
            blockEntity.wasFullLastFrame = false;
            blockEntity.currentPumpingProgress = 0;
            state = (BlockState)state.with(PumpBlock.LEVEL, 0);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }

        //blockEntity.soundTime = blockEntity.soundTime + 1;
        if(blockEntity.burnTime > 0) {
            blockEntity.burnTime = blockEntity.burnTime - 1;
        }
        else if(resultStack.isEmpty() && inputStack.getItem() == Items.BUCKET) {
            if((!fuelStack.isEmpty()) && AbstractFurnaceBlockEntity.canUseAsFuel(fuelStack)) {
                blockUpdate = true;
                if(downBlockState.isOf(Blocks.WATER)){
                    blockEntity.liquidType = WATER_TYPE;
                    state = (BlockState)state.with(PumpBlock.LIQUID_TYPE, true);
                }
                else if(downBlockState.isOf(Blocks.LAVA)){
                    blockEntity.liquidType  = LAVA_TYPE;
                    state = (BlockState)state.with(PumpBlock.LIQUID_TYPE, false);
                }
                state = (BlockState)state.with(PumpBlock.POWERED, true);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
                blockEntity.fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
                blockEntity.burnTime = blockEntity.fuelTime;
                fuelStack.decrement(1);
                if (fuelStack.isEmpty()) {
                    blockEntity.inventory.set(0, ItemStack.EMPTY);
                }
            }
        }

        if (blockEntity.burnTime == 0) {
            if(!(!fuelStack.isEmpty() && blockEntity.currentPumpingProgress < blockEntity.totalPumpingTime)){
                blockUpdate = true;
                state = (BlockState)state.with(PumpBlock.POWERED, false);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
        }

        if (downBlockState != null) {
            if(!resultStack.isEmpty()) {
                return;
            }

            if(inputStack.getItem() != Items.BUCKET) {
                return;
            }
            
            if(blockEntity.burnTime > 0 && blockEntity.currentPumpingProgress < blockEntity.totalPumpingTime) {
                blockUpdate = true;
                blockEntity.currentPumpingProgress = blockEntity.currentPumpingProgress + 1;
                int liquidLevel = (int)((float)blockEntity.currentPumpingProgress / ((float)blockEntity.totalPumpingTime / 4.0f));
                state = (BlockState)state.with(PumpBlock.LEVEL, liquidLevel);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
                //if(blockEntity.soundTime >= 80) {
                //    blockEntity.soundTime = 0;
                //}
            }
            
            if (blockEntity.currentPumpingProgress >= blockEntity.totalPumpingTime && resultStack.isEmpty()) {
                if(world instanceof ServerWorld) {
                    if(resultStack.isEmpty()) {
                        blockUpdate = true;
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.7f, 1.0f);
                        inputStack.decrement(1);
                        if (inputStack.isEmpty()) {
                            blockEntity.inventory.set(1, ItemStack.EMPTY);
                        }
                        if(downBlockState.isOf(Blocks.WATER)){
                            blockEntity.setStack(2, Items.WATER_BUCKET.getDefaultStack());
                        }
                        else if(downBlockState.isOf(Blocks.LAVA)){
                            blockEntity.setStack(2, Items.LAVA_BUCKET.getDefaultStack());
                        }
                    }
                }
            }
        }
        if(blockUpdate) {
           ExtractorBlockEntity.markDirty(world, pos, state);
        }
        //if(blockEntity.soundTime > 80) {
        //    blockEntity.soundTime = 80;
        //}
        return;
    }
}
