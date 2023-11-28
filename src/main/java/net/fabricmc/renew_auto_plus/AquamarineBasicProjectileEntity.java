package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class AquamarineBasicProjectileEntity extends AbstractMagicProjectileEntity {
    public AquamarineBasicProjectileEntity(EntityType<? extends AbstractMagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AquamarineBasicProjectileEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(RenewAutoPlusInitialize.AQUAMARINE_BASIC_PROJECTILE_ENTITY, owner, velocityX, velocityY, velocityZ, world);
        this.life = this.random.nextInt(0, 50);
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
        entity.damage(new ProjectileDamageSource("directMagic", this, owner).setProjectile(), 1.4f);
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
    protected void age() {
        ++this.life;
        if (this.life >= 300) {
            this.discard();
            this.setOwner(null);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean earlyOutOnBlockHit() {
        return true;
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    public void spawnTrailParticles(double d, double e, double f) {
        if(this.random.nextFloat() < 0.02f) {
            if(hasHitBlock) {
                this.world.addParticle(this.getParticleType(), d - powerX * 7.0, e - powerY * 7.0, f - powerZ * 7.0, 0.0, 0.0, 0.0);
            }
            else {
                this.world.addParticle(this.getParticleType(), d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }
}
