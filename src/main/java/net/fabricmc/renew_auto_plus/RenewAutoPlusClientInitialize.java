package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class RenewAutoPlusClientInitialize implements ClientModInitializer {
    public static final EntityModelLayer FALLEN_MODEL_LAYER = new EntityModelLayer(new Identifier("renew_auto_plus", "fallen"), "main");
    
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(RenewAutoPlusInitialize.EXTRACTOR_SCREEN_HANDLER, ExtractorScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CLOCK_BLOCK_SCREEN_HANDLER, ClockBlockScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CAPACITOR_SCREEN_HANDLER, CapacitorScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.PUMP_SCREEN_HANDLER, PumpScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.ABACUS_SCREEN_HANDLER, AbacusScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CRATE_SCREEN_HANDLER, CrateScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.WASTES_PORTAL_SCREEN_HANDLER, WastesPortalControllerScreen::new);

        DimensionRenderingRegistry.registerDimensionEffects(RenewAutoPlusInitialize.THE_WASTES, new WastesDimensionEffect());
        DimensionRenderingRegistry.registerSkyRenderer(RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY, new WastesSkyRenderer());

        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.CLOCK_BLOCK_ENTITY, ClockBlockEntityRender::new);
        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.PUMP_BLOCK_ENTITY, PumpBlockEntityRender::new);
        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.WASTES_PORTAL_SURFACE_ENTITY, WastesPortalSurfaceBlockEntityRenderer::new);

        EntityRendererRegistry.register(RenewAutoPlusInitialize.BANISHED_ENTITY, BanishedEntityRenderer::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.GHOST_BANISHED_ENTITY, GhostBanishedEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.FALLEN_ENTITY, FallenEntityRenderer::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AMETHYST_BASIC_PROJECTILE_ENTITY, AmethystBasicProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AQUAMARINE_BASIC_PROJECTILE_ENTITY, AquamarineBasicProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AQUAMARINE_SPECIAL_PROJECTILE_ENTITY, AquamarineSpecialProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.RUBY_BASIC_PROJECTILE_ENTITY, RubyBasicProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.ONYX_BASIC_PROJECTILE_ENTITY, OnyxBasicProjectileEntityRender::new);

        EntityModelLayerRegistry.registerModelLayer(FALLEN_MODEL_LAYER, FallenEntityModel::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), RenewAutoPlusInitialize.LETTUCE_BLOCK);
    }
}
