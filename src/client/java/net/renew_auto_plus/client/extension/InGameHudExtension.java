package net.renew_auto_plus.client.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.renew_auto_plus.RenewAutoPlusInitialize;
import net.minecraft.client.MinecraftClient;

@Pseudo
@Mixin(InGameHud.class)
public abstract class InGameHudExtension {
    @Shadow
    private final MinecraftClient client;

    @Shadow
    private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");

    public InGameHudExtension(MinecraftClient client) {
        this.client = client;
    }

    @Shadow
    private void renderOverlay(DrawContext context, Identifier texture, float opacity) {}

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    public void renderExtension(DrawContext context, float tickDelta, CallbackInfo info) {
        if(this.client.player.getDataTracker().get(RenewAutoPlusInitialize.IS_ICEBOUND)) {
            this.renderOverlay(context, POWDER_SNOW_OUTLINE, 1.0f);
        }
    }
}
