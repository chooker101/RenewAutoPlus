package net.renew_auto_plus;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FrilledBasiliskEntity extends AnimalEntity implements VariantHolder<net.renew_auto_plus.FrilledBasiliskEntity.BasiliskType> {
    public static final double NORMAL_SPEED = 0.6;
    public static final double SPRINTING_SPEED = 0.8;
    public static final double ESCAPE_DANGER_SPEED = 2.2;
    private static final TrackedData<Integer> BASILISK_TYPE = DataTracker.registerData(FrilledBasiliskEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_FLARING = DataTracker.registerData(FrilledBasiliskEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_SPRINTING = DataTracker.registerData(FrilledBasiliskEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int flaringTimer = 10;

    public FrilledBasiliskEntity(EntityType<? extends FrilledBasiliskEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new EscapeDangerGoal(this, SPRINTING_SPEED));
        this.goalSelector.add(3, new AnimalMateGoal(this, NORMAL_SPEED));
        this.goalSelector.add(4, new TemptGoal(this, 0.4, Ingredient.ofItems(RenewAutoPlusInitialize.SPICE), false));
        this.goalSelector.add(5, new FleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, NORMAL_SPEED, SPRINTING_SPEED));
        this.goalSelector.add(6, new FleeGoal<WolfEntity>(this, WolfEntity.class, 10.0f, NORMAL_SPEED, SPRINTING_SPEED));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, NORMAL_SPEED));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    public void setSpeed(double speed) {
        this.getNavigation().setSpeed(speed);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BASILISK_TYPE, BasiliskType.BLACK.id);
        this.dataTracker.startTracking(IS_FLARING, false);
        this.dataTracker.startTracking(IS_SPRINTING, false);
    }

    @Override
    public void mobTick() {
    }

    @Override
    public boolean shouldSpawnSprintingParticles() {
        return isSprinting();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    public static DefaultAttributeContainer.Builder createRabbitAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BasiliskType", this.getVariant().id);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(BasiliskType.byId(nbt.getInt("BasiliskType")));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean tryAttack(Entity target) {
        return target.damage(this.getDamageSources().mobAttack(this), 3.0f);
    }

    private static boolean isTempting(ItemStack stack) {
        return stack.isOf(RenewAutoPlusInitialize.SPICE);
    }

    @Override
    @Nullable
    public FrilledBasiliskEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        FrilledBasiliskEntity child = RenewAutoPlusInitialize.FRILLED_BASILISK_ENTITY.create(serverWorld);
        return child;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return FrilledBasiliskEntity.isTempting(stack);
    }

    @Override
    public BasiliskType getVariant() {
        return BasiliskType.byId(this.dataTracker.get(BASILISK_TYPE));
    }

    @Override
    public void setVariant(BasiliskType basiliskType) {
        this.dataTracker.set(BASILISK_TYPE, basiliskType.id);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        BasiliskType basiliskType = FrilledBasiliskEntity.getTypeFromPos(world, this.getBlockPos());
        if (entityData instanceof BasiliskData) {
            basiliskType = ((BasiliskData)entityData).type;
        } else {
            entityData = new BasiliskData(basiliskType);
        }
        this.setVariant(basiliskType);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    //Might eventually actually use biome pos
    private static BasiliskType getTypeFromPos(WorldAccess world, BlockPos pos) {
        int i = world.getRandom().nextInt(100);
        return i < 50 ? BasiliskType.BLACK : (i < 90 ? BasiliskType.GREEN : BasiliskType.RED);
    }

    public static boolean canSpawn(EntityType<FrilledBasiliskEntity> entity, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && FrilledBasiliskEntity.isLightLevelValidForNaturalSpawn(world, pos);
    }


    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_SPRINTING_PARTICLES_OR_RESET_SPAWNER_MINECART_SPAWN_DELAY) {
            this.spawnSprintingParticles();
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    public void setFlaringTimer(int flaringTimer) {
        this.flaringTimer = flaringTimer;
    }

    public int getFlaringTimer() {
        return this.flaringTimer;
    }

    public void setIsSprinting(boolean isSprinting) {
        this.dataTracker.set(IS_SPRINTING, isSprinting);
    }
    
    public void setIsFlaring(boolean isFlaring) {
        this.dataTracker.set(IS_FLARING, isFlaring);
    }

    public boolean isSprinting() {
        return this.dataTracker.get(IS_SPRINTING);
    }

    public boolean isFlaring() {
        return this.dataTracker.get(IS_FLARING);
    }

    static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
        private final FrilledBasiliskEntity entity;

        public EscapeDangerGoal(FrilledBasiliskEntity entity, double speed) {
            super(entity, speed);
            this.entity = entity;
        }

        @Override
        public void start() {
            super.start();
            entity.setIsFlaring(false);
            entity.setIsSprinting(true);
        }

        @Override
        public void stop() {
            super.stop();
            entity.setIsSprinting(false);
        }
    }

    static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final FrilledBasiliskEntity entity;

        public FleeGoal(FrilledBasiliskEntity entity, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(entity, fleeFromType, distance, slowSpeed, fastSpeed);
            this.entity = entity;
        }

        @Override
        public void tick() {
            if(entity.isFlaring() && entity.flaringTimer > 0) {
                // play flaring anim
                entity.flaringTimer--;
                entity.getLookControl().lookAt(targetEntity.getX(), targetEntity.getEyeY(), targetEntity.getZ());
                if (entity.flaringTimer <= 0) {
                    super.start();
                    entity.setIsSprinting(true);
                    entity.setIsFlaring(false);
                    entity.flaringTimer = 10;
                }
            }
            else {
                super.tick();
            }
        }

        @Override
        public boolean shouldContinue() {
            return entity.isFlaring() || !this.fleeingEntityNavigation.isIdle();
        }

        @Override
        public void start() {
            entity.setIsFlaring(true);
            entity.getLookControl().lookAt(targetEntity.getX(), targetEntity.getEyeY(), targetEntity.getZ());
            entity.getNavigation().startMovingTo(targetEntity, 0.1f);
        }

        @Override
        public void stop() {
            super.stop();
            entity.setIsSprinting(false);
        }
    }

    public static enum BasiliskType implements StringIdentifiable
    {
        BLACK(0, "black"),
        GREEN(1, "green"),
        RED(2, "red");

        private static final IntFunction<BasiliskType> BY_ID;
        public static final Codec<BasiliskType> CODEC;
        final int id;
        private final String name;

        private BasiliskType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static BasiliskType byId(int id) {
            return BY_ID.apply(id);
        }

        static {
            BY_ID = ValueLists.createIdToValueFunction(BasiliskType::getId, BasiliskType.values(), BLACK);
            CODEC = StringIdentifiable.createCodec(BasiliskType::values);
        }
    }

    public static class BasiliskData
    extends PassiveEntity.PassiveData {
        public final BasiliskType type;

        public BasiliskData(BasiliskType type) {
            super(1.0f);
            this.type = type;
        }
    }
}

