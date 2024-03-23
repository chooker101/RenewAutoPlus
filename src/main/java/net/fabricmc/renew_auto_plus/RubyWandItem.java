package net.fabricmc.renew_auto_plus;

import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class RubyWandItem extends RangedWeaponItem implements AttackActionReplacedWithCharge {
    public final Random random = new Random();
    public int chargeAnimationFrame = 0;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final int maxAttackTicks = 16;
    private int currentAttackTicks = maxAttackTicks;
    private boolean onCooldown = false;
    

    public RubyWandItem(Item.Settings settings) {
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

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ItemStack.areItemsEqual(ingredient, RenewAutoPlusInitialize.SPICE.getDefaultStack());
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = user.getArrowType(itemStack).getCount() >= 3;
        user.getItemCooldownManager().set(this, 60);
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            RubyWandItem.shootSpecialAttack(world, user, hand, itemStack, RubyWandItem.getSpeed(), RubyWandItem.getDivergence());
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

    public static void shootBasicAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        if (world.isClient) {
            return;
        }
        Vec3d vec3d = user.getRotationVec(1.0f);
        Vec3f vec3f = new Vec3f(vec3d);
        RubyBasicProjectileEntity projectileEntity = new RubyBasicProjectileEntity(world, user, vec3f.getX(), vec3f.getY(), vec3f.getZ());
        projectileEntity.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), speed, divergence);
        projectileEntity.setPosition(user.getX() + vec3f.getX(), (user.getEyeY() - 0.15) + vec3f.getY(), user.getZ() + vec3f.getZ());
        float attackCooldownPercentage = ((RubyWandItem)wand.getItem()).getAttackCooldownPercentage();
        if(attackCooldownPercentage > 0.4f) {
            projectileEntity.setCurrentSize(RubyBasicProjectileEntity.Size.SMALL);
        }
        else if(attackCooldownPercentage > 0.125f) {
            projectileEntity.setCurrentSize(RubyBasicProjectileEntity.Size.MEDIUM);
        }
        else if(attackCooldownPercentage >= 0.0f) {
            projectileEntity.setCurrentSize(RubyBasicProjectileEntity.Size.LARGE);
        }
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getArrowType(wand).decrement(1);
        }
        world.spawnEntity(projectileEntity);
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    public static void shootSpecialAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        float tridentEntity = user.getYaw();
        float f = user.getPitch();
        float g = -MathHelper.sin(tridentEntity * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        float h = -MathHelper.sin(f * ((float)Math.PI / 180));
        float k = MathHelper.cos(tridentEntity * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        float l = MathHelper.sqrt(g * g + h * h + k * k);
        float m = 2.0f;
        user.addVelocity(g *= m / l, h *= m / l, k *= m / l);
        user.setRiptideTicks(18);
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 120, 1));
        if (user.isOnGround()) {
            user.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
        }
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getArrowType(wand).decrement(3);
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
        if(getAttackCooldownPercentage() < 1.0f && canAttack()) {
            if(chargeAnimationFrame < 20) {
                chargeAnimationFrame++;
            }
            else {
                chargeAnimationFrame = 9;
            }
        }
        else {
            chargeAnimationFrame = 0;
        }
    }

    @Override
    public void onAttackServer(World world, PlayerEntity user, Hand hand) {
    }

    @Override
    public void onAttackClient(World world, PlayerEntity user, Hand hand) {
        if(currentAttackTicks > 0 && !onCooldown) {
            if(currentAttackTicks > 1){
                currentAttackTicks -= 2;
            } else {
                currentAttackTicks = 0;
            }
        }
    }

    @Override
    public boolean canAttack(PlayerEntity user) {
        return !onCooldown;
    }

    public boolean canAttack() {
        return !onCooldown;
    }

    @Override
    public float getAttackCooldownPercentage() {
        return (float)currentAttackTicks / (float)maxAttackTicks;
    }
    
    @Override
    public void stopAttackServer(World world, PlayerEntity user, Hand hand) {
        if(currentAttackTicks < maxAttackTicks){
            ItemStack itemStack = user.getStackInHand(hand);
            boolean bl = !user.getArrowType(itemStack).isEmpty();
            if (user.getAbilities().creativeMode || bl) {
                user.setCurrentHand(hand);
                RubyWandItem.shootBasicAttack(world, user, hand, itemStack, RubyWandItem.getSpeed(), RubyWandItem.getDivergence());
            }
        }
    }

    @Override
    public void stopAttackClient(World world, PlayerEntity user, Hand hand) {
        user.swingHand(Hand.MAIN_HAND);
        onCooldown = true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
