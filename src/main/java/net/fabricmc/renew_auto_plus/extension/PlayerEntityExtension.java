package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Pseudo
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityExtension extends LivingEntity {
    protected PlayerEntityExtension(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract float getAttackCooldownProgressPerTick();
    
    @Inject(method = "getAttackCooldownProgress(F)F", at = @At("HEAD"), cancellable = true)
    public void getAttackCooldownProgressEarlyReturn(float baseTime, CallbackInfoReturnable<Float> info) {
        if(this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof AttackActionReplacedWithCharge) {
            float percentage = MathHelper.clamp(((AttackActionReplacedWithCharge)this.getStackInHand(Hand.MAIN_HAND).getItem()).getAttackCooldownPercentage(), 0.0f, 1.0f);
            info.setReturnValue(percentage);
        }
    }
}
