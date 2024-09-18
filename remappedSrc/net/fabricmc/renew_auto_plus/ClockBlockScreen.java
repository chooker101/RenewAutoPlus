package net.fabricmc.renew_auto_plus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        
        int k = 25;
        for(int step = 0; step < 4; step++) {
            k = 25 + 36 * step;
            int u = mouseX - (i + k);
            int v = mouseY - (j + 50);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                context.drawTexture(TEXTURE, i + k, j + 50, 1, 178, 18, 12);
            }
            else {
                context.drawTexture(TEXTURE, i + k, j + 50, 1, 166, 18, 12);
            }
            v = mouseY - (j + 20);
            if(u >= 0 && v >= 0 && u < 18 && v < 12) {
                context.drawTexture(TEXTURE, i + k, j + 20, 20, 178, 18, 12);
            }
            else {
                context.drawTexture(TEXTURE, i + k, j + 20, 20, 166, 18, 12);
            }
        }

        if(((ClockBlockScreenHandler)this.handler).getMode() == 0) {
            context.drawTexture(TEXTURE, i + 16, j + 6, 39, 166, 10, 10);
        }
        else {
            context.drawTexture(TEXTURE, i + 30, j + 6, 39, 166, 10, 10);
        }
        
        //Draw numbers
        String string = intToString(((ClockBlockScreenHandler)this.handler).getDispHours());
        context.drawText(textRenderer, string, i + 28, j + 37, 8453920, true);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispMinutes());
        context.drawText(textRenderer, string, i + 64, j + 37, 8453920, true);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispSeconds());
        context.drawText(textRenderer, string, i + 100, j + 37, 8453920, true);
        string = intToString(((ClockBlockScreenHandler)this.handler).getDispTwentieths());
        context.drawText(textRenderer, string, i + 136, j + 37, 8453920, true);

        //Draw lables
        context.drawText(textRenderer, "h", i + 45, j + 37, 0x404040, false);
        context.drawText(textRenderer, "m", i + 81, j + 37, 0x404040, false);
        context.drawText(textRenderer, "s", i + 117, j + 37, 0x404040, false);
        context.drawText(textRenderer, "1", i + 156, j + 32, 0x404040, false);
        context.drawText(textRenderer, "-", i + 156, j + 37, 0x404040, false);
        context.drawText(textRenderer, "20", i + 153, j + 42, 0x404040, false);
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
