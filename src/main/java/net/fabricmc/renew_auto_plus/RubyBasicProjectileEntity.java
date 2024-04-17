package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RubyBasicProjectileEntity extends AbstractMagicProjectileEntity {
    enum Size {
        SMALL,
        MEDIUM,
        LARGE
    };
    protected static final TrackedData<Byte> TRACKED_SIZE = DataTracker.registerData(RubyBasicProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);

    public RubyBasicProjectileEntity(EntityType<? extends AbstractMagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public RubyBasicProjectileEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(RenewAutoPlusInitialize.RUBY_BASIC_PROJECTILE_ENTITY, owner, velocityX, velocityY, velocityZ, world);
        this.life = this.random.nextBetween(0, 50);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRACKED_SIZE, (byte)0);
    }

    public RubyBasicProjectileEntity.Size getCurrentSize() {
        switch(this.dataTracker.get(TRACKED_SIZE)){
            case 0:
                return Size.SMALL;
            case 1:
                return Size.MEDIUM;
            case 2:
                return Size.LARGE;
        }
        return Size.SMALL;
    }

    public void setCurrentSize(RubyBasicProjectileEntity.Size currentSize) {
        switch (currentSize) {
            case SMALL:
                this.dataTracker.set(TRACKED_SIZE, (byte)0);
                break;
            case MEDIUM:
                this.dataTracker.set(TRACKED_SIZE, (byte)1);
                break;
            case LARGE:
                this.dataTracker.set(TRACKED_SIZE, (byte)2);
                break;
            default:
                this.dataTracker.set(TRACKED_SIZE, (byte)0);
                break;
        }
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
        Size currentSize = this.getCurrentSize();
        switch (currentSize) {
            case SMALL:
                entity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), this, owner), 2.0f);
                break;
            case MEDIUM:
                entity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), this, owner), 7.0f);
                break;
            case LARGE:
                entity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), this, owner), 9.0f);
                break;
            default:
                break;
        }
        if(entity instanceof LivingEntity) {
            if(!((LivingEntity)entity).isOnFire()){
                ((LivingEntity)entity).setOnFireFor(3);
            }
        }
        if(owner instanceof LivingEntity) {
            ((LivingEntity)owner).onAttacking(entity);
        }
        this.discard();
        this.setOwner(null);
    }

    @SuppressWarnings("resource")
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
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
        Size currentSize = this.getCurrentSize();
        if(currentSize == null) {
            this.setBoundingBox(boundingBox);
            return boundingBox;
        }
        switch (currentSize) {
            case SMALL:
                break;
            case MEDIUM:
                boundingBox = boundingBox.expand(0.25, 0.25, 0.25);
                break;
            case LARGE:
                boundingBox = boundingBox.expand(0.5, 0.5, 0.5);
                break;
            default:
                break;
        }
        this.setBoundingBox(boundingBox);
        return boundingBox;
    }

    @Override
    protected boolean useDoubleCollision() {
        return true;
    }

}
