package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OnyxBasicProjectileEntity extends AbstractMagicProjectileEntity {
    protected static final TrackedData<Boolean> TRACKED_WAS_SECOND = DataTracker.registerData(OnyxBasicProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public OnyxBasicProjectileEntity(EntityType<? extends AbstractMagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public OnyxBasicProjectileEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(RenewAutoPlusInitialize.ONYX_BASIC_PROJECTILE_ENTITY, owner, velocityX, velocityY, velocityZ, world);
        this.life = this.random.nextInt(0, 50);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRACKED_WAS_SECOND, false);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        entity.timeUntilRegen = 0;
        entity.damage(new ProjectileDamageSource("directMagic", this, owner).setProjectile(), 4.0f);
        if(owner instanceof LivingEntity) {
            ((LivingEntity)owner).onAttacking(entity);
        }
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
            if(hasHitBlock) {
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
    protected boolean canHit(Entity entity) {
        if (entity.isTeammate(this.getOwner())) {
            return false;
        }
        return super.canHit(entity);
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }


    @Override
    protected void setVelocityIfMoving(Vec3d velocity) {
        this.setVelocity(velocity);
    }

    @Override
    protected Box calculateBoundingBox() {
        Box boundingBox = this.getDimensions(getPose()).getBoxAt(this.getPos());
        boundingBox = boundingBox.expand(0.7, 0.1, 0.7);
        this.setBoundingBox(boundingBox);
        return boundingBox;
    }

    @Override
    protected boolean useDoubleCollision() {
        return true;
    }

    public void setWasSecond(boolean second) {
        this.dataTracker.set(TRACKED_WAS_SECOND, second);
    }

    public boolean getWasSecond() {
        return this.dataTracker.get(TRACKED_WAS_SECOND);
    }
}
