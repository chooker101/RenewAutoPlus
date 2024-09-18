package net.fabricmc.renew_auto_plus.helper;

import net.fabricmc.renew_auto_plus.RubyWandItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

public abstract class RubyChargeRenderHelper {
    public static final Identifier texture = new Identifier("renew_auto_plus", "textures/entity/projectiles/ruby_basic_charge.png");

    public static void renderChargeAnim(RubyWandItem wand, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float vFramePosition = (float)Math.floor((double)(((float)wand.chargeAnimationFrame) / 1.5f)) * 0.0714f;
        matrices.push();
        matrices.translate(0.0, 0.9, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityAlpha(texture));
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition + 0.0714f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition + 0.0714f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition + 0.0714f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 0.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition + 0.0714f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 1.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(1.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(positionMatrix, 0.0f - 0.5f, 1.0f - 0.25f, 0.0f).color(255, 255, 255, 255).texture(0.0f, vFramePosition).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
        matrices.pop();
    }
}