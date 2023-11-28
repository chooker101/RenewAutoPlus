package net.fabricmc.renew_auto_plus;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BanishedParryingSwordItem extends ToolItem {
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    private int cooldownTime = 0; //didn't know ItemCooldownManger existed, need to change
    private int maxUseTime = 11;

    public BanishedParryingSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = (float)attackDamage + toolMaterial.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return maxUseTime;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(cooldownTime > 0) {
            return TypedActionResult.pass(itemStack);
        }
        else {
            if(!world.isClient()) {
                maxUseTime = 6;
                user.setCurrentHand(hand);
                maxUseTime = 11; // Jank to stop 5 frame startup of block
                user.world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, user.getSoundCategory(), 1.0f, 1.0f);
                user.spawnSweepAttackParticles();
                return TypedActionResult.pass(itemStack);
            }
            else {
                return TypedActionResult.consume(itemStack);
            }
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(cooldownTime > 0) {
            return ActionResult.PASS;
        }
        else {
            if(!user.world.isClient()) {
                maxUseTime = 6;
                user.setCurrentHand(hand);
                maxUseTime = 11;
                specialAttack(user, entity, hand);
                return ActionResult.PASS;
            }
            else {
                return ActionResult.SUCCESS;
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(cooldownTime > 0) {
            --cooldownTime;
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        cooldownTime = 14;
        if(user instanceof PlayerEntity) {
            ((PlayerEntity)user).getItemCooldownManager().set(this, 14);
        }
        user.clearActiveItem();
        return stack;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.OFFHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    public void specialAttack(PlayerEntity player, Entity target, Hand hand) {
        if (!target.isAttackable()) {
            return;
        }
        if (target.handleAttack(player)) {
            return;
        }
        float f = attackDamage + 1.0f; //It will get the mainhand damage, so just put in a one for player
        float g = target instanceof LivingEntity ? EnchantmentHelper.getAttackDamage(player.getStackInHand(hand), ((LivingEntity)target).getGroup()) : EnchantmentHelper.getAttackDamage(player.getStackInHand(hand), EntityGroup.DEFAULT);
        float h = 1.0f;
        g *= h;
        //player.resetLastAttackedTicks();
        if ((f *= 0.2f + h * h * 0.8f) > 0.0f || g > 0.0f) {
            //ItemStack itemStack;
            boolean bl = h > 0.9f;
            boolean bl2 = false;
            int i = 0;
            i += EnchantmentHelper.getKnockback(player);
            if (player.isSprinting() && bl) {
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0f, 1.0f);
                ++i;
                bl2 = true;
            }
            boolean bl3 = bl && player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle() && target instanceof LivingEntity;
            bl3 = bl3 && !player.isSprinting();
            if (bl3) {
                f *= 1.5f;
            }
            f += g;
            boolean bl42 = false;
            double d = player.horizontalSpeed - player.prevHorizontalSpeed;
            if (bl && !bl3 && !bl2 && player.isOnGround() && d < (double)player.getMovementSpeed() && player.getStackInHand(hand).getItem() instanceof BanishedParryingSwordItem) {
                bl42 = true;
            }
            float targetHealth = 0.0f;
            boolean bl5 = false;
            int j = EnchantmentHelper.getFireAspect(player);
            if (target instanceof LivingEntity) {
                targetHealth = ((LivingEntity)target).getHealth();
                if (j > 0 && !target.isOnFire()) {
                    bl5 = true;
                    target.setOnFireFor(1);
                }
            }
            Vec3d vec3d = target.getVelocity();
            target.timeUntilRegen = 0;
            boolean bl6 = target.damage(DamageSource.player(player), f);
            if (bl6) {
                List<LivingEntity> list;
                if (i > 0) {
                    if (target instanceof LivingEntity) {
                        ((LivingEntity)target).takeKnockback((float)i * 0.5f, MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)));
                    } else {
                        target.addVelocity(-MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f, 0.1, MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f);
                    }
                    player.setVelocity(player.getVelocity().multiply(0.6, 1.0, 0.6));
                    player.setSprinting(false);
                }
                if (bl42) {
                    float k = 1.0f + EnchantmentHelper.getSweepingMultiplier(player) * f;
                    list = player.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                    Iterator<LivingEntity> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LivingEntity livingEntity = iterator.next();
                        if (livingEntity == player || livingEntity == target || player.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(player.squaredDistanceTo(livingEntity) < 9.0)) continue;
                        livingEntity.takeKnockback(0.4f, MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)));
                        livingEntity.damage(DamageSource.player(player), k);
                    }
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
                    player.spawnSweepAttackParticles();
                }
                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                    ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                    target.setVelocity(vec3d);
                }
                if (bl3) {
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0f, 1.0f);
                    player.addCritParticles(target);
                }
                if (!bl3 && !bl42) {
                    if (bl) {
                        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0f, 1.0f);
                    } else {
                        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (g > 0.0f) {
                    player.addEnchantedHitParticles(target);
                }
                player.onAttacking(target);
                if (target instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)target, player);
                }
                EnchantmentHelper.onTargetDamaged(player, target);
                ItemStack k = player.getOffHandStack();
                Entity entity4 = target;
                if (target instanceof EnderDragonPart) {
                    entity4 = ((EnderDragonPart)target).owner;
                }
                if (!player.world.isClient && !k.isEmpty() && entity4 instanceof LivingEntity) {
                    k.postHit((LivingEntity)entity4, player);
                    if (k.isEmpty()) {
                        player.setStackInHand(hand, ItemStack.EMPTY);
                    }
                }
                if (target instanceof LivingEntity) {
                    float l = targetHealth - ((LivingEntity)target).getHealth();
                    player.increaseStat(Stats.DAMAGE_DEALT, Math.round(l * 10.0f));
                    if (j > 0) {
                        target.setOnFireFor(j * 4);
                    }
                    if (player.world instanceof ServerWorld && l > 2.0f) {
                        int livingEntity = (int)((double)l * 0.5);
                        ((ServerWorld)player.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), livingEntity, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                player.addExhaustion(0.1f);
            } else {
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0f, 1.0f);
                if (bl5) {
                    target.extinguish();
                }
            }
        }
    }
}

