package net.renew_auto_plus;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CrateScreen extends HandledScreen<CrateScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/crate_screen.png");
    private static final int comfirmButtonX = 133;
    private static final int comfirmButtonY = -21;

    private TextFieldWidget companyNameBox;
 
    public CrateScreen(CrateScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, Text.of(text.getString() + " [" + handler.getBlockPos().toShortString() + "]"));
    }

    @Override
    protected void init() {
        super.init();
        backgroundHeight = 243;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = -32;
        playerInventoryTitleY = 110;

        //this.client.keyboard.setRepeatEvents(true);
        this.companyNameBox = new TextFieldWidget(this.textRenderer, this.x + 27, this.y - 16, 94, this.textRenderer.fontHeight, Text.translatable("itemGroup.search"));
        this.companyNameBox.setMaxLength(20);
        this.companyNameBox.setDrawsBackground(false);
        this.companyNameBox.setVisible(true);
        this.companyNameBox.setEditableColor(0xFFFFFF);
        this.companyNameBox.setText(handler.getCurrentCompanyName());
        this.companyNameBox.setFocusUnlocked(true);
        this.companyNameBox.setFocused(true);

        this.addSelectableChild(this.companyNameBox);
    }
 
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        int u = mouseX - (this.x + comfirmButtonX);
        int v = mouseY - (this.y + comfirmButtonY);
        if(u >= 0 && v >= 0 && u < 18 && v < 18) {
            context.drawTexture(TEXTURE, this.x + comfirmButtonX, this.y + comfirmButtonY, 176, 0, 18, 18);
        }
        this.companyNameBox.render(context, mouseX, mouseY, delta);
    }
 
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
 
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int u = (int)mouseX - (this.x + comfirmButtonX);
        int v = (int)mouseY - (this.y + comfirmButtonY);
        if(u >= 0 && v >= 0 && u < 18 && v < 18) {
            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
            StringToCrateC2SPacket packet = new StringToCrateC2SPacket(handler.getBlockPos(), companyNameBox.getText());
            packet.write(byteBuf);
            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.CRATE_STRING_PACKET_ID, byteBuf));
            this.client.interactionManager.clickButton(((CrateScreenHandler)this.handler).syncId, 0);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //Remove if not needed
    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        //if (this.companyNameBox != null) {
        //    this.companyNameBox.tick();
        //}
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
