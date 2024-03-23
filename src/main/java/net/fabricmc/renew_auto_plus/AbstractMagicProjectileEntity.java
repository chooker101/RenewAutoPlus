package net.fabricmc.renew_auto_plus;

import net.fabricmc.renew_auto_plus.helper.PublicProjectileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class AbstractMagicProjectileEntity extends PublicProjectileEntity {
    public double powerX = 0.0;
    public double powerY = 0.0;
    public double powerZ = 0.0;
    protected int life = 0;
    protected boolean hasHitBlock = false;

    public AbstractMagicProjectileEntity(EntityType<? extends PublicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractMagicProjectileEntity(EntityType<? extends PublicProjectileEntity> type, double x, double y, double z, double directionX, double directionY, double directionZ, World world,float yaw, float pitch) {
        this(type, world);
        this.refreshPositionAndAngles(x, y, z, pitch, yaw);
        this.refreshPosition();
        double d = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        if (d != 0.0) {
            this.powerX = directionX / d * 0.1;
            this.powerY = directionY / d * 0.1;
            this.powerZ = directionZ / d * 0.1;
        }
    }

    public AbstractMagicProjectileEntity(EntityType<? extends PublicProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
        this(type, owner.getX(), owner.getY(), owner.getZ(), directionX, directionY, directionZ, world, owner.getYaw(), owner.getPitch());
        prevYaw = owner.getYaw();
        prevPitch = owner.getPitch();
        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    public boolean earlyOutOnBlockHit() {
        return false;
    }

    @Override
    public void tick() {
        HitResult hitResult;
        Entity entity = this.getOwner();
        if (!this.world.isClient && (entity != null && entity.isRemoved() || entity == null)) {
            this.discard();
            return;
        }
        super.tick();
        if (this.hasHitBlock && earlyOutOnBlockHit()) {
            if(!this.world.isClient){
                this.age();
            }
            this.spawnTrailParticles(this.getX(), this.getY(), this.getZ());
            return;
        }
        if ((hitResult = PublicProjectileEntity.getCollision(this, this::canHit)).getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
            this.velocityDirty = true;
        }
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        float g = this.getDrag();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
            g = 0.8f * g;
        }
        this.setVelocityIfMoving(vec3d.add(this.powerX, this.powerY, this.powerZ).multiply(g));
        this.setPosition(d, e, f);
        //Yucky, need some sort of lerp or something
        if(this.useDoubleCollision()) {
            if (hitResult.getType() == HitResult.Type.MISS){
                if ((hitResult = PublicProjectileEntity.getCollision(this, this::canHit)).getType() != HitResult.Type.MISS) {
                    this.onCollision(hitResult);
                    this.velocityDirty = true;
                }
            }
        }
        this.spawnTrailParticles(d, e, f);
        this.age();
    }

    protected void parentTick() {
        super.tick();
    }

    protected void setVelocityIfMoving(Vec3d velocity) {
        if(!hasHitBlock) {
            this.setVelocity(velocity);
        } 
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(vec3d);
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
        hasHitBlock = true;
    }

    protected void spawnTrailParticles(double d, double e, double f) {
        this.world.addParticle(this.getParticleType(), d, e + 0.25, f, 0.0, 0.0, 0.0);
    }

    protected void age() {
        ++this.life;
        if (this.life >= 1200) {
            this.discard();
            this.setOwner(null);
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (this.getOwner().equals(entity)) {
            return false;
        }
        return super.canHit(entity) && !entity.noClip;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.WITCH;
    }

    protected float getDrag() {
        return 1.0f;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("life", (short)this.life);
        nbt.put("power", this.toNbtList(this.powerX, this.powerY, this.powerZ));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        NbtList nbtList;
        super.readCustomDataFromNbt(nbt);
        this.life = nbt.getShort("life");
        if (nbt.contains("power", 9) && (nbtList = nbt.getList("power", 6)).size() == 3) {
            this.powerX = nbtList.getDouble(0);
            this.powerY = nbtList.getDouble(1);
            this.powerZ = nbtList.getDouble(2);
        }
    }

    @Override
    public float getTargetingMargin() {
        return 1.0f;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        Entity entity = source.getAttacker();
        if (entity != null) {
            Vec3d vec3d = entity.getRotationVector();
            this.setVelocity(vec3d);
            this.powerX = vec3d.x * 0.1;
            this.powerY = vec3d.y * 0.1;
            this.powerZ = vec3d.z * 0.1;
            this.setOwner(entity);
            return true;
        }
        return false;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    protected boolean useDoubleCollision() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(), this.getPitch(), this.getYaw(), this.getType(), i, new Vec3d(this.powerX, this.powerY, this.powerZ));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        double g = Math.sqrt(d * d + e * e + f * f);
        if (g != 0.0) {
            this.powerX = d / g * 0.1;
            this.powerY = e / g * 0.1;
            this.powerZ = f / g * 0.1;
        }
    }
}
