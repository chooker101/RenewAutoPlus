package net.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import net.renew_auto_plus.VoidVillagerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;


@Pseudo
@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemExtension extends Item {
    public WrittenBookItemExtension(Settings settings) {
        super(settings);
    }
    
    @SuppressWarnings("resource")
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(entity instanceof VoidVillagerEntity) {
            return ActionResult.success(user.method_48926().isClient);
        }
        return ActionResult.PASS;
    }
}
