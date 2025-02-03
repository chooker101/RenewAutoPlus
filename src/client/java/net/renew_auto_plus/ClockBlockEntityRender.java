package net.renew_auto_plus;

import java.io.IOException;
import java.util.NoSuchElementException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class ClockBlockEntityRender<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final NativeImageBackedTexture texture;
    private final RenderLayer renderLayer;
    private final TextureManager textureManager;
    private final ResourceManager resourceManager;
    private Identifier frontIdentifier;
    private Identifier[] clockIdentifiers;
    private double time;
    private double step;
    private long lastTick;
    private int lastClockFace = -1;

    ClockBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        frontIdentifier = new Identifier("renew_auto_plus:textures/block/clock_block_front.png");
        clockIdentifiers = new Identifier[64];
        for (int i = 0; i < 64; ++i) {
            clockIdentifiers[i] = new Identifier(String.format("minecraft:textures/item/clock_%02d.png", i));
        }
        //register dynamic texture
        texture = new NativeImageBackedTexture(16, 16, true);
        textureManager = MinecraftClient.getInstance().getTextureManager();
        resourceManager = MinecraftClient.getInstance().getResourceManager();
        Identifier identifier = textureManager.registerDynamicTexture("dynamic_clock_face", this.texture);
        renderLayer = RenderLayer.getText(identifier);

        Resource resource = null;
        NativeImage nativeImage = null;
        try {
            resource = resourceManager.getResource(frontIdentifier).get();
        }
        catch(NoSuchElementException exception) {
            return;
        }
        try {
            nativeImage = NativeImage.read(resource.getInputStream());
        }
        catch(IOException exception2) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.texture.getImage().setColor(j, i, nativeImage.getColor(j, i));
            }
        }
        this.texture.upload();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return false;
    }

    public float UdateClockTime(ClientWorld clientWorld) {
        //double skyAngle = clientWorld.getDimension().isNatural() ? (double)clientWorld.getSkyAngle(1.0f) : Math.random();
        double skyAngle = clientWorld.getDimension().hasSkyLight() ? (double)clientWorld.getSkyAngle(1.0f) : Math.random();
        if (clientWorld.getTime() != this.lastTick) {
            this.lastTick = clientWorld.getTime();
            double d = skyAngle - this.time;
            d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
            this.step += d * 0.1;
            this.step *= 0.9;
            this.time = MathHelper.floorMod(this.time + this.step, 1.0);
        }
        return (float)this.time;
    }

    private void UpdateFaceTexture(int clock_image)
    {
        if(clock_image >= 64) {
            return;
        }
        Resource resource = null;
        NativeImage nativeImage = null;
        try {
            resource = resourceManager.getResource(clockIdentifiers[clock_image]).get();
        }
        catch(NoSuchElementException exception) {
            return;
        }
        try {
            nativeImage = NativeImage.read(resource.getInputStream());
        }
        catch(IOException exception2) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int color = nativeImage.getColor(j, i);
                if(ColorHelper.Abgr.getAlpha(color) != 0) {
                    this.texture.getImage().setColor(j, i, color);
                }
            }
        }
        this.texture.upload();
    }
    
    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(!blockEntity.getWorld().isClient()) {
            return;
        }
        float clockTime = UdateClockTime((ClientWorld)blockEntity.getWorld());
        int clockFaceFromTime = (int)(clockTime * 64.0f);
        if(clockFaceFromTime != lastClockFace) {
            UpdateFaceTexture(clockFaceFromTime);
            lastClockFace = clockFaceFromTime;
        }

        matrices.push();
        //Scale and draw front face
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        BlockState blockState = blockEntity.getCachedState();
        Direction facing = blockState.get(Properties.HORIZONTAL_FACING);
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().north());
        if(facing == Direction.SOUTH) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            matrices.translate(-128.0, 0.0, -128.0);
            lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().south());
        }
        if(facing == Direction.EAST) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
            matrices.translate(0.0, 0.0, -128.0);
            lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().east());
        }
        if(facing == Direction.WEST) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            matrices.translate(-128.0, 0.0, 0.0);
            lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().west());
        }
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
        //lightAbove = lightAbove - (lightAbove / 16);
        
        vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(128, 128, 128, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(128, 128, 128, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(128, 128, 128, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(128, 128, 128, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        matrices.pop();
    }
}
