package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class RenewAutoPlusClientInitialize implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(RenewAutoPlusInitialize.EXTRACTOR_SCREEN_HANDLER, ExtractorScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CLOCK_BLOCK_SCREEN_HANDLER, ClockBlockScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CAPACITOR_SCREEN_HANDLER, CapacitorScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.PUMP_SCREEN_HANDLER, PumpScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.ABACUS_SCREEN_HANDLER, AbacusScreen::new);
        ScreenRegistry.register(RenewAutoPlusInitialize.CRATE_SCREEN_HANDLER, CrateScreen::new);

        DimensionRenderingRegistry.registerDimensionEffects(RenewAutoPlusInitialize.THE_WASTES, new WastesDimensionEffect());
        DimensionRenderingRegistry.registerSkyRenderer(RenewAutoPlusInitialize.THE_WASTES_REGISTRY_KEY, new WastesSkyRenderer());

        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.CLOCK_BLOCK_ENTITY, ClockBlockEntityRender::new);
        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.PUMP_BLOCK_ENTITY, PumpBlockEntityRender::new);

        EntityRendererRegistry.register(RenewAutoPlusInitialize.BANISHED_ENTITY, BanishedEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AMETHYST_BASIC_PROJECTILE_ENTITY, AmethystBasicProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AQUAMARINE_BASIC_PROJECTILE_ENTITY, AquamarineBasicProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.AQUAMARINE_SPECIAL_PROJECTILE_ENTITY, AquamarineSpecialProjectileEntityRender::new);
        EntityRendererRegistry.register(RenewAutoPlusInitialize.RUBY_BASIC_PROJECTILE_ENTITY, RubyBasicProjectileEntityRender::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), RenewAutoPlusInitialize.LETTUCE_BLOCK);
    }
}
