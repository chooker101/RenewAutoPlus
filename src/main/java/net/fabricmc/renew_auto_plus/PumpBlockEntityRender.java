package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class PumpBlockEntityRender<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private Identifier textureIdentifier;
    private final RenderLayer renderLayer;

    PumpBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        textureIdentifier = new Identifier("renew_auto_plus:textures/block/clock_block_front.png");
        //clockIdentifiers = new Identifier[64];
        //for (int i = 0; i < 64; ++i) {
        //    clockIdentifiers[i] = new Identifier(String.format("minecraft:textures/item/clock_%02d.png", i));
        //}
        ////register dynamic texture
        //texture = new NativeImageBackedTexture(16, 16, true);
        //textureManager = MinecraftClient.getInstance().getTextureManager();
        //resourceManager = MinecraftClient.getInstance().getResourceManager();
        //Identifier identifier = textureManager.registerDynamicTexture("dynamic_clock_face", this.texture);
        renderLayer = RenderLayer.getText(textureIdentifier);
        //Resource resource = null;
        //NativeImage nativeImage = null;
        //try {
        //    resource = resourceManager.getResource(frontIdentifier);
        //}
        //catch(IOException exception) {
        //    return;
        //}
        //try {
        //    nativeImage = NativeImage.read(resource.getInputStream());
        //}
        //catch(IOException exception2) {
        //    return;
        //}
        //for (int i = 0; i < 16; ++i) {
        //    for (int j = 0; j < 16; ++j) {
        //        this.texture.getImage().setColor(j, i, nativeImage.getColor(j, i));
        //    }
        //}
        //this.texture.upload();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return true;
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(!blockEntity.getWorld().isClient()) {
            return;
        }
        //float clockTime = UdateClockTime((ClientWorld)blockEntity.getWorld());
        //int clockFaceFromTime = (int)(clockTime * 64.0f);
        //UpdateFaceTexture(clockFaceFromTime);

        matrices.push();
        //Scale and draw front face
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        //BlockState blockState = blockEntity.getCachedState();
        //Direction facing = blockState.get(Properties.HORIZONTAL_FACING);
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        //if(facing == Direction.SOUTH) {
        //    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        //    matrices.translate(-128.0, 0.0, -128.0);
        //    lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().south());
        //}
        //if(facing == Direction.EAST) {
        //    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        //    matrices.translate(0.0, 0.0, -128.0);
        //    lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().east());
        //}
        //if(facing == Direction.WEST) {
        //    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        //    matrices.translate(-128.0, 0.0, 0.0);
        //    lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().west());
        //}
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
        lightAbove = lightAbove - (lightAbove / 16);
        
        //vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        //vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        //vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        //vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        matrices.pop();

        matrices.push();
        lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        matrices.translate(56.0, 104.0, -40.0);
        //matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-15.0f));
        matrix4f = matrices.peek().getPositionMatrix();

        //front
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        
        //left
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        matrices.translate(-64.0, 0.0, 0.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        
        //right
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        matrices.translate(-64.0, 0.0, -16.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();

        //back
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        matrices.translate(0.0, 0.0, -64.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 8.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        
        //up
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        matrices.translate(0.0, 0.0, -8.0);
        matrix4f = matrices.peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();
        
        //down
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        matrices.translate(0.0, -64.0, -8.0);
        matrix4f = matrices.peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(lightAbove).next();

        matrices.pop();
    }
}
