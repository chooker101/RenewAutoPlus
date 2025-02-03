package net.renew_auto_plus;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TopazWandItem extends RangedWeaponItem implements AttackActionReplacedWithCharge {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final int maxAttackTicks = 144;
    private int currentAttackTicks = maxAttackTicks;
    private final int maxProcTicks = 20;
    private int currentProcTicks = 0;
    private final int maxSegments = 20;
    private int currentSegments = maxSegments;
    private boolean onCooldown = false;
    private boolean isAttacking = false;
    private boolean hasHit = false;
    private Entity mUser = null;

    public TopazWandItem(Item.Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 5.5f, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> ItemStack.areItemsEqual(stack, RenewAutoPlusInitialize.SPICE.getDefaultStack());
    }

    @Override
    public int getRange() {
        return 15;
    }

    public int getCurrentSegments() {
        return currentSegments;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ItemStack.areItemsEqual(ingredient, RenewAutoPlusInitialize.SPICE.getDefaultStack());
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = user.getProjectileType(itemStack).getCount() >= 2;
        user.getItemCooldownManager().set(this, 300);
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            TopazWandItem.shootSpecialAttack(world, user, hand, itemStack, TopazWandItem.getSpeed(), TopazWandItem.getDivergence());
            return TypedActionResult.success(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    public static float getSpeed() {
        return 1.5f;
    }

    public static float getDivergence() {
        return 1.0f;
    }

    public Entity getUser() {
        return mUser;
    }

    public void setUser(Entity user) {
        mUser = user;
    }

    private boolean canHit(Entity entity) {
        if(mUser.equals(entity)) {
            return false;
        }
        if (!entity.canBeHitByProjectile()) {
            return false;
        }
        return !mUser.isConnectedThroughVehicle(entity) && !entity.noClip;
    }

    public static void shootBasicAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        if (world.isClient) {
            return;
        }
        TopazWandItem topazWand = null;
        if(user.getStackInHand(hand).getItem() instanceof TopazWandItem) {
            topazWand = (TopazWandItem)user.getStackInHand(hand).getItem();
        }
        if (!topazWand.isAttacking) {
            topazWand.isAttacking = true;
        }
        if (!user.getAbilities().creativeMode) {
            if(topazWand.currentProcTicks == 0) {
                user.getProjectileType(wand).decrement(1);
            }
        }
        Vec3d endPoint = user.getRotationVector().normalize().multiply((float)topazWand.maxSegments).add(user.getEyePos());
        HitResult hitResult = world.raycast(new RaycastContext(user.getEyePos(), endPoint, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
        topazWand.setUser(user);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(world, user, user.getEyePos(), endPoint, Box.of(user.getEyePos(), topazWand.maxSegments * 2, topazWand.maxSegments * 2, topazWand.maxSegments * 2), topazWand::canHit, 0.3f);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }
        HitResult.Type hitType = ((HitResult)hitResult).getType();
        if (hitType != HitResult.Type.MISS) {
            Vec3d hitPos = hitResult.getPos();
            float distance = (float)hitPos.distanceTo(user.getEyePos());
            topazWand.currentSegments = (int)distance < topazWand.maxSegments ? (int)distance : topazWand.maxSegments;
            topazWand.hasHit = true;
            if(hitType == HitResult.Type.ENTITY) {
                // Hack to make it fire twice as fast as it consumes spice
                if(topazWand.currentProcTicks == 0 || topazWand.currentProcTicks == 10) {
                    Entity entity = entityHitResult.getEntity();
                    if(entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        if(livingEntity.isTeammate(user)) {
                            livingEntity.heal(6.0f);
                            user.addStatusEffect(new StatusEffectInstance(RenewAutoPlusInitialize.CHARGED, 160, 1), user);
                        }
                        else {
                            if(user.hasStatusEffect(RenewAutoPlusInitialize.CHARGED)) {
                                livingEntity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), user, user), 5.0f);
                            }
                            else {
                                livingEntity.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(RenewAutoPlusInitialize.DIRECT_MAGIC), user, user), 4.0f);
                            }
                            user.onAttacking(livingEntity);
                        }
                    }
                }
            }
        }
        else {
            topazWand.currentSegments = topazWand.maxSegments;
            topazWand.hasHit = false;
        }
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    public static void shootSpecialAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        if (world.isClient) {
            if(wand.getItem() instanceof TopazWandItem) {
                ((TopazWandItem)wand.getItem()).currentAttackTicks += 36;
            }
            return;
        }
        user.heal(10.0f);
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getProjectileType(wand).decrement(2);
        }
        //world.playSoundFromEntity(null, user, SoundEvents.ITEM_TRIDENT_RIPTIDE_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient) {
            return;
        }
        if(currentAttackTicks >= maxAttackTicks) {
            onCooldown = false;
        }
        if(currentAttackTicks < maxAttackTicks) {
            currentAttackTicks++;
        }
        if(isAttacking) {
            currentProcTicks = ++currentProcTicks % maxProcTicks;
        }
    }

    @Override
    public void onAttackServer(World world, PlayerEntity user, Hand hand) {
        if(currentAttackTicks > 2 && !onCooldown) {
            ItemStack itemStack = user.getStackInHand(hand);
            boolean bl = !user.getProjectileType(itemStack).isEmpty();
            if (user.getAbilities().creativeMode || bl) {
                user.setCurrentHand(hand);
                TopazWandItem.shootBasicAttack(world, user, hand, itemStack, TopazWandItem.getSpeed(), TopazWandItem.getDivergence());
            }
        }
        else {
            isAttacking = false;
            onCooldown = true;
        }
    }

    @Override
    public void onAttackClient(World world, PlayerEntity user, Hand hand) {
        if(currentAttackTicks > 0 && !onCooldown) {
            if(currentAttackTicks > 2){
                currentAttackTicks -= 3;
            } else {
                currentAttackTicks = 0;
            }
        }
        else {
            onCooldown = true;
        }
    }

    @Override
    public boolean canAttack(PlayerEntity user) {
        return true;
    }

    public boolean canAttack() {
        return true;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public boolean isHitting() {
        return hasHit;
    }

    @Override
    public float getAttackCooldownPercentage() {
        return (float)currentAttackTicks / (float)maxAttackTicks;
    }
    
    @Override
    public void stopAttackServer(World world, PlayerEntity user, Hand hand) {
        isAttacking = false;
        onCooldown = false;
        currentSegments = maxSegments;
        hasHit = false;
    }

    @Override
    public void stopAttackClient(World world, PlayerEntity user, Hand hand) {
        onCooldown = false;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
