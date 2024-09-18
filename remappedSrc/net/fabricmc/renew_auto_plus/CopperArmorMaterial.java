package net.fabricmc.renew_auto_plus;

import java.util.EnumMap;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

public class CopperArmorMaterial implements ArmorMaterial{
    public static final CopperArmorMaterial INSTANCE = new CopperArmorMaterial();
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static final EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY = Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 13);
		map.put(ArmorItem.Type.LEGGINGS, 15);
		map.put(ArmorItem.Type.CHESTPLATE, 16);
		map.put(ArmorItem.Type.HELMET, 11);
	});
	//private static final int[] PROTECTION_VALUES = new int[] {1, 4, 5, 2};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final EnumMap<ArmorItem.Type, Integer> PROTECTION_VALUES = Util.make(new EnumMap(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 4);
        map.put(ArmorItem.Type.CHESTPLATE, 5);
        map.put(ArmorItem.Type.HELMET, 2);
    });

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY.get((Object)type) * 10;
    }

    @Override
	public int getProtection(ArmorItem.Type type) {
		return PROTECTION_VALUES.get((Object)type);
	}
 
	@Override
	public int getEnchantability() {
		return 10;
	}
 
	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}
 
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(Items.COPPER_INGOT);
	}
 
	@Override
	public String getName() {
		return "copper";
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
