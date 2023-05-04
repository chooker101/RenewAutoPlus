package net.fabricmc.renew_auto_plus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PumpScreen extends HandledScreen<PumpScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/pump.png");
 
    public PumpScreen(PumpScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
 
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int k;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (((PumpScreenHandler)this.handler).isBurning()) {
            k = ((PumpScreenHandler)this.handler).getFuelProgress();
            this.drawTexture(matrices, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        this.drawTexture(matrices, i + 83, j + 22, 0, 166, 18, 40);
        k = ((PumpScreenHandler)this.handler).getPumpingProgress();
        int liquidType = ((PumpScreenHandler)this.handler).getLiquidType(); 
        if(liquidType == PumpBlockEntity.WATER_TYPE) {
            this.drawTexture(matrices, i + 83, j + 22 + 40 - k, 18, 166 + 40 - k, 18, k + 1);
        }
        else if(liquidType == PumpBlockEntity.LAVA_TYPE) {
            this.drawTexture(matrices, i + 83, j + 22 + 40 - k, 36, 166 + 40 - k, 18, k + 1);
        }
    }
 
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
 
    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
