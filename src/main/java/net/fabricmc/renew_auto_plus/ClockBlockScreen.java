package net.fabricmc.renew_auto_plus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ClockBlockScreen extends HandledScreen<ClockBlockScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/clock_block_screen.png");
 
    public ClockBlockScreen(ClockBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.x;
        int j = this.y;
        int k = 25;
        for(int step = 0; step < 4; step++) {
            k = 25 + 36 * step;
            int u = (int)mouseX - (i + k);
            int v = (int)mouseY - (j + 50);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                this.client.interactionManager.clickButton(((ClockBlockScreenHandler)this.handler).syncId, step);
                return true;
            }
            v = (int)mouseY - (j + 20);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                this.client.interactionManager.clickButton(((ClockBlockScreenHandler)this.handler).syncId, 4 + step);
                return true;
            }
        }
        int u = (int)mouseX - (i + 16);
        int v = (int)mouseY - (j + 6);
        if(u >= 0 && v >= 0 && u < 20 && v < 10) {
            this.client.interactionManager.clickButton(((ClockBlockScreenHandler)this.handler).syncId, 8);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        
        int k = 25;
        for(int step = 0; step < 4; step++) {
            k = 25 + 36 * step;
            int u = mouseX - (i + k);
            int v = mouseY - (j + 50);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                this.drawTexture(matrices, i + k, j + 50, 1, 178, 18, 12);
            }
            else {
                this.drawTexture(matrices, i + k, j + 50, 1, 166, 18, 12);
            }
            v = mouseY - (j + 20);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                this.drawTexture(matrices, i + k, j + 20, 20, 178, 18, 12);
            }
            else {
                this.drawTexture(matrices, i + k, j + 20, 20, 166, 18, 12);
            }
        }

        if(((ClockBlockScreenHandler)this.handler).getMode() == 0) {
            this.drawTexture(matrices, i + 16, j + 6, 39, 166, 10, 10);
        }
        else {
            this.drawTexture(matrices, i + 30, j + 6, 39, 166, 10, 10);
        }
        
        //Draw numbers
        String string = intToString(((ClockBlockScreenHandler)this.handler).getDispHours());
        this.textRenderer.drawWithShadow(matrices, string, (float)(i + 28), (float)(j + 37), 8453920);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispMinutes());
        this.textRenderer.drawWithShadow(matrices, string, (float)(i + 64), (float)(j + 37), 8453920);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispSeconds());
        this.textRenderer.drawWithShadow(matrices, string, (float)(i + 100), (float)(j + 37), 8453920);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispTwentieths());
        this.textRenderer.drawWithShadow(matrices, string, (float)(i + 136), (float)(j + 37), 8453920);

        //Draw lables
        this.textRenderer.draw(matrices, "h", (float)(i + 45), (float)(j + 37), 0x404040);
        this.textRenderer.draw(matrices, "m", (float)(i + 81), (float)(j + 37), 0x404040);
        this.textRenderer.draw(matrices, "s", (float)(i + 117), (float)(j + 37), 0x404040);
        this.textRenderer.draw(matrices, "1", (float)(i + 156), (float)(j + 32), 0x404040);
        this.textRenderer.draw(matrices, "-", (float)(i + 156), (float)(j + 37), 0x404040);
        this.textRenderer.draw(matrices, "20", (float)(i + 153), (float)(j + 42), 0x404040);
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
