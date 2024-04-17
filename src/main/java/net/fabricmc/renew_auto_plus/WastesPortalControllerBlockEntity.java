package net.fabricmc.renew_auto_plus;

import net.fabricmc.renew_auto_plus.helper.OverworldInventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WastesPortalControllerBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
    protected DefaultedList<ItemStack> chargeInventory;
    protected static final int maxCharge = 11;
    protected int currentCharge = 0;
    protected boolean isLit = false;
    //Only used for ruined, not ideal but less cringe than
    protected boolean isRuined = true;
    protected boolean wasCharged = false;
    protected int currentTicksBetweenCharge = 0;

    public final PropertyDelegate propertyDelegate = new PropertyDelegate(){
        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return WastesPortalControllerBlockEntity.this.currentCharge;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    WastesPortalControllerBlockEntity.this.currentCharge = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    WastesPortalControllerBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.WASTES_PORTAL_CONTROLLER_ENTITY, pos, state);
        this.chargeInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.chargeInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.chargeInventory);
        this.currentCharge = nbt.getShort("CurrentCharge");
     }
  
     public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("CurrentCharge", (short)this.currentCharge);
        Inventories.writeNbt(nbt, this.chargeInventory);
     }

    @Override
    public int size() {
        return this.chargeInventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : chargeInventory) {
            if(!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if(slot > 0) {
            return ItemStack.EMPTY;
        }
        return chargeInventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot > 0) {
            return ItemStack.EMPTY;
        }
        return Inventories.splitStack(this.chargeInventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot > 0) {
            return;
        }
        this.chargeInventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for(int i = 0; i < chargeInventory.size(); ++i) {
            chargeInventory.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        Direction facing = getCachedState().get(WastesPortalControllerBlock.FACING);
        if(facing == Direction.DOWN) {}
        return new int[]{};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction var3) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        //((OverworldInventoryStorage)playerInventory.player).storeCurrentInventory();
        Inventory storedInventory = ((OverworldInventoryStorage)playerInventory.player).getStoredInventory();
        return new WastesPortalControllerScreenHandler(syncId, playerInventory, this, storedInventory, this.propertyDelegate);
    }

    public boolean powerNextPortalBlock(boolean powered) {
        Direction facing = getCachedState().get(WastesPortalControllerBlock.FACING);
        Direction right = facing.rotateYClockwise();
        Direction left = facing.rotateYCounterclockwise();
        BlockState state = null;
        BlockPos offsetPos = new BlockPos(pos);
        switch (currentCharge) {
            case 0:
                break;
            case 1:
                offsetPos = offsetPos.offset(right, 1);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 2:
                offsetPos = offsetPos.offset(right, 2).offset(Direction.UP, 1);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 3:
                offsetPos = offsetPos.offset(right, 2).offset(Direction.UP, 2);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 4:
                offsetPos = offsetPos.offset(right, 2).offset(Direction.UP, 3);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 5:
                offsetPos = offsetPos.offset(right, 1).offset(Direction.UP, 4);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 6:
                offsetPos = offsetPos.offset(Direction.UP, 4);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 7:
                offsetPos = offsetPos.offset(left, 1).offset(Direction.UP, 4);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 8:
                offsetPos = offsetPos.offset(left, 2).offset(Direction.UP, 3);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 9:
                offsetPos = offsetPos.offset(left, 2).offset(Direction.UP, 2);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 10:
                offsetPos = offsetPos.offset(left, 2).offset(Direction.UP, 1);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            case 11:
                offsetPos = offsetPos.offset(left, 1);
                state = world.getBlockState(offsetPos);
                if(state.isOf(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME)){
                    world.setBlockState(offsetPos, state.with(WastesPortalFrameBlock.POWERED, powered));
                }
                else {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void tryToLightPortal(World world, BlockPos pos, BlockState state) {
        if(isLit) {
            return;
        }
        if(isRuined) {
            BlockPattern.Result result = WastesPortalControllerBlock.getCompletedFramePattern().searchAround(world, pos);
            if (result != null) {
                BlockPos topLeft = new BlockPos(pos);
                Direction facing = state.get(WastesPortalControllerBlock.FACING);
                Direction right = facing.rotateYClockwise();
                topLeft = topLeft.offset(right, 1);
                topLeft = topLeft.offset(Direction.UP, 3);
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        world.setBlockState(topLeft.offset(right.getOpposite(), j).offset(Direction.DOWN, i), RenewAutoPlusInitialize.WASTES_PORTAL_SURFACE.getDefaultState().with(WastesPortalSurfaceBlock.AXIS, facing.getAxis()).with(WastesPortalSurfaceBlock.X_BLOCK_POS, j).with(WastesPortalSurfaceBlock.Y_BLOCK_POS, i), Block.NOTIFY_LISTENERS);
                    }
                }
                isLit = true;
            }
        }
        else {

        }
    }

    public boolean checkIsStillComplete(World world, BlockPos pos) {
        BlockPattern.Result result = null;
        if(isRuined) {
            result = WastesPortalControllerBlock.getCompletedFramePattern().searchAround(world, pos);
        }
        else {
            result = WastesPortalControllerBlock.getCompletedFramePattern().searchAround(world, pos);
        }
        return result != null;
    }

    public void unlightPortal(World world, BlockPos pos, BlockState state) {
        if(!isLit) {
            return;
        }
        BlockPos topLeft = new BlockPos(pos);
        Direction facing = null;
        if(isRuined) {
            facing = state.get(WastesPortalControllerBlock.FACING);
        }
        else {
            facing = state.get(WastesPortalControllerBlock.FACING);
        }
        Direction right = facing.rotateYClockwise();
        topLeft = topLeft.offset(right, 1);
        topLeft = topLeft.offset(Direction.UP, 3);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                world.setBlockState(topLeft.offset(right.getOpposite(), j).offset(Direction.DOWN, i), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
        }
        isLit = false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, WastesPortalControllerBlockEntity blockEntity) {
        if(!blockEntity.wasCharged) {
            if(blockEntity.isLit) {
                if(!blockEntity.checkIsStillComplete(world, pos)) {
                    //blockEntity.unlightPortal(world, pos, state);
                }
            }
            int diamondCount = blockEntity.count(Items.DIAMOND);
            if(blockEntity.isRuined) {
                if(blockEntity.currentCharge >= WastesPortalControllerBlockEntity.maxCharge) {
                    blockEntity.wasCharged = true;
                    blockEntity.currentTicksBetweenCharge = 0;
                }
            }
            if(diamondCount >= 5 && blockEntity.currentCharge < WastesPortalControllerBlockEntity.maxCharge) {
                if(blockEntity.currentTicksBetweenCharge >= 20) {
                    blockEntity.currentCharge++;
                    if(blockEntity.powerNextPortalBlock(true)) {
                        blockEntity.removeStack(0, 5);
                    }
                    else {
                        blockEntity.currentCharge--;
                    }
                    blockEntity.currentTicksBetweenCharge = 0;
                }
                blockEntity.currentTicksBetweenCharge++;
            }
        }
        else {
            if(blockEntity.currentTicksBetweenCharge >= 40) {
                if(blockEntity.currentCharge > 0) {
                    blockEntity.powerNextPortalBlock(false);
                    blockEntity.currentCharge--;
                    blockEntity.currentTicksBetweenCharge = 0;
                }
                else {
                    blockEntity.currentCharge = 0;
                    blockEntity.wasCharged = false;
                    blockEntity.currentTicksBetweenCharge = 0;
                }
            }
            blockEntity.currentTicksBetweenCharge++;
        }
        if(blockEntity.currentCharge >= WastesPortalControllerBlockEntity.maxCharge && !blockEntity.isLit) { 
            blockEntity.tryToLightPortal(world, pos, state);
        }
    }
}
