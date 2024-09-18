package net.fabricmc.renew_auto_plus;

import java.util.EnumMap;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

public class BanishedArmorMaterial implements ArmorMaterial {
    public static final BanishedArmorMaterial INSTANCE = new BanishedArmorMaterial();
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static final EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY = Util.make(new EnumMap(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 13);
		map.put(ArmorItem.Type.LEGGINGS, 15);
		map.put(ArmorItem.Type.CHESTPLATE, 16);
		map.put(ArmorItem.Type.HELMET, 11);
	});
	//private static final int[] PROTECTION_VALUES = new int[] {3, 6, 8, 3};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final EnumMap<ArmorItem.Type, Integer> PROTECTION_VALUES = Util.make(new EnumMap(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    });

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY.get((Object)type) * 15;
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
		return Ingredient.ofItems(RenewAutoPlusInitialize.SPICE);
	}
 
	@Override
	public String getName() {
		return "banished";
	}
 
	@Override
	public float getToughness() {
		return 2.0F;
	}
 
	@Override
	public float getKnockbackResistance() {
		return 0.0F;
	}
}
