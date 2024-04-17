package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class AquamarineSpecialProjectileEntityRender extends EntityRenderer<AquamarineSpecialProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/entity/projectiles/aquamarine_special_projectile.png");
    private static final RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);

    public AquamarineSpecialProjectileEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(AquamarineSpecialProjectileEntity magicEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(AquamarineSpecialProjectileEntity magicEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(1.0f, 1.0f, 1.0f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        float animFrameFlatV = (float)Math.floor((double)(magicEntity.currentAnimFrame / 0.125f)) * 0.125f;
        AquamarineSpecialProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0.0f, animFrameFlatV + 0.125f);
        AquamarineSpecialProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1.0f, animFrameFlatV + 0.125f);
        AquamarineSpecialProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1.0f, animFrameFlatV);
        AquamarineSpecialProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0.0f, animFrameFlatV);
        matrixStack.pop();
        super.render(magicEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, float textureU, float textureV) {
        vertexConsumer.vertex(positionMatrix, x - 0.5f, (float)y - 0.25f, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(AquamarineSpecialProjectileEntity magicEntity) {
        return TEXTURE;
    }
}