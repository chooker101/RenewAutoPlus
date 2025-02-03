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
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class RubyBasicProjectileEntityRender extends EntityRenderer<RubyBasicProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/entity/projectiles/ruby_basic_projectile.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityAlpha(TEXTURE);

    public RubyBasicProjectileEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(RubyBasicProjectileEntity magicEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(RubyBasicProjectileEntity magicEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.15, 0.0);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, magicEntity.prevYaw, magicEntity.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, magicEntity.prevPitch, magicEntity.getPitch())));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f));
        matrixStack.scale(0.05625f, 0.05625f, 0.05625f);
        VertexConsumer t = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        RubyBasicProjectileEntity.Size size = magicEntity.getCurrentSize();
        if(size == RubyBasicProjectileEntity.Size.LARGE) {
            matrixStack.translate(-4.0, 0.0, 0.0);
            this.vertex(matrix4f, matrix3f, t, 8, -7, -7, 0.0f, 0.78125f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, -7, 7, 0.34375f, 0.78125f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, 7, 7, 0.34375f, 0.8958f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, 7, -7, 0.0f, 0.8958f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, 7, -7, 0.0f, 0.78125f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, 7, 7, 0.34375f, 0.78125f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, -7, 7, 0.34375f, 0.8958f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, -7, -7, 0.0f, 0.8958f, 1, 0, 0, i);
            for (int u = 0; u < 2; ++u) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                this.vertex(matrix4f, matrix3f, t, -15, -7, 0, 0.0f, 0.6666f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 15, -7, 0, 0.6875f, 0.6666f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 15, 7, 0, 0.6875f, 0.78125f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, -15, 7, 0, 0.0f, 0.78125f, 0, 1, 0, i);
            }
        }
        else if(size == RubyBasicProjectileEntity.Size.MEDIUM) {
            matrixStack.translate(-4.0, 0.0, 0.0);
            this.vertex(matrix4f, matrix3f, t, 5, -5, -5, 0.0f, 0.40625f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, -5, 5, 0.21875f, 0.40625f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, 5, 5, 0.21875f, 0.4792f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, 5, -5, 0.0f, 0.4792f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, 5, -5, 0.0f, 0.40625f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, 5, 5, 0.21875f, 0.40625f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, -5, 5, 0.21875f, 0.4792f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, -5, -5, 0.0f, 0.4792f, 1, 0, 0, i);
            for (int u = 0; u < 2; ++u) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                this.vertex(matrix4f, matrix3f, t, -10, -5, 0, 0.0f, 0.3333f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 10, -5, 0, 0.5f, 0.3333f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 10, 5, 0, 0.5f, 0.40625f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, -10, 5, 0, 0.0f, 0.40625f, 0, 1, 0, i);
            }
        }
        else {
            matrixStack.translate(-4.0, 0.0, 0.0);
            this.vertex(matrix4f, matrix3f, t, 3, -2, -2, 0.0f, 0.05208f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, -2, 2, 0.15625f, 0.05208f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, 2, 2, 0.15625f, 0.1042f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, 2, -2, 0.0f, 0.1042f, -1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, 2, -2, 0.0f, 0.05208f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, 2, 2, 0.15625f, 0.05208f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, -2, 2, 0.15625f, 0.1042f, 1, 0, 0, i);
            this.vertex(matrix4f, matrix3f, t, 3, -2, -2, 0.0f, 0.1042f, 1, 0, 0, i);
            for (int u = 0; u < 2; ++u) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                this.vertex(matrix4f, matrix3f, t, -6, -2, 0, 0.0f, 0.0f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 6, -2, 0, 0.5f, 0.0f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, 6, 2, 0, 0.5f, 0.05208f, 0, 1, 0, i);
                this.vertex(matrix4f, matrix3f, t, -6, 2, 0, 0.0f, 0.05208f, 0, 1, 0, i);
            }
        }
        matrixStack.pop();
        super.render(magicEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, normalX, normalY, normalZ).next();
    }

    @Override
    public Identifier getTexture(RubyBasicProjectileEntity magicEntity) {
        return TEXTURE;
    }
}