package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.fabricmc.renew_auto_plus.RenewAutoPlusInitialize;
import net.minecraft.client.MinecraftClient;

@Pseudo
@Mixin(InGameHud.class)
public abstract class InGameHudExtension extends DrawableHelper {
    @Shadow
    private final MinecraftClient client;

    @Shadow
    private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");

    public InGameHudExtension(MinecraftClient client) {
        this.client = client;
    }

    @Shadow
    private void renderOverlay(Identifier texture, float opacity) {}

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    //@Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"))
    protected void renderExtension(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        if(this.client.player.getDataTracker().get(RenewAutoPlusInitialize.IS_ICEBOUND)) {
            this.renderOverlay(POWDER_SNOW_OUTLINE, 1.0f);
        }
    }
}
