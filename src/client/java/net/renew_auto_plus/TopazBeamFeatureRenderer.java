package net.renew_auto_plus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class TopazBeamFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier INNER_TEXTURE = new Identifier("minecraft", "textures/entity/beacon_beam.png");
    public static final Identifier OUTER_TEXTURE = new Identifier("renew_auto_plus", "textures/entity/topaz_beam_outer.png");
    public static final Identifier HIT_TEXTURE = new Identifier("renew_auto_plus", "textures/entity/projectiles/topaz_beam_hit.png");
    private static final RenderLayer INNER_LAYER = RenderLayer.getBeaconBeam(INNER_TEXTURE, true);
    private static final RenderLayer OUTER_LAYER = RenderLayer.getEntityAlpha(OUTER_TEXTURE);

    public TopazBeamFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!(livingEntity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof TopazWandItem)) {
            return;
        }
        
        TopazWandItem wand = (TopazWandItem)livingEntity.getStackInHand(Hand.MAIN_HAND).getItem();
        if (!(wand.isAttacking())) {
            return;
        }

        int segments = wand.getCurrentSegments();
        //Fuck me minecraft is ass
        //Vec3d eyepos = livingEntity.getEyePos();
        //double radianYaw = Math.toRadians(headYaw);
        //double radianPitch = Math.toRadians(headPitch);
        //Vec3d facing = new Vec3d(Math.cos(radianYaw) * Math.cos(radianPitch), Math.sin(radianYaw) * Math.cos(radianPitch), -Math.sin(radianPitch));
        //facing.normalize();
        //RenewAutoPlusInitialize.LOGGER.info(facing);
        
        //facing.multiply(segments > 1 ? segments : 1);
        //facing.add(eyepos.x, eyepos.y, eyepos.z);
        //Vec3d beamStart = new Vec3d(eyepos.x, eyepos.y, eyepos.z);
        //facing.subtract(beamStart);
        //facing.normalize();

        matrixStack.push();
        matrixStack.translate(-0.35, 0.4, -1.0);
        //RenewAutoPlusInitialize.LOGGER.info(radianPitch);
        //RenewAutoPlusInitialize.LOGGER.info(Math.asin(-z));
        //RenewAutoPlusInitialize.LOGGER.info(radianYaw);
        //RenewAutoPlusInitialize.LOGGER.info(Math.atan2(y, x));
        RenewAutoPlusInitialize.LOGGER.info(matrixStack.peek().getPositionMatrix());
        //matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float)Math.asin(-z)));
        //matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float)Math.atan2(y, x)));
        this.getContextModel().getHead().rotate(matrixStack); //Will have to suffice, can't get starting pos from matrix
        for(int heightOffset = 0; heightOffset < segments; heightOffset++) {
            matrixStack.push();
            TopazBeamFeatureRenderer.renderBeam(matrixStack, vertexConsumerProvider, light, livingEntity, tickDelta, heightOffset, 1);
            matrixStack.pop();
        }
        if(wand.isHitting()) {
            matrixStack.translate(0.0, -0.3, -(double)segments - 0.5);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
            TopazBeamFeatureRenderer.renderHitEffect(wand, matrixStack, vertexConsumerProvider, light, tickDelta, segments);
        }
        matrixStack.pop();
    }

    public static void renderHitEffect(TopazWandItem wand, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, int segments) {
        float vFramePosition = (float)Math.floor((double)((float)Math.floorMod(wand.getUser().getWorld().getTime(), 60) + tickDelta) / 1.5) * 0.25f;
        matrices.push();
        matrices.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityAlpha(HIT_TEXTURE));
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition + 0.25f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition + 0.25f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition + 0.25f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition + 0.25f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        matrices.pop();
    }

    public static void renderBeam(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, LivingEntity livingEntity, float tickDelta, int yOffset, int maxY) {
        int height = yOffset + maxY;
        final float innerRadius = 0.2f;
        final float outerRadius = 0.25f;
        float f = (float)Math.floorMod(livingEntity.getWorld().getTime(), 40) + tickDelta;
        float f2 = (float)livingEntity.getWorld().getTime() + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2f - (float)MathHelper.floor(g * 0.1f));
        float j = 1.0f;
        float k = 1.0f;
        float l = 1.0f;
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrixStack.push();
        VertexConsumer vertexConsumerTranslucent = vertexConsumerProvider.getBuffer(INNER_LAYER);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 9.0f - 45.0f));
        float m = 0.0f;
        float n = innerRadius;
        float o = innerRadius;
        float p = 0.0f;
        float q = -innerRadius;
        float r = 0.0f;
        float s = 0.0f;
        float t = -innerRadius;
        float w = -1.0f + h;
        float x = (float)maxY * (0.5f / innerRadius) + w;
        TopazBeamFeatureRenderer.renderBeamLayer(matrixStack, vertexConsumerTranslucent, 1.0f, 0.79f, 0.02f, 0.75f, yOffset, height, 0.0f, n, o, 0.0f, q, 0.0f, 0.0f, t, 0.0f, 1.0f, x, w);
        matrixStack.pop();
        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(OUTER_LAYER);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f2 * 9.0f + 45.0f));
        m = -outerRadius;
        n = -outerRadius;
        o = outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        r = outerRadius;
        s = outerRadius;
        t = outerRadius;
        if(yOffset % 8 > 3) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        if(yOffset % 4 < 2) {
            if(yOffset % 4 == 0) {
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, m, n, o, p, 0.3333f, 0.6666f, 0.5f, 0.0f);
            }
            else {
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, m, n, o, p, 0.3333f, 0.6666f, 1.0f, 0.5f);
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, o, p, s, t, 0.0f, 0.3333f, 1.0f, 0.5f);
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, q, r, m, n, 0.6666f, 1.0f, 1.0f, 0.5f);
            }
        }
        else {
            if(yOffset % 4 == 2){
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, s, t, q, r, 0.3333f, 0.6666f, 0.5f, 1.0f);
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, o, p, s, t, 0.6666f, 1.0f, 0.5f, 1.0f);
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, q, r, m, n, 0.0f, 0.3333f, 0.5f, 1.0f);
            }
            else {
                TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertexConsumer, j, k, l, 1.0f, yOffset, height, s, t, q, r, 0.3333f, 0.6666f, 0.0f, 0.5f);
            }
        }
        matrixStack.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        TopazBeamFeatureRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        TopazBeamFeatureRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        TopazBeamFeatureRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        TopazBeamFeatureRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        TopazBeamFeatureRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    /**
     * @param v the top-most coordinate of the texture region
     * @param u the left-most coordinate of the texture region
     */
    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }
}
