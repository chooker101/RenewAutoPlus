package net.fabricmc.renew_auto_plus;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CrateScreen extends HandledScreen<CrateScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/crate_screen.png");
    private static final int comfirmButtonX = 133;
    private static final int comfirmButtonY = -21;

    private TextFieldWidget companyNameBox;
 
    public CrateScreen(CrateScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, new LiteralText(text.getString() + " [" + handler.getBlockPos().toShortString() + "]").setStyle(text.getStyle()));
    }

    @Override
    protected void init() {
        super.init();
        backgroundHeight = 243;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = -32;
        playerInventoryTitleY = 110;

        this.client.keyboard.setRepeatEvents(true);
        this.companyNameBox = new TextFieldWidget(this.textRenderer, this.x + 27, this.y - 16, 94, this.textRenderer.fontHeight, new TranslatableText("itemGroup.search"));
        this.companyNameBox.setMaxLength(20);
        this.companyNameBox.setDrawsBackground(false);
        this.companyNameBox.setVisible(true);
        this.companyNameBox.setEditableColor(0xFFFFFF);
        this.companyNameBox.setText(handler.getCurrentCompanyName());
        this.companyNameBox.setFocusUnlocked(false);
        this.companyNameBox.setTextFieldFocused(true);

        this.addSelectableChild(this.companyNameBox);
    }
 
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        int u = mouseX - (this.x + comfirmButtonX);
        int v = mouseY - (this.y + comfirmButtonY);
        if(u >= 0 && v >= 0 && u < 18 && v < 18) {
            this.drawTexture(matrices, this.x + comfirmButtonX, this.y + comfirmButtonY, 176, 0, 18, 18);
        }
        this.companyNameBox.render(matrices, mouseX, mouseY, delta);
    }
 
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
 
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int u = (int)mouseX - (this.x + comfirmButtonX);
        int v = (int)mouseY - (this.y + comfirmButtonY);
        if(u >= 0 && v >= 0 && u < 18 && v < 18) {
            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
            StringToCrateC2SPacket packet = new StringToCrateC2SPacket(handler.getBlockPos(), companyNameBox.getText());
            packet.write(byteBuf);
            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.CrateStringPacketID, byteBuf));
            this.client.interactionManager.clickButton(((CrateScreenHandler)this.handler).syncId, 0);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.companyNameBox != null) {
            this.companyNameBox.tick();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.companyNameBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.companyNameBox.isFocused() && this.companyNameBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
