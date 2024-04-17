package net.fabricmc.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CapacitorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public int maxCapacitance = 1;
    public int currentCharge = 0;
    public boolean wasCharged = false;

    public final PropertyDelegate propertyDelegate = new PropertyDelegate(){
        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return CapacitorBlockEntity.this.maxCapacitance;
                }
                case 1: {
                    return CapacitorBlockEntity.this.currentCharge;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    CapacitorBlockEntity.this.maxCapacitance = value;
                    CapacitorBlockEntity.this.currentCharge = 0;
                    break;
                }
                case 1: {
                    CapacitorBlockEntity.this.currentCharge = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    CapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.CAPACITOR_BLOCK_ENTITY, pos, state);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.maxCapacitance = nbt.getShort("MaxCapacitance");
        this.currentCharge = nbt.getShort("CurrentCharge");
        this.wasCharged = nbt.getBoolean("WasCharged");
    }
  
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("MaxCapacitance", (short)this.maxCapacitance);
        nbt.putShort("CurrentCharge", (short)this.currentCharge);
        nbt.putBoolean("WasCharged", this.wasCharged);
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CapacitorScreenHandler(i, playerInventory, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, CapacitorBlockEntity blockEntity) {
        boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos);
        if(isReceivingRedstonePower && !blockEntity.wasCharged) {
            blockEntity.currentCharge = blockEntity.currentCharge + 1;
            blockEntity.wasCharged = true;
            CapacitorBlockEntity.markDirty(world, pos, state);
        }
        else if(!isReceivingRedstonePower) {
            blockEntity.wasCharged = false;
        }

        if(blockEntity.currentCharge >= blockEntity.maxCapacitance) {
            state = (BlockState)state.with(CapacitorBlock.POWER, 4);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
            world.scheduleBlockTick(pos, state.getBlock(), 2);
            blockEntity.currentCharge = 0;
            blockEntity.wasCharged = true;
            CapacitorBlockEntity.markDirty(world, pos, state);
        }
        else {
            int lightLevel = (int)((float)blockEntity.currentCharge / ((float)blockEntity.maxCapacitance / 4.0f));
            state = (BlockState)state.with(CapacitorBlock.POWER, lightLevel);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        return;
    }
}
