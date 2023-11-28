package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.renew_auto_plus.BanishedParryingSwordItem;
import net.fabricmc.renew_auto_plus.RenewAutoPlusInitialize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Pseudo
@Mixin(LivingEntity.class)
public abstract class LivingEntityExtension extends Entity {
    public boolean alreadyTracking = false;

    @Shadow
    ItemStack activeItemStack = ItemStack.EMPTY;

    @Shadow
    protected int lastAttackedTicks;

    @Shadow
    private void tickActiveItemStack() {}

    @Shadow
    public abstract int getItemUseTimeLeft();

    @Shadow
    public abstract void clearActiveItem();

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    public LivingEntityExtension(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "stopUsingItem()V", at = @At("HEAD"), cancellable = true)
    private void stopUsingEarlyReturn(CallbackInfo info) {
        if(this.activeItemStack.getItem() instanceof BanishedParryingSwordItem) { //Change to interface
            if(this.getItemUseTimeLeft() > 0.0f) {
                info.cancel();
                return;
            }
        }
    }

    @Inject(method = "updatePotionVisibility()V", at = @At("RETURN"))
    protected void updatePotionVisibilityExtension(CallbackInfo info) {
        this.setIcebound(this.hasStatusEffect(RenewAutoPlusInitialize.ICEBOUND));
    }

    @Inject(method = "initDataTracker()V", at = @At("RETURN"))
    protected void initDataTrackerExtension(CallbackInfo info) {
        this.dataTracker.startTracking(RenewAutoPlusInitialize.IS_ICEBOUND, false);
    }

    public void setIcebound(boolean icebound) {
        this.dataTracker.set(RenewAutoPlusInitialize.IS_ICEBOUND, icebound);
    }
}
