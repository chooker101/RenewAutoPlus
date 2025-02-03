package net.renew_auto_plus;

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
public class AmethystBasicProjectileEntityRender extends EntityRenderer<AmethystBasicProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/entity/projectiles/amethyst_basic_projectile.png");
    private static final RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);

    public AmethystBasicProjectileEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(AmethystBasicProjectileEntity magicEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(AmethystBasicProjectileEntity magicEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(0.75f, 0.75f, 0.75f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        float animFrameFlatV = (float)Math.floor((double)(magicEntity.currentAnimFrame / 0.2f)) * 0.2f;
        AmethystBasicProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0.0f, animFrameFlatV + 0.2f);
        AmethystBasicProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1.0f, animFrameFlatV + 0.2f);
        AmethystBasicProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1.0f, animFrameFlatV);
        AmethystBasicProjectileEntityRender.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0.0f, animFrameFlatV);
        matrixStack.pop();
        super.render(magicEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, float textureU, float textureV) {
        vertexConsumer.vertex(positionMatrix, x - 0.5f, (float)y - 0.25f, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(AmethystBasicProjectileEntity magicEntity) {
        return TEXTURE;
    }
}