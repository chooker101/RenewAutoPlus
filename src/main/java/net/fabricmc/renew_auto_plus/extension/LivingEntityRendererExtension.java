package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.renew_auto_plus.RenewAutoPlusInitialize;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Pseudo
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererExtension {
    protected LivingEntityRendererExtension(Context ctx) {
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render*(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void render(EntityModel<LivingEntity> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float r, float g, float b, float alpha, LivingEntity livingEntity, float yaw, float tickDelta, MatrixStack matrixStack2, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.getDataTracker().get(RenewAutoPlusInitialize.IS_ICEBOUND)) {
            model.render(matrixStack, vertexConsumer, light, overlay, 0.55f, 0.75f, 1.0f, alpha);
        }
        else {
            model.render(matrixStack, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, alpha);
        }
    }
}
