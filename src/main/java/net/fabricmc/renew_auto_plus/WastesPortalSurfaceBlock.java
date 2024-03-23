package net.fabricmc.renew_auto_plus;

import net.fabricmc.renew_auto_plus.helper.OverworldInventoryStorage;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//NEED TO MAKE A RUINED VERSION, CAN'T REASONABLY DETECT
public class WastesPortalSurfaceBlock extends BlockWithEntity {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    public static final IntProperty X_BLOCK_POS = IntProperty.of("x_block_pos", 0, 2);
    public static final IntProperty Y_BLOCK_POS = IntProperty.of("y_block_pos", 0, 2);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    protected WastesPortalSurfaceBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_AXIS, Direction.Axis.X).with(X_BLOCK_POS, 0).with(Y_BLOCK_POS, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WastesPortalSurfaceBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.get(AXIS) == Direction.Axis.Z) {
            return Z_SHAPE;
        }
        return X_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world instanceof ServerWorld && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals() && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), state.getOutlineShape(world, pos), BooleanBiFunction.AND)) {
            RegistryKey<World> registryKey = world.getRegistryKey() == RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY ? World.OVERWORLD : RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY;
            ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
            if (serverWorld == null) {
                return;
            }
            if(entity instanceof PlayerEntity && world.getRegistryKey() == World.OVERWORLD) {
                ((OverworldInventoryStorage)((PlayerEntity)entity)).storeCurrentInventory();
            }
            entity.moveToWorld(serverWorld);
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (state.get(WastesPortalSurfaceBlock.AXIS)) {
                    case X: {
                        return (BlockState)state.with(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)state.with(AXIS, Direction.Axis.X);
                    }
                    case Y: {}
                }
                return state;
            }
            case CLOCKWISE_180: {}
            case NONE: {}
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS).add(X_BLOCK_POS).add(Y_BLOCK_POS);
    }
}
