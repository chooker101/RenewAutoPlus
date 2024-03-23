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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class AquamarineBasicProjectileEntityRender extends EntityRenderer<AquamarineBasicProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/entity/projectiles/aquamarine_basic_projectile.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityAlpha(TEXTURE);

    public AquamarineBasicProjectileEntityRender(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(AquamarineBasicProjectileEntity magicEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(AquamarineBasicProjectileEntity magicEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(tickDelta, magicEntity.prevYaw, magicEntity.getYaw()) - 90.0f));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(tickDelta, magicEntity.prevPitch, magicEntity.getPitch())));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrixStack.scale(0.05625f, 0.05625f, 0.05625f);
        matrixStack.translate(-4.0, 0.0, 0.0);
        VertexConsumer t = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        this.vertex(matrix4f, matrix3f, t, -5, -2, -2, 0.0f, 0.15625f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, -2, 0.0f, 0.3125f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, -2, 0.0f, 0.15625f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, -2, 0.0f, 0.3125f, 1, 0, 0, i);
        for (int u = 0; u < 2; ++u) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            this.vertex(matrix4f, matrix3f, t, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, i);
        }
        matrixStack.pop();
        super.render(magicEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, normalX, normalY, normalZ).next();
    }

    @Override
    public Identifier getTexture(AquamarineBasicProjectileEntity magicEntity) {
        return TEXTURE;
    }
}