package net.fabricmc.renew_auto_plus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CapacitorScreen extends HandledScreen<CapacitorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/capacitor_screen.png");
 
    public CapacitorScreen(CapacitorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int u = (int)mouseX - (this.x + 97);
        int v = (int)mouseY - (this.y + 50);
        if(u >= 0 && v >= 0 && u < 18 && v < 12) {
            this.client.interactionManager.clickButton(((CapacitorScreenHandler)this.handler).syncId, 0);
            return true;
        }
        v = (int)mouseY - (this.y + 20);
        if(u >= 0 && v >= 0 && u < 18 && v < 12) {
            this.client.interactionManager.clickButton(((CapacitorScreenHandler)this.handler).syncId, 1);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int i = this.x;
        int j = this.y;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        
        int u = mouseX - (i + 97);
        int v = mouseY - (j + 50);
        if(u >= 0 && v >= 0 && u < 18 && v < 12) {
            context.drawTexture(TEXTURE, i + 97, j + 50, 1, 178, 18, 12);
        }
        else {
            context.drawTexture(TEXTURE, i + 97, j + 50, 1, 166, 18, 12);
        }
        v = mouseY - (j + 20);
        if(u >= 0 && v >= 0 && u < 18 && v < 12) {
            context.drawTexture(TEXTURE, i + 97, j + 20, 20, 178, 18, 12);
        }
        else {
            context.drawTexture(TEXTURE, i + 97, j + 20, 20, 166, 18, 12);
        }
        context.drawTexture(TEXTURE, i + 62, j + 20, 39, 166, 18, 40);
        int k = ((CapacitorScreenHandler)this.handler).getChargeProgress();
        context.drawTexture(TEXTURE, i + 62, j + 20 + 40 - k, 57, 166 + 40 - k, 18, k + 1);
        
        String string = intToString(((CapacitorScreenHandler)this.handler).getCapacitance());
        context.drawText(textRenderer, string, i + 100, j + 37, 8453920, true);
    }
 
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
 
    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    public String intToString(int number) {
        if(number < 10) {
            return "0" + number;
        }
        else if (number > 99) {
            return "99";
        }
        else {
            return "" + number;
        }
    }
}
