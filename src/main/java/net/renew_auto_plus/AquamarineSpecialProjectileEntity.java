package net.renew_auto_plus;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AquamarineSpecialProjectileEntity extends AbstractMagicProjectileEntity {
    public float currentAnimFrame = new Random().nextFloat();

    public AquamarineSpecialProjectileEntity(EntityType<? extends AbstractMagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AquamarineSpecialProjectileEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(RenewAutoPlusInitialize.AQUAMARINE_SPECIAL_PROJECTILE_ENTITY, owner, velocityX, velocityY, velocityZ, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        World world = this.getWorld();
        if (world.isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        entity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), this, owner), 2.0f);
        if(entity instanceof LivingEntity) {
            if(!((LivingEntity)entity).hasStatusEffect(RenewAutoPlusInitialize.ICEBOUND_RESISTANCE)){
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(RenewAutoPlusInitialize.ICEBOUND, 100, 1), owner);
            }
        }
        if(owner instanceof LivingEntity) {
            ((LivingEntity)owner).onAttacking(entity);
        }
        this.discard();
        this.setOwner(null);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        World world = this.getWorld();
        if (!world.isClient) {
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
    protected float getDrag() {
        return 0.85f;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        World world = this.getWorld();
        if (!world.isClient) {
            if(hasHitBlock) {
                this.discard();
                this.setOwner(null);
            }
        }
        super.onBlockHit(blockHitResult);
        if(world.isClient){
            world.addBlockBreakParticles(blockHitResult.getBlockPos(), world.getBlockState(blockHitResult.getBlockPos()));
        }
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        currentAnimFrame = (currentAnimFrame + 0.05f) % 1.0f;
        super.tick();
    }

    @Override
    protected void setVelocityIfMoving(Vec3d velocity) {
        this.setVelocity(velocity);
    }
}
