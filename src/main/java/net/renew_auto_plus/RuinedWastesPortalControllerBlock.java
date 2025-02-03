package net.renew_auto_plus;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RuinedWastesPortalControllerBlock extends WastesPortalControllerBlock {
    public RuinedWastesPortalControllerBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
         } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbacusBlockEntity) {
                player.openHandledScreen((AbacusBlockEntity)blockEntity);
            }
            return ActionResult.CONSUME;
         }
    }


	@Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AbacusBlockEntity(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient()) {
            return null;
        }
        else {
            return returnAFuckingDumbAssTicker(AbacusBlockEntity::tick);
        }
    }
}
