package net.renew_auto_plus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class PumpBlockEntityRender<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private Identifier handleTextureIdentifier;
    private Identifier lavaTextureIdentifier;
    private Identifier waterTextureIdentifier;
    private final RenderLayer handleRenderLayer;
    private final RenderLayer lavaRenderLayer;
    private final RenderLayer waterRenderLayer;

    PumpBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        handleTextureIdentifier = new Identifier("renew_auto_plus:textures/block/pump_handle.png");
        lavaTextureIdentifier = new Identifier("renew_auto_plus:textures/block/pump_bowl_lava.png");
        waterTextureIdentifier = new Identifier("renew_auto_plus:textures/block/pump_bowl_water.png");

        handleRenderLayer = RenderLayer.getText(handleTextureIdentifier);
        lavaRenderLayer = RenderLayer.getText(lavaTextureIdentifier);
        waterRenderLayer = RenderLayer.getText(waterTextureIdentifier);
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

        BlockState state = blockEntity.getCachedState();
        PumpBlockEntity pumpBlockEntity = ((PumpBlockEntity)blockEntity);
        
        if(state.get(PumpBlock.POWERED)) { 
            pumpBlockEntity.angleCounter = (pumpBlockEntity.angleCounter + tickDelta) % 360.0f; // Instanced on client entity so not all move
        }
        float xSineInRadians = (float)Math.toRadians(pumpBlockEntity.angleCounter);
        float ySineInRadians = 0.3927f * (float)Math.sin(xSineInRadians - Math.PI / 2) + 0.3927f;
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());

        Direction facing = state.get(PumpBlock.FACING);

        // Handle
        matrices.push();
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);

        //Handle rotation
        if(facing == Direction.NORTH) {
            matrices.translate(56.0, 104.0 - (Math.sin(ySineInRadians) * 48), -40.0 + (48 - Math.cos(ySineInRadians) * 48));
        }
        else if(facing == Direction.EAST) {
            matrices.translate(168.0 - (48 - Math.cos(ySineInRadians) * 48), 104.0 - (Math.sin(ySineInRadians) * 48), 56.0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
        }
        else if(facing == Direction.SOUTH) {
            matrices.translate(72.0, 104.0 - (Math.sin(ySineInRadians) * 48), 168.0 - (48 - Math.cos(ySineInRadians) * 48));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        else if(facing == Direction.WEST) {
            matrices.translate(-40.0 + (48 - Math.cos(ySineInRadians) * 48), 104.0 - (Math.sin(ySineInRadians) * 48), 72.0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        }

        matrices.multiply(RotationAxis.POSITIVE_X.rotation(-ySineInRadians));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.handleRenderLayer);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        //front
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0f, 0.5625f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5625f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.0f, 0.5f).light(lightAbove).next();
        
        //left
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        matrices.translate(-64.0, 0.0, 0.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0625f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0625f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.0f).light(lightAbove).next();
        
        //right
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.translate(-64.0, 0.0, -16.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0625f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0625f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.0f).light(lightAbove).next();

        //back
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        matrices.translate(0.0, 0.0, -64.0);
        matrix4f = matrices.peek().getPositionMatrix();
        
        vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.0f, 0.5625f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 8.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5625f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.0f, 0.5f).light(lightAbove).next();
        
        //up
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        matrices.translate(0.0, 0.0, -8.0);
        matrix4f = matrices.peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(224, 224, 224, 255).texture(0.0f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(224, 224, 224, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(224, 224, 224, 255).texture(0.125f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(224, 224, 224, 255).texture(0.0f, 0.0f).light(lightAbove).next();
        
        //down
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        matrices.translate(0.0, -64.0, -8.0);
        matrix4f = matrices.peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(160, 160, 160, 255).texture(0.25f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.0f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.125f, 0.5f).light(lightAbove).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(160, 160, 160, 255).texture(0.25f, 0.5f).light(lightAbove).next();

        matrices.pop();
        int fluidLevel = state.get(PumpBlock.LEVEL).intValue();
        int alpha = 160;
        int r = 128;
        int g = 128;
        int b = 128;

        if(state.get(PumpBlock.POWERED)) {
            // Pour flow
            matrices.push();
            matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
            
            if(facing == Direction.NORTH) {
                matrices.translate(56.0, 72.0, 40.0);
            }
            else if(facing == Direction.EAST) {
                matrices.translate(88.0, 72.0, 56.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            }
            else if(facing == Direction.SOUTH) {
                matrices.translate(72.0, 72.0, 88.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
            else if(facing == Direction.WEST) {
                matrices.translate(40.0, 72.0, 72.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            if(state.get(PumpBlock.LIQUID_TYPE)) {
                vertexConsumer = vertexConsumers.getBuffer(this.waterRenderLayer);
                alpha = 160;
                r = 160;
                g = 160;
                b = 160;
            }
            else {
                vertexConsumer = vertexConsumers.getBuffer(this.lavaRenderLayer);
                alpha = 255;
                r = 224;
                g = 224;
                b = 224;
            }
            matrix4f = matrices.peek().getPositionMatrix();
            

            //top
            if(fluidLevel < 4) {
                vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0625f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.0625f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0f).light(lightAbove).next();
            }
            
            //left
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            matrices.translate(-64.0, 0.0, 0.0);
            matrix4f = matrices.peek().getPositionMatrix();
            
            if(fluidLevel < 4) {
                vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.6875f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.6875f, 0.5f).light(lightAbove).next();
            }
            
            //right
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            matrices.translate(-64.0, 0.0, -16.0);
            matrix4f = matrices.peek().getPositionMatrix();
            
            if(fluidLevel < 4) {
                vertexConsumer.vertex(matrix4f, 0.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.6875f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 64.0f, 8.0f, 0.0f).color(r, g, b, alpha).texture(0.6875f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 64.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.0f).light(lightAbove).next();
            }
            
            //front
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
            matrices.translate(0.0, 0.0, -64.0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            matrices.translate(0.0, 0.0, -8.0);
            matrix4f = matrices.peek().getPositionMatrix();

            if(fluidLevel < 4) {
                vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.5f).light(lightAbove).next();
            }
            
            //back
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            matrices.translate(0.0, -64.0, -8.0);
            matrix4f = matrices.peek().getPositionMatrix();

            if(fluidLevel < 4) {
                vertexConsumer.vertex(matrix4f, 0.0f, 64.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 64.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.5f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 16.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0f).light(lightAbove).next();
                vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.75f, 0.0f).light(lightAbove).next();
            }
            matrices.pop();
        }

        //Level
        if (fluidLevel > 0) {
            float levelHeight = fluidLevel * 8.0f;
            matrices.push();
            matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
            if(facing == Direction.NORTH) {
                matrices.translate(24.0, 8.0 + levelHeight, 40.0);
            }
            else if(facing == Direction.EAST) {
                matrices.translate(88.0, 8.0 + levelHeight, 24.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            }
            else if(facing == Direction.SOUTH) {
                matrices.translate(104.0, 8.0 + levelHeight, 88.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
            else if(facing == Direction.WEST) {
                matrices.translate(40.0, 8.0 + levelHeight, 104.0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            }
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            if(state.get(PumpBlock.LIQUID_TYPE)) {
                vertexConsumer = vertexConsumers.getBuffer(this.waterRenderLayer);
                alpha = 160;
                r = 160;
                g = 160;
                b = 160;
            }
            else {
                vertexConsumer = vertexConsumers.getBuffer(this.lavaRenderLayer);
                alpha = 255;
                r = 255;
                g = 255;
                b = 255;
            }
            matrix4f = matrices.peek().getPositionMatrix();

            vertexConsumer.vertex(matrix4f, 0.0f, 80.0f, 0.0f).color(r, g, b, alpha).texture(0.0f, 0.625f).light(lightAbove).next();
            vertexConsumer.vertex(matrix4f, 80.0f, 80.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.625f).light(lightAbove).next();
            vertexConsumer.vertex(matrix4f, 80.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.625f, 0.0f).light(lightAbove).next();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(r, g, b, alpha).texture(0.0f, 0.0f).light(lightAbove).next();
            matrices.pop();
        }
    }
}
