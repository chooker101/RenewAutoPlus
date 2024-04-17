package net.fabricmc.renew_auto_plus;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class BanishedToolMaterial implements ToolMaterial {
    public static final BanishedToolMaterial INSTANCE = new BanishedToolMaterial();

    @Override
    public int getDurability() {
        return 750;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 5.0F;
    }

    public float getAttackDamage() {
        return 1.0f;
    }

    public int getMiningLevel() {
        return 3;
    }

    public int getEnchantability() {
        return 14;
    }

    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.COPPER_INGOT);
    }
}
