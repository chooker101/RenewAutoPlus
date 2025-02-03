package net.renew_auto_plus;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RotationAxis;

public class WastesSkyRenderer implements SkyRenderer {
    private VertexBuffer starsBuffer;
    private VertexBuffer lightSkyBuffer;
    private VertexBuffer darkSkyBuffer;
    private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    private static final Identifier BLACK_HOLE = new Identifier("renew_auto_plus", "textures/environment/black_hole.png");
    private boolean first_render = true;

    private void renderDarkSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.darkSkyBuffer != null) {
            this.darkSkyBuffer.close();
        }
        this.darkSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.BuiltBuffer builtBuffer = WastesSkyRenderer.renderSky(bufferBuilder, 16.0f);
        this.darkSkyBuffer.bind();
        this.darkSkyBuffer.upload(builtBuffer);
    }

    private void renderLightSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.lightSkyBuffer != null) {
            this.lightSkyBuffer.close();
        }
        this.lightSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        //WastesSkyRenderer.renderSky(bufferBuilder, 16.0f);
        BufferBuilder.BuiltBuffer builtBuffer = WastesSkyRenderer.renderSky(bufferBuilder, 16.0f);
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.upload(builtBuffer);
    }

    private static BufferBuilder.BuiltBuffer renderSky(BufferBuilder builder, float f) {
        float g = Math.signum(f) * 512.0f;
        //RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
        builder.vertex(0.0, f, 0.0).next();
        for (int i = -180; i <= 180; i += 45) {
            builder.vertex(g * MathHelper.cos((float)i * ((float)Math.PI / 180)), f, 512.0f * MathHelper.sin((float)i * ((float)Math.PI / 180))).next();
        }
        return builder.end();
    }

    private void renderStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        if (this.starsBuffer != null) {
            this.starsBuffer.close();
        }
        this.starsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.BuiltBuffer builtBuffer = this.renderStars(bufferBuilder);
        this.starsBuffer.bind();
        this.starsBuffer.upload(builtBuffer);
    }

    private BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer) {
        Random random = new Random(10842L);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            double e = random.nextFloat() * 2.0f - 1.0f;
            double f = random.nextFloat() * 2.0f - 1.0f;
            double g = 0.15f + random.nextFloat() * 0.1f;
            double h = d * d + e * e + f * f;
            if (!(h < 1.0) || !(h > 0.01)) continue;
            h = 1.0 / Math.sqrt(h);
            double j = (d *= h) * 100.0;
            double k = (e *= h) * 100.0;
            double l = (f *= h) * 100.0;
            double m = Math.atan2(d, f);
            double n = Math.sin(m);
            double o = Math.cos(m);
            double p = Math.atan2(Math.sqrt(d * d + f * f), e);
            double q = Math.sin(p);
            double r = Math.cos(p);
            double s = random.nextDouble() * Math.PI * 2.0;
            double t = Math.sin(s);
            double u = Math.cos(s);
            for (int v = 0; v < 4; ++v) {
                double x = (double)((v & 2) - 1) * g;
                double y = (double)((v + 1 & 2) - 1) * g;
  
                double aa = x * u - y * t;
                double ac = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ac * o;
                double ag = ad;
                double ah = ac * n + ae * o;
                buffer.vertex(j + af, k + ag, l + ah).next();
            }
        }
        return buffer.end();
    }

    private boolean hasBlindnessOrDarkness(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
        }
        return false;
    }

    @Override
    public void render(WorldRenderContext context) {
        ClientWorld world = context.world();
        float tickDelta = context.tickDelta();
        MatrixStack matrices = context.matrixStack();
        Matrix4f projectionMatrix = context.projectionMatrix();
        GameRenderer gameRenderer = context.gameRenderer();
        Camera camera = context.camera();
        if(first_render) {
            renderDarkSky();
            renderLightSky();
            renderStars();
            first_render = false;
        }
        //float q;
        //float p;
        //float o;
        //int m;
        //float k;
        //float i;
        ////runnable.run();
        //if (world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
        //    RenderSystem.enableBlend();
        //    RenderSystem.defaultBlendFunc();
        //    RenderSystem.depthMask(false);
        //    RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        //    RenderSystem.setShaderTexture(0, END_SKY);
        //    Tessellator tessellator = Tessellator.getInstance();
        //    BufferBuilder bufferBuilder = tessellator.getBuffer();
        //    for (int j = 0; j < 6; ++j) {
        //        matrices.push();
        //        if (j == 1) {
        //            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        //        }
        //        if (j == 2) {
        //            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        //        }
        //        if (j == 3) {
        //            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        //        }
        //        if (j == 4) {
        //            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        //        }
        //        if (j == 5) {
        //            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f));
        //        }
        //        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        //        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        //        bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();
        //        bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();
        //        bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();
        //        bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();
        //        tessellator.draw();
        //        matrices.pop();
        //    }
        //    RenderSystem.depthMask(true);
        //    //RenderSystem.enableTexture();
        //    RenderSystem.disableBlend();
        //    return;
        //}
        //if (world.getDimensionEffects().getSkyType() != DimensionEffects.SkyType.NORMAL) {
        //    return;
        //}
        ////RenderSystem.disableTexture();
        //Vec3d vec3d = world.getSkyColor(gameRenderer.getCamera().getPos(), tickDelta);
        //float f = (float)vec3d.x;
        //float g = (float)vec3d.y;
        //float h = (float)vec3d.z;
        //BackgroundRenderer.clearFog();
        //BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        //RenderSystem.depthMask(false);
        //RenderSystem.setShaderColor(f, g, h, 1.0f);
        //ShaderProgram shader = RenderSystem.getShader();
        //this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
        //RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();
        //float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
        //if (fs != null) {
        //    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        //    //RenderSystem.disableTexture();
        //    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //    matrices.push();
        //    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        //    i = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f;
        //    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
        //    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        //    float j = fs[0];
        //    k = fs[1];
        //    float l = fs[2];
        //    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        //    bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        //    bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(j, k, l, fs[3]).next();
        //    m = 16;
        //    for (int n = 0; n <= 16; ++n) {
        //        o = (float)n * ((float)Math.PI * 2) / 16.0f;
        //        p = MathHelper.sin(o);
        //        q = MathHelper.cos(o);
        //        bufferBuilder.vertex(matrix4f, p * 120.0f, q * 120.0f, -q * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
        //    }
        //    BufferRenderer.draw(bufferBuilder.end());
        //    matrices.pop();
        //}
        ////RenderSystem.enableColorLogicOp();
        ////RenderSystem.enableTexture();
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        //matrices.push();
        //i = 1.0f - world.getRainGradient(tickDelta);
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, i);
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        //Matrix4f j = matrices.peek().getPositionMatrix();
        //k = 30.0f;
        //RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        //RenderSystem.setShaderTexture(0, BLACK_HOLE);
        //bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        //bufferBuilder.vertex(j, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        //bufferBuilder.vertex(j, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        //bufferBuilder.vertex(j, k, 100.0f, k).texture(1.0f, 1.0f).next();
        //bufferBuilder.vertex(j, -k, 100.0f, k).texture(0.0f, 1.0f).next();
        //BufferRenderer.draw(bufferBuilder.end());
        //k = 20.0f;
        //RenderSystem.setShaderTexture(0, MOON_PHASES);
        //int l = world.getMoonPhase();
        //int matrix4f = l % 4;
        //m = l / 4 % 2;
        //float n = (float)(matrix4f + 0) / 4.0f;
        //o = (float)(m + 0) / 2.0f;
        //p = (float)(matrix4f + 1) / 4.0f;
        //q = (float)(m + 1) / 2.0f;
        //bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        //bufferBuilder.vertex(j, -k, -100.0f, k).texture(p, q).next();
        //bufferBuilder.vertex(j, k, -100.0f, k).texture(n, q).next();
        //bufferBuilder.vertex(j, k, -100.0f, -k).texture(n, o).next();
        //bufferBuilder.vertex(j, -k, -100.0f, -k).texture(p, o).next();
        //BufferRenderer.draw(bufferBuilder.end());
        ////RenderSystem.disableTexture();
        //float skyAngle = world.getSkyAngle(tickDelta);
        //float someAngleConversion = 1.0f - (MathHelper.cos(skyAngle * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        //someAngleConversion = MathHelper.clamp(someAngleConversion, 0.0f, 1.0f);
        //float r = (someAngleConversion * someAngleConversion * 0.5f) * i;
        //if (r > 0.0f) {
        //    RenderSystem.setShaderColor(r, r, r, r);
        //    BackgroundRenderer.clearFog();
        //    this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
        //    //runnable.run();
        //}
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //RenderSystem.disableBlend();
        //matrices.pop();
        ////RenderSystem.disableTexture();
        //RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        //double i2 = gameRenderer.getCamera().getPos().y - world.getLevelProperties().getSkyDarknessHeight(world);
        //if (i2 < 0.0) {
        //    matrices.push();
        //    matrices.translate(0.0, 12.0, 0.0);
        //    this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
        //    matrices.pop();
        //}
        //if (world.getDimensionEffects().isAlternateSkyColor()) {
        //    RenderSystem.setShaderColor(f * 0.2f + 0.04f, g * 0.2f + 0.04f, h * 0.6f + 0.1f, 1.0f);
        //} else {
        //    RenderSystem.setShaderColor(f, g, h, 1.0f);
        //}
        ////RenderSystem.enableTexture();
        //RenderSystem.depthMask(true);

        float q;
        float p;
        float o;
        int m;
        float k;
        float i;
        //fogCallback.run();
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA || this.hasBlindnessOrDarkness(camera)) {
            return;
        }
        if (world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
            return;
        }
        Vec3d vec3d = world.getSkyColor(gameRenderer.getCamera().getPos(), tickDelta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        BackgroundRenderer.applyFogColor();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, g, h, 1.0f);
        ShaderProgram shaderProgram = RenderSystem.getShader();
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
        if (fs != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            i = MathHelper.sin((float)world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            float j = fs[0];
            k = fs[1];
            float l = fs[2];
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(j, k, l, fs[3]).next();
            m = 16;
            for (int n = 0; n <= 16; ++n) {
                o = (float)n * ((float)Math.PI * 2) / 16.0f;
                p = MathHelper.sin((float)o);
                q = MathHelper.cos((float)o);
                bufferBuilder.vertex(matrix4f, p * 120.0f, q * 120.0f, -q * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.push();
        i = 1.0f - world.getRainGradient(tickDelta);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, i);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0f));
        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        k = 30.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, BLACK_HOLE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        k = 20.0f;
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        int r = world.getMoonPhase();
        int s = r % 4;
        m = r / 4 % 2;
        float t = (float)(s + 0) / 4.0f;
        o = (float)(m + 0) / 2.0f;
        p = (float)(s + 1) / 4.0f;
        q = (float)(m + 1) / 2.0f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, k).texture(p, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, k).texture(t, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, -k).texture(t, o).next();
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, -k).texture(p, o).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        float u = world.method_23787(tickDelta) * i;
        if (u > 0.0f) {
            RenderSystem.setShaderColor(u, u, u, u);
            BackgroundRenderer.clearFog();
            this.starsBuffer.bind();
            this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
            VertexBuffer.unbind();
            //fogCallback.run();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.pop();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        double d = camera.getPos().y - world.getLevelProperties().getSkyDarknessHeight(world);
        if (d < 0.0) {
            matrices.push();
            matrices.translate(0.0f, 12.0f, 0.0f);
            this.darkSkyBuffer.bind();
            this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
            VertexBuffer.unbind();
            matrices.pop();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
    }
}
