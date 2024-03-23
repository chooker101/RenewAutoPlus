package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.renew_auto_plus.RubyWandItem;
import net.fabricmc.renew_auto_plus.TopazBeamFeatureRenderer;
import net.fabricmc.renew_auto_plus.TopazWandItem;
import net.fabricmc.renew_auto_plus.helper.RubyChargeRenderHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Pseudo
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererExtension {
    @Shadow
    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {}

    @Redirect(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingRiptide*()Z"))
    public boolean isUsingRiptideReplacement(AbstractClientPlayerEntity self, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) { // AbstractClientPlayerEntity self, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light
        if(item.getItem() instanceof RubyWandItem) {
            return false;
        }
        else {
            return player.isUsingRiptide();
        }
    }

    @Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    public void renderFirstPersonItemExtension(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if(item.getItem() instanceof RubyWandItem) { 
            RubyWandItem wand = (RubyWandItem)item.getItem();
            if(wand.getAttackCooldownPercentage() < 1.0f && wand.canAttack()) {
                Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
                boolean bl = arm == Arm.LEFT;
                matrices.push();
                matrices.translate((bl ? -1.0 : 1.0) / 2.0, -0.1, -0.6);
                applySwingOffset(matrices, arm, swingProgress);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
                matrices.translate(0.0, -0.9, 0.0);
                RubyChargeRenderHelper.renderChargeAnim((RubyWandItem)item.getItem(), matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
        if(item.getItem() instanceof TopazWandItem) {
            //Max at twenty segments to match rotation, perspective illusion
            TopazWandItem wand = (TopazWandItem)item.getItem();
            if(wand.isAttacking()) {
                int wandSegments = wand.getCurrentSegments() + 1;
                int segments = wandSegments > 20 ? 20 : wandSegments;
                matrices.push();
                matrices.translate(0.8, -0.4, -0.8);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp((float)segments / 20.0f, 4.0f, 2.0f)));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp((float)segments / 20.0f, 8.0f, 4.0f)));
                for(int heightOffset = 0; heightOffset < segments; heightOffset++) {
                    matrices.push();
                    matrices.scale(0.7f, 0.7f, 0.7f); //Scale here to not effect hit effect, maybe add another push around? More scared of stack limit/matrix copy
                    TopazBeamFeatureRenderer.renderBeam(matrices, vertexConsumers, light, player, tickDelta, heightOffset, 1);
                    matrices.pop();
                }
                if(wand.isHitting()) {
                    matrices.translate(0.6, -0.7, -(double)segments - 1.0);
                    TopazBeamFeatureRenderer.renderHitEffect(wand, matrices, vertexConsumers, light, tickDelta, segments);
                }
                matrices.pop();
            }
        }
    }
}
