package net.fabricmc.renew_auto_plus;

import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class AmethystWandItem extends RangedWeaponItem {
    public AmethystWandItem(Item.Settings settings) {
        super(settings);
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
        boolean bl = !user.getArrowType(itemStack).isEmpty();
        user.getItemCooldownManager().set(this, 10);
        if (user.getAbilities().creativeMode || bl) {
            user.setCurrentHand(hand);
            AmethystWandItem.shootBasicAttack(world, user, hand, itemStack, AmethystWandItem.getSpeed(), AmethystWandItem.getDivergence());
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
        AmethystBasicProjectileEntity projectileEntity = new AmethystBasicProjectileEntity(world, user, vec3f.getX(), vec3f.getY(), vec3f.getZ());
        projectileEntity.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), speed, divergence);
        projectileEntity.setPosition(user.getX() + vec3f.getX(), (user.getEyeY() - 0.15) + vec3f.getY(), user.getZ() + vec3f.getZ());
        wand.damage(1, user, e -> e.sendToolBreakStatus(hand));
        if (!user.getAbilities().creativeMode) {
            user.getArrowType(wand).decrement(1);
        }
        world.spawnEntity(projectileEntity);
        //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
        projectileEntity = null;
    }
}
