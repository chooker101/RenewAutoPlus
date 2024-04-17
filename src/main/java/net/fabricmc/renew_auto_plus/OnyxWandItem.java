package net.fabricmc.renew_auto_plus;

import java.util.Random;
import java.util.UUID;
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
import net.minecraft.world.World;

public class OnyxWandItem extends RangedWeaponItem implements AttackActionReplaced {
    public final Random random = new Random();
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private boolean firstAttack = true;

    public OnyxWandItem(Item.Settings settings) {
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
        boolean bl = user.getProjectileType(itemStack).getCount() >= 3;
        user.getItemCooldownManager().set(this, 600);
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            OnyxWandItem.shootSpecialAttack(world, user, hand, itemStack, OnyxWandItem.getSpeed(), OnyxWandItem.getDivergence());
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
        final float h = 0.003f;
        OnyxWandItem wandItem = (OnyxWandItem)wand.getItem();
        OnyxBasicProjectileEntity projectileEntity = null;
        float x = (float)wandItem.random.nextGaussian() * h;
        float y = (float)wandItem.random.nextGaussian() * h;
        projectileEntity = new OnyxBasicProjectileEntity(world, user, vec3d.getX() + x, vec3d.getY() + y, vec3d.getZ());
        projectileEntity.setPosition(user.getX() + vec3d.getX(), (user.getEyeY() - 0.15) + vec3d.getY(), user.getZ() + vec3d.getZ());
        projectileEntity.setVelocity(vec3d.getX() + x, vec3d.getY() + y, vec3d.getZ(), speed, divergence);
        projectileEntity.setWasSecond(!wandItem.firstAttack);
        world.spawnEntity(projectileEntity);
        projectileEntity = null;
        if(wandItem.firstAttack) {
            wandItem.firstAttack = false;
        } else {
            wandItem.firstAttack = true;
        }
        
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getProjectileType(wand).decrement(1);
        }
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    public static void shootSpecialAttack(World world, PlayerEntity user, Hand hand, ItemStack wand, float speed, float divergence) {
        if (world.isClient) {
            return;
        }
        GhostBanishedEntity ghostEntity = RenewAutoPlusInitialize.GHOST_BANISHED_ENTITY.create(world);
        UUID uUID = user.getUuid();
        if (uUID != null) {
            ghostEntity.setOwnerUuid(uUID);
            ghostEntity.setTamed(true);
            ghostEntity.initEquipment(world.getLocalDifficulty(user.getBlockPos()));
            world.spawnEntity(ghostEntity);
        }
        if (!user.getAbilities().creativeMode) {
            user.getProjectileType(wand).decrement(3);
        }
        
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    @Override
    public void onAttackServer(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            OnyxWandItem.shootBasicAttack(world, user, hand, itemStack, AquamarineWandItem.getSpeed(), AquamarineWandItem.getDivergence());
        }
    }

    @Override
    public void onAttackClient(World world, PlayerEntity user, Hand hand) {
        user.swingHand(Hand.MAIN_HAND);
        user.resetLastAttackedTicks();
    }

    @Override
    public boolean canAttack(PlayerEntity user) {
        if(firstAttack) {
            return user.getAttackCooldownProgress(0.0f) > 0.99f;
        }
        else {
            return true;
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
