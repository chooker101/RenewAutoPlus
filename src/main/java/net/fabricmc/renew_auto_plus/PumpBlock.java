package net.fabricmc.renew_auto_plus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")

public class PumpBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final BooleanProperty LIQUID_TYPE = Properties.CONDITIONAL;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public PumpBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(Properties.LEVEL_8, 0).with(Properties.CONDITIONAL, true).with(Properties.POWERED, false));
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
            if (blockEntity instanceof PumpBlockEntity) {
               player.openHandledScreen((PumpBlockEntity)blockEntity);
            }
            return ActionResult.CONSUME;
         }
    }


	@Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PumpBlockEntity(pos, state);
    }

    @Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING, Properties.LEVEL_8, Properties.CONDITIONAL, Properties.POWERED);
	}

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
	}

	@Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		Direction facing = state.get(FACING);

        if(facing == Direction.NORTH) {
            //Bottom, N Wall + S Wall, E Wall + W Wall
            return VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(3.0D, 0.0D, 5.0D, 13.0D, 1.0D, 15.0D), Block.createCuboidShape(3.0D, 1.0D, 4.0D, 13.0D, 5.0D, 16.0D)), Block.createCuboidShape(2.0D, 1.0D, 5.0D, 14.0D, 5.0D, 15.0D));
        }
        else if(facing == Direction.EAST) {
            return VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(1.0D, 0.0D, 3.0D, 11.0D, 1.0D, 13.0D), Block.createCuboidShape(0.0D, 1.0D, 3.0D, 12.0D, 5.0D, 13.0D)), Block.createCuboidShape(1.0D, 1.0D, 2.0D, 11.0D, 5.0D, 14.0D));
        }
        else if(facing == Direction.SOUTH) {
            return VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(3.0D, 0.0D, 1.0D, 13.0D, 1.0D, 11.0D), Block.createCuboidShape(3.0D, 1.0D, 0.0D, 13.0D, 5.0D, 12.0D)), Block.createCuboidShape(2.0D, 1.0D, 1.0D, 14.0D, 5.0D, 11.0D));
        }
        else if(facing == Direction.WEST) {
            return VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(5.0D, 0.0D, 3.0D, 15.0D, 1.0D, 13.0D), Block.createCuboidShape(4.0D, 1.0D, 3.0D, 16.0D, 5.0D, 13.0D)), Block.createCuboidShape(5.0D, 1.0D, 2.0D, 15.0D, 5.0D, 14.0D));
        }

        return VoxelShapes.fullCube();
	}

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> returnAFuckingDumbAssTicker(BlockEntityTicker<? super E> ticker) {
        return (BlockEntityTicker<A>)ticker;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient()) {
            return null;
        }
        else {
            return returnAFuckingDumbAssTicker(PumpBlockEntity::tick);
        }
    }
}
