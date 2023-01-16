package net.fabricmc.renew_auto_plus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
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

        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.CLOCK_BLOCK_ENTITY, ClockBlockEntityRender::new);
        BlockEntityRendererRegistry.register(RenewAutoPlusInitialize.PUMP_BLOCK_ENTITY, PumpBlockEntityRender::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), RenewAutoPlusInitialize.LETTUCE_BLOCK);
    }
}
