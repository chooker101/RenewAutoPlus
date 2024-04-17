package net.fabricmc.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.HoldInHandsGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class BanishedEntity extends HostileEntity{
    private static final TrackedData<Boolean> IS_SHADOWED = DataTracker.registerData(BanishedEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_PARRYING_SWORD = DataTracker.registerData(BanishedEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ActiveTargetGoal<PlayerEntity> playerTargetGoal = null;

    public BanishedEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
        this.dataTracker.startTracking(IS_SHADOWED, false);
        this.dataTracker.startTracking(HAS_PARRYING_SWORD, false);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0).add(EntityAttributes.GENERIC_ATTACK_SPEED, 8.0).add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new HoldInHandsGoal<BanishedEntity>(this, PotionUtil.setPotion(new ItemStack(Items.POTION), RenewAutoPlusInitialize.SHADOW_POTION), null, entity -> entity.isAttacking() && !entity.isShadowed()));
        this.goalSelector.add(1, new HoldInHandsGoal<BanishedEntity>(this, new ItemStack(RenewAutoPlusInitialize.BANISHED_SWORD_CLEAR), null, entity -> entity.isShadowed() && !entity.getMainHandStack().isOf(RenewAutoPlusInitialize.BANISHED_SWORD_CLEAR)));
        this.goalSelector.add(0, new HoldInHandsGoal<BanishedEntity>(this, new ItemStack(RenewAutoPlusInitialize.BANISHED_SWORD), null, entity -> !entity.isShadowed() && (entity.getMainHandStack().isOf(RenewAutoPlusInitialize.BANISHED_SWORD_CLEAR) || entity.getMainHandStack().isEmpty())));
        this.goalSelector.add(1, new HoldInOffhandGoal<BanishedEntity>(this, new ItemStack(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD_CLEAR), null, entity -> entity.hasParryingSword() && (entity.isShadowed() && !entity.getOffHandStack().isOf(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD_CLEAR))));
        this.goalSelector.add(0, new HoldInOffhandGoal<BanishedEntity>(this, new ItemStack(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD), null, entity -> entity.hasParryingSword() && (!entity.isShadowed() && (entity.getOffHandStack().isOf(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD_CLEAR) || entity.getOffHandStack().isEmpty()))));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        playerTargetGoal = new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true);
        this.targetSelector.add(2, playerTargetGoal);
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(RenewAutoPlusInitialize.BANISHED_SWORD));
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(RenewAutoPlusInitialize.BANISHED_HELMET));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(RenewAutoPlusInitialize.BANISHED_CHESTPLATE));
        this.equipStack(EquipmentSlot.LEGS, new ItemStack(RenewAutoPlusInitialize.BANISHED_LEGGINGS));
        this.equipStack(EquipmentSlot.FEET, new ItemStack(RenewAutoPlusInitialize.BANISHED_BOOTS));
        if (this.random.nextFloat() < 0.15f) { 
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD));
            this.dataTracker.set(HAS_PARRYING_SWORD, true);
        }
    }
    
    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        boolean bl;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            float f = this.getDropChance(equipmentSlot);
            bl = f > 1.0f;
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack) || !allowDrops && !bl || !(Math.max(this.random.nextFloat() - (float)lootingMultiplier * 0.01f, 0.0f) < f)) continue;
            if (itemStack.isOf(RenewAutoPlusInitialize.BANISHED_SWORD_CLEAR)) {
                itemStack = new ItemStack(RenewAutoPlusInitialize.BANISHED_SWORD);
            }
            if (itemStack.isOf(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD_CLEAR)) {
                itemStack = new ItemStack(RenewAutoPlusInitialize.BANISHED_PARRYING_SWORD);
            }
            if (!bl && itemStack.isDamageable()) {
                itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
            }
            this.dropStack(itemStack);
            this.equipStack(equipmentSlot, ItemStack.EMPTY);
        }
    }
    
    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        this.initEquipment(difficulty);
        return entityData;
    }

    public float getArmorAlpha() {
        if(isShadowed()) {
            return 0.2f;
        }
        else {
            return 1.0f;
        }
    }

    @Override
    protected void updatePotionVisibility() {
        if (this.getActiveStatusEffects().isEmpty()) {
            this.setShadowed(false);
        } else {
            this.setShadowed(this.hasStatusEffect(RenewAutoPlusInitialize.SHADOWED));
        }
        super.updatePotionVisibility();
    }

    public void setShadowed(boolean shadowed) {
        this.dataTracker.set(IS_SHADOWED, shadowed);
    }

    public boolean isShadowed() {
        return this.dataTracker.get(IS_SHADOWED);
    }

    public boolean hasParryingSword() {
        return this.dataTracker.get(HAS_PARRYING_SWORD);
    }
}
