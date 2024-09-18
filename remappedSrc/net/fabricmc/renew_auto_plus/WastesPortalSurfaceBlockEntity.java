package net.fabricmc.renew_auto_plus;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WastesPortalSurfaceBlockEntity extends BlockEntity {
    protected boolean hasImage = false;
    protected UUID imageUUID;

    protected WastesPortalSurfaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        imageUUID = UUID.randomUUID();
    }

    public WastesPortalSurfaceBlockEntity(BlockPos pos, BlockState state) {
        this(RenewAutoPlusInitialize.WASTES_PORTAL_SURFACE_ENTITY, pos, state);
    }

    public int getXBlockPos() {
        return this.getCachedState().get(WastesPortalSurfaceBlock.X_BLOCK_POS);
    }

    public int getYBlockPos() {
        return this.getCachedState().get(WastesPortalSurfaceBlock.Y_BLOCK_POS);
    }

    public Direction.Axis getAxis() {
        return this.getCachedState().get(WastesPortalSurfaceBlock.AXIS);
    }

    public UUID getImageUUID() {
        return imageUUID;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean hasImage() {
        return hasImage;
    }
}
