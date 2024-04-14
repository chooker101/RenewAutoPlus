package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LinenArmorMaterial implements ArmorMaterial{
    public static final LinenArmorMaterial INSTANCE = new LinenArmorMaterial();
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
	private static final int[] PROTECTION_VALUES = new int[] {1, 2, 3, 1};

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 5;
    }

    @Override
	public int getProtectionAmount(EquipmentSlot slot) {
		return PROTECTION_VALUES[slot.getEntitySlotId()];
	}
 
	@Override
	public int getEnchantability() {
		return 10;
	}
 
	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
	}
 
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(RenewAutoPlusInitialize.LINEN);
	}
 
	@Override
	public String getName() {
		return "linen";
	}
 
	@Override
	public float getToughness() {
		return 0.0f;
	}
 
	@Override
	public float getKnockbackResistance() {
		return 0.0F;
	}
}
