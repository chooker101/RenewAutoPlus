package net.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.renew_auto_plus.RenewAutoPlusInitialize;
import net.renew_auto_plus.RubyWandItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

@Pseudo
@Mixin(TridentRiptideFeatureRenderer.class)
public abstract class TridentRiptideFeatureRenderExtension {
    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCull*(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    protected RenderLayer getEntityCutoutNoCullReplacement(Identifier texture, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l) {
        if(livingEntity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RubyWandItem){
            return RenderLayer.getEntityCutoutNoCull(RenewAutoPlusInitialize.RUBY_WAND_SPECIAL_EFFECT);
        }
        else {
            return RenderLayer.getEntityCutoutNoCull(texture);
        }
    }
}
