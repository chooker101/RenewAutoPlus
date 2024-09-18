package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.renew_auto_plus.RenewAutoPlusInitialize;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
//import net.minecraft.world.gen.chunk.ChunkGenerator;

@Pseudo
@Mixin(Entity.class)
public abstract class EntityExtension {
    @Shadow
    public World world;

    @Shadow
    public Vec3d getVelocity() { return null; }
    @Shadow
    public float getYaw() { return 0.0f; }
    @Shadow
    public float getPitch() { return 0.0f; }

    @Inject(method = "getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    protected void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> info) {
        if (destination.getRegistryKey() == RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY) {
            if(RenewAutoPlusInitialize.wastesSpawnPos == null) { 
                //ChunkGenerator chunkGenerator = destination.getChunkManager().getChunkGenerator();
                ChunkPos chunkPos = new ChunkPos(0,0);
                int i = destination.getLogicalHeight();
                if (i < destination.getBottomY()) {
                    BlockPos blockPos = chunkPos.getStartPos();
                    i = destination.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ());
                }
                BlockPos possibleSpawnPos = SpawnLocating.findServerSpawnPoint(destination, chunkPos);
                if(possibleSpawnPos != null) {
                    RenewAutoPlusInitialize.wastesSpawnPos = SpawnLocating.findServerSpawnPoint(destination, chunkPos); //Might be fucked, but working for now
                }
                else {
                    RenewAutoPlusInitialize.wastesSpawnPos = chunkPos.getStartPos().add(0, i, 0);
                }
                destination.setSpawnPos(RenewAutoPlusInitialize.wastesSpawnPos, 0.0f); //Don't know why this doesn't work
            }
            info.setReturnValue(new TeleportTarget(new Vec3d((double)RenewAutoPlusInitialize.wastesSpawnPos.getX() + 0.5, RenewAutoPlusInitialize.wastesSpawnPos.getY(), (double)RenewAutoPlusInitialize.wastesSpawnPos.getZ() + 0.5), this.getVelocity(), this.getYaw(), this.getPitch()));
        } else if (destination.getRegistryKey() == World.OVERWORLD && world.getRegistryKey() == RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY) {
            BlockPos blockPos2 = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
            info.setReturnValue(new TeleportTarget(new Vec3d((double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5), this.getVelocity(), this.getYaw(), this.getPitch()));
        }
        return;
    }
}
