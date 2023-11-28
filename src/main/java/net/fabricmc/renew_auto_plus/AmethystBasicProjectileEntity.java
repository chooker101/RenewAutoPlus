package net.fabricmc.renew_auto_plus;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AmethystBasicProjectileEntity extends AbstractMagicProjectileEntity {
    public float currentAnimFrame = new Random().nextFloat();

    public AmethystBasicProjectileEntity(EntityType<? extends AbstractMagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AmethystBasicProjectileEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(RenewAutoPlusInitialize.AMETHYST_BASIC_PROJECTILE_ENTITY, owner, velocityX, velocityY, velocityZ, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        entity.damage(new ProjectileDamageSource("directMagic", this, owner).setProjectile(), 5.0f);
        this.discard();
        this.setOwner(null);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY && this.isOwner(((EntityHitResult)hitResult).getEntity())) {
                return;
            }
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                return;
            }
            this.discard();
            this.setOwner(null);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!this.world.isClient) {
            if(hasHitBlock) { //Some jank to make sure client hits atleast once
                this.discard();
                this.setOwner(null);
            }
        }
        super.onBlockHit(blockHitResult);
        if(this.world.isClient){
            world.addBlockBreakParticles(blockHitResult.getBlockPos(), world.getBlockState(blockHitResult.getBlockPos()));
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        currentAnimFrame = (currentAnimFrame + 0.1f) % 1.0f;
        super.tick();
    }

    @Override
    protected void setVelocityIfMoving(Vec3d velocity) {
        this.setVelocity(velocity);
    }
}
