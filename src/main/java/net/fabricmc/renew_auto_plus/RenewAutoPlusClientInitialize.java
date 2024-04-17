package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class RenewAutoPlusClientInitialize implements ClientModInitializer {
    public static final EntityModelLayer FALLEN_MODEL_LAYER = new EntityModelLayer(new Identifier("renew_auto_plus", "fallen"), "main");
    
    @Override
    public void onInitializeClient() {
        HandledScreens.register(RenewAutoPlusInitialize.EXTRACTOR_SCREEN_HANDLER, ExtractorScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.CLOCK_BLOCK_SCREEN_HANDLER, ClockBlockScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.CAPACITOR_SCREEN_HANDLER, CapacitorScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.PUMP_SCREEN_HANDLER, PumpScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.ABACUS_SCREEN_HANDLER, AbacusScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.CRATE_SCREEN_HANDLER, CrateScreen::new);
        HandledScreens.register(RenewAutoPlusInitialize.WASTES_PORTAL_SCREEN_HANDLER, WastesPortalControllerScreen::new);

        DimensionRenderingRegistry.registerDimensionEffects(RenewAutoPlusInitialize.THE_WASTES, new WastesDimensionEffect());
        DimensionRenderingRegistry.registerSkyRenderer(RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY, new WastesSkyRenderer());

        BlockEntityRendererFactories.register(RenewAutoPlusInitialize.CLOCK_BLOCK_ENTITY, ClockBlockEntityRender::new);
        BlockEntityRendererFactories.register(RenewAutoPlusInitialize.PUMP_BLOCK_ENTITY, PumpBlockEntityRender::new);
        BlockEntityRendererFactories.register(RenewAutoPlusInitialize.WASTES_PORTAL_SURFACE_ENTITY, WastesPortalSurfaceBlockEntityRenderer::new);

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
