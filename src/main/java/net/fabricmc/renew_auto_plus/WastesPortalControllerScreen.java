package net.fabricmc.renew_auto_plus;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WastesPortalControllerScreen extends HandledScreen<WastesPortalControllerScreenHandler> {
    private static final Identifier PORTAL_TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/wastes_portal_screen.png");
    private static final Identifier INVENTORY_TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/wastes_portal_inventory.png");
    protected boolean isPlayerInventoryScreen = false;
    protected boolean wasFullLastFrame = false;
    protected int lastCharge = 0;
 
    public WastesPortalControllerScreen(WastesPortalControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(((WastesPortalControllerScreenHandler)this.handler).isRuined()) {
            int u = (int)mouseX - (this.x + 151);
            int v = (int)mouseY - (this.y + 8);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                this.client.interactionManager.clickButton(((WastesPortalControllerScreenHandler)this.handler).syncId, 0);
                if(isPlayerInventoryScreen) {
                    ((WastesPortalControllerScreenHandler)this.handler).slots.clear();
                    ((WastesPortalControllerScreenHandler)this.handler).addPortalSlots();
                    super.mouseClicked(0, 0, 0); //Somehow this updates the client window ¯\_(ツ)_/¯
                    this.y = this.y + 40;
                    this.backgroundHeight = 166;
                }
                else {
                    ((WastesPortalControllerScreenHandler)this.handler).slots.clear();
                    ((WastesPortalControllerScreenHandler)this.handler).addInventorySlots();
                    super.mouseClicked(0, 0, 0); //Somehow this updates the client window ¯\_(ツ)_/¯
                    this.y = this.y - 40;
                    this.backgroundHeight = 246;
                }
                isPlayerInventoryScreen = !isPlayerInventoryScreen;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }
 
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(isPlayerInventoryScreen) {
            RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
        }
        else {
            RenderSystem.setShaderTexture(0, PORTAL_TEXTURE);
        }
        int i = this.x;
        int j = this.y;
        if(isPlayerInventoryScreen) {
            this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        } else {
            this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }
        if(((WastesPortalControllerScreenHandler)this.handler).isRuined()) {
            int u = mouseX - (i + 151);
            int v = mouseY - (j + 8);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                this.drawTexture(matrices, i + 151, j + 8, 194, 0, 18, 18);
            }
            else {
                this.drawTexture(matrices, i + 151, j + 8, 176, 0, 18, 18);
            }
            if(!isPlayerInventoryScreen){
                renderRunes(matrices, delta, mouseX, mouseY);
            }
            if(isPlayerInventoryScreen) {
                this.itemRenderer.renderInGui(RenewAutoPlusInitialize.WASTES_PORTAL_CONTROLLER.asItem().getDefaultStack(), i + 152, j + 9);
            }
            else {
                this.itemRenderer.renderInGui(Blocks.CHEST.asItem().getDefaultStack(), i + 152, j + 9);
            }
        }
    }
 
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void renderRunes(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int chargeLevel = ((WastesPortalControllerScreenHandler)this.handler).getCharge();
        int diamondProgress = ((WastesPortalControllerScreenHandler)this.handler).getSlot(82).getStack().getCount() % 5;
        if(diamondProgress == 0 && !((WastesPortalControllerScreenHandler)this.handler).getSlot(82).getStack().isEmpty()) {
            wasFullLastFrame = true;
        } else if(lastCharge != chargeLevel) {
            wasFullLastFrame = false;
        }
        lastCharge = chargeLevel;
        int i = this.x;
        int j = this.y;
        if(chargeLevel == 0 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 70, j + 60, 0, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 0 || wasFullLastFrame) {
            this.drawTexture(matrices, i + 70, j + 60, 0, 166, 5, 7);
        }
        if(chargeLevel == 1 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 54, j + 52, 8, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 1 || (wasFullLastFrame && chargeLevel > 0)) {
            this.drawTexture(matrices, i + 54, j + 52, 8, 166, 5, 7);
        }
        if(chargeLevel == 2 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 54, j + 36, 14, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 2 || (wasFullLastFrame && chargeLevel > 1)) {
            this.drawTexture(matrices, i + 54, j + 36, 14, 166, 5, 7);
        }
        if(chargeLevel == 3 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 54, j + 20, 24, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 3 || (wasFullLastFrame && chargeLevel > 2)) {
            this.drawTexture(matrices, i + 54, j + 20, 24, 166, 5, 7);
        }
        if(chargeLevel == 4 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 70, j + 12, 32, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 4 || (wasFullLastFrame && chargeLevel > 3)) {
            this.drawTexture(matrices, i + 70, j + 12, 32, 166, 5, 7);
        }
        if(chargeLevel == 5 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 85, j + 12, 40, 166, diamondProgress + 1, 7);
        }
        else if(chargeLevel > 5 || (wasFullLastFrame && chargeLevel > 4)) {
            this.drawTexture(matrices, i + 85, j + 12, 40, 166, 6, 7);
        }
        if(chargeLevel == 6 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 101, j + 12, 47, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 6 || (wasFullLastFrame && chargeLevel > 5)) {
            this.drawTexture(matrices, i + 101, j + 12, 47, 166, 5, 7);
        }
        if(chargeLevel == 7 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 117, j + 20, 56, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 7 || (wasFullLastFrame && chargeLevel > 6)) {
            this.drawTexture(matrices, i + 117, j + 20, 56, 166, 5, 7);
        }
        if(chargeLevel == 8 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 117, j + 36, 64, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 8 || (wasFullLastFrame && chargeLevel > 7)) {
            this.drawTexture(matrices, i + 117, j + 36, 64, 166, 5, 7);
        }
        if(chargeLevel == 9 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 117, j + 52, 72, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 9 || (wasFullLastFrame && chargeLevel > 8)) {
            this.drawTexture(matrices, i + 117, j + 52, 72, 166, 5, 7);
        }
        if(chargeLevel == 10 && !wasFullLastFrame) {
            this.drawTexture(matrices, i + 102, j + 60, 79, 166, diamondProgress, 7);
        }
        else if(chargeLevel > 10 || (wasFullLastFrame && chargeLevel > 9)) {
            this.drawTexture(matrices, i + 102, j + 60, 79, 166, 5, 7);
        }
    }
}
