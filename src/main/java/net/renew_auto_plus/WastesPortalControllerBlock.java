package net.renew_auto_plus;

import com.google.common.base.Predicates;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")

public class WastesPortalControllerBlock extends BlockWithEntity {
    public static final MapCodec<WastesPortalControllerBlock> CODEC = WastesPortalControllerBlock.createCodec(WastesPortalControllerBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static BlockPattern COMPLETED_FRAME;

    public WastesPortalControllerBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
         } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WastesPortalControllerBlockEntity) {
                player.openHandledScreen((WastesPortalControllerBlockEntity)blockEntity);
            }
            return ActionResult.CONSUME;
         }
    }


	@Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WastesPortalControllerBlockEntity(pos, state);
    }

    @Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_FRAME == null) {
            COMPLETED_FRAME = BlockPatternBuilder.start().aisle("?ppp?", "p???p", "p???p", "p???p", "?pcp?").where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY)).where('p', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(RenewAutoPlusInitialize.WASTES_PORTAL_FRAME).with(WastesPortalFrameBlock.POWERED, Predicates.equalTo(true)))).where('c', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(RenewAutoPlusInitialize.WASTES_PORTAL_CONTROLLER))).build();
        }
        return COMPLETED_FRAME;
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> returnAFuckingDumbAssTicker(BlockEntityTicker<? super E> ticker) {
        return (BlockEntityTicker<A>)ticker;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient()) {
            return null;
        }
        else {
            return returnAFuckingDumbAssTicker(WastesPortalControllerBlockEntity::tick);
        }
    }
}
