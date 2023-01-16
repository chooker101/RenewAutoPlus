package net.fabricmc.renew_auto_plus.extension;

import net.fabricmc.renew_auto_plus.BlockTagsExtension;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.BlockTags;

import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(PickaxeItem.class)
public abstract class PickaxeItemExtension extends MiningToolItem {

    public PickaxeItemExtension(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.PICKAXE_MINEABLE, settings);
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        if(state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            float i = this.getMaterial().getMiningSpeedMultiplier();
            if(i < 4.0f && state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
                return false;
            }
            if(i < 5.0f && state.isIn(BlockTagsExtension.NEEDS_COPPER_TOOL)) {
                return false;
            }
            if(i < 6.0f && state.isIn(BlockTagsExtension.NEEDS_GOLD_TOOL)) {
                return false;
            }
            if((i < 6.0f || i == 12.0f) && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
                return false;
            }
            if((i < 8.0f || i == 12.0f) && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
