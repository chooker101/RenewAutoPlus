package net.fabricmc.renew_auto_plus;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class WastesPortalSurfaceBlockEntityRenderer<T extends WastesPortalSurfaceBlockEntity>
implements BlockEntityRenderer<T> {
    private Identifier[] layerIdentifiers;
    public static final Identifier CHEAP_LAYER = new Identifier("textures/environment/end_sky.png");
    private final TextureManager textureManager;
    private final ResourceManager resourceManager;
    private Resource[] layerResources;
    private NativeImage[] layerNativeImages;
    private final int renderTextureWidth = 64;
    private final int renderTextureHeight = 64;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final int updatePortalFrame = 5;
    private int currentFrame = 0;
    private HashMap<UUID, RenderLayer> renderLayers = new HashMap<>();
    private HashMap<UUID, NativeImageBackedTexture> renderedTextures = new HashMap<>();

    public WastesPortalSurfaceBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        layerIdentifiers = new Identifier[4];
        layerResources = new Resource[4];
        layerNativeImages = new NativeImage[4];
        for (int i = 0; i < 4; ++i) {
            layerIdentifiers[i] = new Identifier(String.format("renew_auto_plus:textures/block/test_portal_surface/test_portal_surface_%02d.png", i));
        }
        textureManager = MinecraftClient.getInstance().getTextureManager();
        resourceManager = MinecraftClient.getInstance().getResourceManager();
        for (int i = 0; i < 4; ++i) { 
            try {
                layerResources[i] = resourceManager.getResource(layerIdentifiers[i]);
            }
            catch(IOException exception) {
                RenewAutoPlusInitialize.LOGGER.debug("Failed to load wastes portal asset!");
                layerResources[i] = null;
            }
            if(layerResources[i] != null) {
                try {
                    layerNativeImages[i] = NativeImage.read(layerResources[i].getInputStream());
                }
                catch(IOException exception2) {
                    RenewAutoPlusInitialize.LOGGER.debug("Failed to create native image!");
                    layerNativeImages[i] = null;
                }
            }
            else {
                RenewAutoPlusInitialize.LOGGER.debug("Failed to create native image!");
                layerNativeImages[i] = null;
            }
        }
    }

    //Fast-ish and approximate
    public void rasterizePortal(T wastesPortalSurfaceBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        BlockPos blockPos = wastesPortalSurfaceBlockEntity.getPos();
        Vec3f cornerPos0 = new Vec3f(((float)blockPos.getX()) + 1.0f, ((float)blockPos.getY()) + 1.0f, blockPos.getZ()); //Need to check direction
        Vec3f cornerPos1 = new Vec3f(((float)blockPos.getX()), ((float)blockPos.getY()) + 1.0f, blockPos.getZ());
        Vec3f cornerPos2 = new Vec3f(((float)blockPos.getX()) + 1.0f, ((float)blockPos.getY()), blockPos.getZ());
        Vec3f renderPos = new Vec3f(0.0f, 0.0f, 0.0f);
        //Need to check if x or z dir
        int textureUOffset = 16 * wastesPortalSurfaceBlockEntity.getXBlockPos(); //Texture width of single block
        int textureVOffset = 16 * wastesPortalSurfaceBlockEntity.getYBlockPos();
        float xStride = (cornerPos1.getX() - cornerPos0.getX()) / renderTextureWidth;
        float yStride = (cornerPos2.getY() - cornerPos0.getY()) / renderTextureHeight;
        float zOffset = 30.0f;
        Vec3f cameraPos = new Vec3f(client.gameRenderer.getCamera().getPos());
        NativeImageBackedTexture nativeImage = renderedTextures.get(wastesPortalSurfaceBlockEntity.getImageUUID());

        for(int y = 0; y < renderTextureHeight; y++) {   
            for(int x = 0; x < renderTextureWidth; x++) {
                renderPos.set(cornerPos0.getX() + xStride * x, cornerPos0.getY() + yStride * y, blockPos.getZ());
                renderPos.subtract(cameraPos);
                renderPos.normalize();
                float yDot = (float)Math.acos((double)renderPos.dot(Vec3f.POSITIVE_Y));
                float currentYAngle = (float)Math.tan(1.5708f + yDot);
                float xDot = (float)Math.acos((double)renderPos.dot(Vec3f.NEGATIVE_X)); //Also needs direction check
                float currentXAngle = (float)Math.tan(1.5708f + xDot);
                for(int z = 0; z < 4; z++) {
                    float currentZDistance = zOffset * z;
                    float perspectiveXPos = x + currentZDistance * currentXAngle;
                    float perspectiveYPos = y + currentZDistance * currentYAngle;
                    int colour = getNearestColour(textureUOffset, textureVOffset, z, perspectiveXPos, perspectiveYPos);
                    //Check if mask above alpha values
                    if(NativeImage.getAlpha(colour) != 0) {
                        nativeImage.getImage().setColor(x, y, colour);
                        break;
                    } else if (z >= 3) {
                        nativeImage.getImage().setColor(x, y, 0xFF000000);
                    }
                }
            }
        }
        nativeImage.upload();
    }

    //Assumes renderTexture coords
    public int getNearestColour(int textureUOffset, int textureVOffset, int z, double x, double y) {
        NativeImage nativeImage = layerNativeImages[z];
        if(nativeImage == null) {
            return 0xFF000000;
        }

        //Need to interpolate
        int u = textureUOffset + (int)Math.round(x * (16.0 / (double)renderTextureWidth));
        int v = textureVOffset + (int)Math.round(y * (16.0 / (double)renderTextureHeight));
        if(u >= 48 || u < 0) {
            return 0xFF000000;
        }
        if(v >= 48 || v < 0) {
            return 0xFF000000;
        }
        return nativeImage.getColor(u, v);
    }

    @Override
    public void render(T wastesPortalSurfaceBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if(!wastesPortalSurfaceBlockEntity.getWorld().isClient()) {
            return;
        }
        if(client.options.graphicsMode == GraphicsMode.FANCY) {
            if(!wastesPortalSurfaceBlockEntity.hasImage()) { //This is shit
                UUID uuid = wastesPortalSurfaceBlockEntity.getImageUUID();
                renderedTextures.put(uuid, new NativeImageBackedTexture(renderTextureWidth, renderTextureHeight, true));
                Identifier textureIdentifier = textureManager.registerDynamicTexture(String.format("dynamic_portal_face_%s", uuid), renderedTextures.get(uuid));
                renderLayers.put(uuid, RenderLayer.getText(textureIdentifier));
                wastesPortalSurfaceBlockEntity.setHasImage(true);
            }
            if(currentFrame == updatePortalFrame) {
                this.rasterizePortal(wastesPortalSurfaceBlockEntity, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);
                currentFrame = 0;
            }
            currentFrame++;

            matrixStack.push();
            //Scale and draw front face
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
            Direction.Axis axis = wastesPortalSurfaceBlockEntity.getAxis();
            if(axis == Direction.Axis.X) {
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
                matrixStack.translate(-128.0, 0.0, -128.0);
            }
            if(axis == Direction.Axis.Z) {
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
                matrixStack.translate(-128.0, 0.0, -128.0);
            }
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayers.get(wastesPortalSurfaceBlockEntity.getImageUUID()));
            
            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(240).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(240).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(240).next();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(240).next();
            matrixStack.pop();
        }
        else {
            //Render basic portal
        }
    }
}
