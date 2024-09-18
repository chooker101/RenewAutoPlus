package net.fabricmc.renew_auto_plus;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//Has wings for flap effects
//Vex movement is good
public class FallenEntity extends HostileEntity {
    private float eyeOffset = 0.5f;
    private int eyeOffsetCooldown;
    /**
     * The tracked flags of blazes. Only has the {@code 1} bit for {@linkplain
     * #isFireActive() fire activation}.
     */
    //private static final TrackedData<Byte> BLAZE_FLAGS = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

    public FallenEntity(EntityType<? extends FallenEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.experiencePoints = 10;
        this.moveControl = new FallenMoveControl(this);
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false)); //Replace with custom "charge"
        this.goalSelector.add(7, new LookAtTargetGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0).add(EntityAttributes.GENERIC_ATTACK_SPEED, 8.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        //this.dataTracker.startTracking(BLAZE_FLAGS, (byte)0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    //private boolean hasTargetCanSee() {
    //    LivingEntity livingEntity;
    //    return (livingEntity = this.getTarget()) != null && livingEntity.getEyeY() > this.getEyeY() + (double)this.eyeOffset && this.canTarget(livingEntity);
    //}

    @Override
    public void tickMovement() {
        //if (this.getVelocity().y < 0.0) {
        //    this.setVelocity(this.getVelocity().multiply(1.0, 0.1, 1.0));
        //}
        //if (this.world.isClient) {
        //    if (this.random.nextInt(24) == 0 && !this.isSilent()) {
        //        this.world.playSound(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
        //    }
        //    for (int i = 0; i < 2; ++i) {
        //        this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0);
        //    }
        //}
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        --this.eyeOffsetCooldown;
        if (this.eyeOffsetCooldown <= 0) {
            this.eyeOffsetCooldown = 100;
            this.eyeOffset = 0.5f + (float)this.random.nextGaussian() * 3.0f;
        }
        //if (hasTargetCanSee()) {
        //    Vec3d vec3d = this.getVelocity();
        //    this.setVelocity(this.getVelocity().add(0.0, ((double)0.3f - vec3d.y) * (double)0.3f, 0.0));
        //    this.velocityDirty = true;
        //}
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    static class FallenMoveControl extends MoveControl {
        private final FallenEntity fallen;
        private int collisionCheckCooldown = 0;
    
        public FallenMoveControl(FallenEntity fallen) {
            super(fallen);
            this.fallen = fallen;
        }
    
        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            if (this.collisionCheckCooldown-- <= 0) {
                this.collisionCheckCooldown += this.fallen.getRandom().nextInt(5) + 2;
                Vec3d vec3d = new Vec3d(this.targetX - this.fallen.getX(), this.targetY - this.fallen.getY(), this.targetZ - this.fallen.getZ());
                double d = vec3d.length();
                if (this.willCollide(vec3d.normalize(), MathHelper.ceil(d))) {
                    if (d < fallen.getBoundingBox().getAverageSideLength()) {
                        this.state = MoveControl.State.WAIT;
                        fallen.setVelocity(fallen.getVelocity().multiply(0.5));
                    } else {
                        fallen.setVelocity(fallen.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                        if (fallen.getTarget() == null) {
                            Vec3d vec3d2 = fallen.getVelocity();
                            fallen.setYaw(-((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                            fallen.bodyYaw = fallen.getYaw();
                        } else {
                            double vec3d2 = fallen.getTarget().getX() - fallen.getX();
                            double e = fallen.getTarget().getZ() - fallen.getZ();
                            fallen.setYaw(-((float)MathHelper.atan2(vec3d2, e)) * 57.295776f);
                            fallen.bodyYaw = fallen.getYaw();
                        }
                    }
                } else {
                    this.state = MoveControl.State.WAIT;
                }
            }
        }
    
        private boolean willCollide(Vec3d direction, int steps) {
            Box box = this.fallen.getBoundingBox();
            for (int i = 1; i < steps; ++i) {
                if (this.fallen.method_48926().isSpaceEmpty(this.fallen, box = box.offset(direction))) continue;
                return false;
            }
            return true;
        }
    }

    class LookAtTargetGoal extends Goal {
        private final FallenEntity fallen;

        public LookAtTargetGoal(FallenEntity fallen) {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
            this.fallen = fallen;
        }

        @Override
        public boolean canStart() {
            return !fallen.getMoveControl().isMoving() && fallen.random.nextInt(LookAtTargetGoal.toGoalTicks(7)) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockPos = fallen.getBlockPos();
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(fallen.random.nextInt(15) - 7, fallen.random.nextInt(11) - 5, fallen.random.nextInt(15) - 7);
                if (!fallen.method_48926().isAir(blockPos2)) continue;
                fallen.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
                if (fallen.getTarget() != null) break;
                fallen.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }
    
    //static class FlyRandomlyGoal extends Goal {
    //    private final FallenEntity fallen;
    //    private final int chance;
    //
    //    public FlyRandomlyGoal(FallenEntity fallen, int chance) {
    //        this.fallen = fallen;
    //        this.chance = chance;
    //        this.setControls(EnumSet.of(Goal.Control.MOVE));
    //    }
    //
    //    @Override
    //    public boolean canStart() {
    //        double f;
    //        double e;
    //        if (this.fallen.getDespawnCounter() >= 100) {
    //            return false;
    //        }
    //        if (this.fallen.getRandom().nextInt(Goal.toGoalTicks(this.chance)) != 0) {
    //            return false;
    //        }
    //        MoveControl moveControl = this.fallen.getMoveControl();
    //        if (!moveControl.isMoving()) {
    //            return true;
    //        }
    //        double d = moveControl.getTargetX() - this.fallen.getX();
    //        double g = d * d + (e = moveControl.getTargetY() - this.fallen.getY()) * e + (f = moveControl.getTargetZ() - this.fallen.getZ()) * f;
    //        return g < 1.0 || g > 3600.0;
    //    }
    //
    //    @Override
    //    public boolean shouldContinue() {
    //        return false;
    //    }
    //
    //    @Override
    //    public void start() {
    //        Random random = this.fallen.getRandom();
    //        double d = this.fallen.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
    //        double e = this.fallen.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
    //        double f = this.fallen.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
    //        this.fallen.getMoveControl().moveTo(d, e, f, 1.0);
    //    }
    //}
}
