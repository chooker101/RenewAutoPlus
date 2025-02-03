package net.renew_auto_plus;

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

public class ClockBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public ClockBlockTime setTime = new ClockBlockTime();
    public ClockBlockTime currentTime = new ClockBlockTime();
    public int toggleMode = 0;
    public boolean setTimeUpdated = false;
    public boolean timerDone = false;
    public boolean timerStarted = false;

    public final PropertyDelegate propertyDelegate = new PropertyDelegate(){
        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return ClockBlockEntity.this.setTime.hours;
                }
                case 1: {
                    return ClockBlockEntity.this.setTime.minutes;
                }
                case 2: {
                    return ClockBlockEntity.this.setTime.seconds;
                }
                case 3: {
                    return ClockBlockEntity.this.setTime.twentieths;
                }
                case 4: {
                    return ClockBlockEntity.this.currentTime.hours;
                }
                case 5: {
                    return ClockBlockEntity.this.currentTime.minutes;
                }
                case 6: {
                    return ClockBlockEntity.this.currentTime.seconds;
                }
                case 7: {
                    return ClockBlockEntity.this.currentTime.twentieths;
                }
                case 8: {
                    return ClockBlockEntity.this.toggleMode;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if(index >= 0 && index <= 3) {
                setTimeUpdated = true;
            }
            switch (index) {
                case 0: {
                    ClockBlockEntity.this.setTime.setHours(value);
                    break;
                }
                case 1: {
                    ClockBlockEntity.this.setTime.setMinutes(value);
                    break;
                }
                case 2: {
                    ClockBlockEntity.this.setTime.setSeconds(value);
                    break;
                }
                case 3: {
                    ClockBlockEntity.this.setTime.setTwentieths(value);
                    break;
                }
                case 4: {
                    break;
                }
                case 5: {
                    break;
                }
                case 6: {
                    break;
                }
                case 7: {
                    break;
                }
                case 8: {
                    ClockBlockEntity.this.toggleMode = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 9;
        }
    };

    ClockBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.CLOCK_BLOCK_ENTITY, pos, state);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        setTime.readNbt("setTime", nbt);
        currentTime.readNbt("currentTime", nbt);
        this.toggleMode = nbt.getShort("ToggleMode");
        this.timerStarted = nbt.getBoolean("TimerStarted");
    }
  
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        setTime.writeNbt("setTime", nbt);
        currentTime.writeNbt("currentTime", nbt);
        nbt.putShort("ToggleMode", (short)this.toggleMode);
        nbt.putBoolean("TimerStarted", this.timerStarted);
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ClockBlockScreenHandler(i, playerInventory, propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, ClockBlockEntity blockEntity) {
        boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos);

        if (blockEntity.toggleMode != 0 && isReceivingRedstonePower && !blockEntity.timerStarted) {
            blockEntity.timerStarted = true;
        }

        if(blockEntity.toggleMode == 0 && isReceivingRedstonePower){
            blockEntity.currentTime.tickDown();
            ClockBlockEntity.markDirty(world, pos, state);
        }
        else if (blockEntity.timerStarted) {
            blockEntity.currentTime.tickDown();
            ClockBlockEntity.markDirty(world, pos, state);
        }

        if(blockEntity.currentTime.isZero() && !blockEntity.setTime.isZero() && !isReceivingRedstonePower && blockEntity.timerDone) {
            blockEntity.setTimeUpdated = true;
        }

        if(blockEntity.toggleMode == 0 && !isReceivingRedstonePower) {
            blockEntity.setTimeUpdated = true;
        }

        if(blockEntity.setTimeUpdated) {
            blockEntity.currentTime.setTo(blockEntity.setTime);
            blockEntity.setTimeUpdated = false;
            blockEntity.timerDone = false;
            blockEntity.timerStarted = false;
            ClockBlockEntity.markDirty(world, pos, state);
        }

        if(blockEntity.currentTime.isZero() && !blockEntity.setTime.isZero() && !blockEntity.timerDone) {
            state = (BlockState)state.with(ClockBlock.POWERED, true);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
            world.scheduleBlockTick(pos, state.getBlock(), 2);
            if(blockEntity.toggleMode == 0) {
                blockEntity.currentTime.setTo(blockEntity.setTime);
            }
            else {
                blockEntity.timerDone = true;
                blockEntity.timerStarted = false;
            }
        }
        return;
    }
}
