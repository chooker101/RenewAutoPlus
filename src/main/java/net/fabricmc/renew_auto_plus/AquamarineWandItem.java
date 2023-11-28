package net.fabricmc.renew_auto_plus;

import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.renew_auto_plus.helper.AttackActionReplaced;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class AquamarineWandItem extends RangedWeaponItem implements AttackActionReplaced {
    public final Random random = new Random();
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public AquamarineWandItem(Item.Settings settings) {
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
        boolean bl = user.getArrowType(itemStack).getCount() >= 2;
        user.getItemCooldownManager().set(this, 40);
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            AquamarineWandItem.shootSpecialAttack(world, user, hand, itemStack, AquamarineWandItem.getSpeed(), AquamarineWandItem.getDivergence());
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
        final float h = 0.016f;
        AquamarineBasicProjectileEntity projectileEntity = null;
        for(int i = 0; i <= 4; ++i) {
            float x = (float)((AquamarineWandItem)wand.getItem()).random.nextGaussian() * h;
            float y = (float)((AquamarineWandItem)wand.getItem()).random.nextGaussian() * h;
            projectileEntity = new AquamarineBasicProjectileEntity(world, user, vec3f.getX() + x, vec3f.getY() + y, vec3f.getZ());
            projectileEntity.setPosition(user.getX() + vec3f.getX(), (user.getEyeY() - 0.15) + vec3f.getY(), user.getZ() + vec3f.getZ());
            projectileEntity.setVelocity(vec3f.getX() + x, vec3f.getY() + y, vec3f.getZ(), speed, divergence);
            world.spawnEntity(projectileEntity);
            projectileEntity = null;
        }
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getArrowType(wand).decrement(1);
        }
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    public static void shootSpecialAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        if (world.isClient) {
            return;
        }
        Vec3d vec3d = user.getRotationVec(1.0f);
        Vec3f vec3f = new Vec3f(vec3d);
        final float specialSpeed = 0.5f;
        AquamarineSpecialProjectileEntity projectileEntity = new AquamarineSpecialProjectileEntity(world, user, vec3f.getX(), vec3f.getY(), vec3f.getZ());
        projectileEntity.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), specialSpeed, divergence);
        projectileEntity.setPosition(user.getX() + vec3f.getX(), (user.getEyeY() - 0.15) + vec3f.getY(), user.getZ() + vec3f.getZ());
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getArrowType(wand).decrement(2);
        }
        world.spawnEntity(projectileEntity);
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    @Override
    public void onAttackServer(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getArrowType(itemStack).isEmpty();
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            AquamarineWandItem.shootBasicAttack(world, user, hand, itemStack, AquamarineWandItem.getSpeed(), AquamarineWandItem.getDivergence());
        }
    }

    @Override
    public void onAttackClient(World world, PlayerEntity user, Hand hand) {
        user.swingHand(Hand.MAIN_HAND);
        user.resetLastAttackedTicks();
    }

    @Override
    public boolean canAttack(PlayerEntity user) {
        return user.getAttackCooldownProgress(0.0f) > 0.99f;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
