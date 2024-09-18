package net.fabricmc.renew_auto_plus.extension;

import net.minecraft.block.FurnaceBlock;
import net.fabricmc.renew_auto_plus.FurnaceBlockEntityReplacement;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@SuppressWarnings("unchecked")

@Pseudo
@Mixin(FurnaceBlock.class)
public abstract class FurnaceBlockExtension extends AbstractFurnaceBlock {
    public boolean once = true;

    protected FurnaceBlockExtension(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FurnaceBlockEntityReplacement(pos, state, 4799, new TranslatableTextContent("container.furnace", "Furnace", null));
    }

    @Override
    public void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FurnaceBlockEntityReplacement) {
            player.openHandledScreen((NamedScreenHandlerFactory)((Object)blockEntity));
            player.incrementStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> returnAFuckingDumbAssTicker(BlockEntityTicker<? super E> ticker) {
        return (BlockEntityTicker<A>)ticker;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient()) {
            return null;
        }
        else {
            return returnAFuckingDumbAssTicker(FurnaceBlockEntityReplacement::tick);
        }
    }
}
