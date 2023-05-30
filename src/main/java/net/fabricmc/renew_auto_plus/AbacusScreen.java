package net.fabricmc.renew_auto_plus;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AbacusScreen extends HandledScreen<AbacusScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final String TAB_TEXTURE_PREFIX = "textures/gui/container/abacus/";
    private static final int COMPANY_SETTINGS = 0;
    private static final int OWNER_SETTINGS = 1;
    private static final int CRATE_SETTINGS = 2;
    private static final int AUTOTRADE_SETTINGS = 3;
    private static final int settingsConfirmX = 72;
    private static final int settingsConfirmY = 141;
    private static final int settingsDeleteX = 12;
    private static final int settingsDeleteY = 141;
    private static final int tradeConfirmX = 151;
    private static final int tradeConfirmY = 78;
    private int selectedTab = 0;
    private int selectedSettingsPage = 0;
    private int selectedTradeIndex;
    private int tradeIndexStartOffset = 0;
    private StallTrade loadedStallTrade = null;

    private final WidgetButtonPage[] offers = new WidgetButtonPage[6];
    private final WidgetButtonPage[] selectedSettingsList = new WidgetButtonPage[7];

    private TextFieldWidget settingsInputBox;
    private TextFieldWidget tradeAmountBox;
 
    public AbacusScreen(AbacusScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        ((AbacusScreenHandler)this.handler).slots.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double d = mouseX - (double)this.x;
        double e = mouseY - (double)this.y;
        AbacusScreenTab currentScreenTab = AbacusScreenTab.TABS[selectedTab];
        for (AbacusScreenTab tab : AbacusScreenTab.TABS) {
            if (this.isClickInTab(tab, d, e)){
                this.selectTab(tab);
                return true;
            }
        }
        if(currentScreenTab != AbacusScreenTab.INVENTORY){
            int u = 0;
            int v = 0;
            if(currentScreenTab == AbacusScreenTab.SETTINGS){
                u = (int)mouseX - (this.x + settingsConfirmX);
                v = (int)mouseY - (this.y + settingsConfirmY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    StringToAbacusC2SPacket packet = new StringToAbacusC2SPacket(handler.getBlockPos(), settingsInputBox.getText());
                    packet.write(byteBuf);
                    this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.AbacusStringPacketID, byteBuf));
                    this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 0);
                    return true;
                }
            }
            if(currentScreenTab == AbacusScreenTab.BUY || currentScreenTab == AbacusScreenTab.SELL){
                u = (int)mouseX - (this.x + tradeConfirmX);
                v = (int)mouseY - (this.y + tradeConfirmY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    if(loadedStallTrade == null) {
                        return super.mouseClicked(mouseX, mouseY, button);
                    }

                    int tradeAmount = 1;
                    try{
                        tradeAmount = Integer.parseInt(tradeAmountBox.getText());
                    }
                    catch(Exception exception) {
                        tradeAmount = 1;
                    }
                    if(currentScreenTab == AbacusScreenTab.BUY) {
                        if (!handler.canBuyWith(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange()) || !isLoadedTradeActive()) {
                            return super.mouseClicked(mouseX, mouseY, button);
                        }
                        if(!handler.canBuyWith(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount)) {
                            tradeAmount = handler.getMaxCanBuy(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange());
                        }
                    }
                    else {
                        if (!loadedStallTrade.isSellable()) {
                            return super.mouseClicked(mouseX, mouseY, button);
                        }
                        int itemOwned = loadedStallTrade.getItemAmount() >= 999 ? 999 : loadedStallTrade.getItemAmount();
                        if(tradeAmount > itemOwned) {
                            tradeAmount = itemOwned;
                        }
                        loadedStallTrade.setItemAmount(loadedStallTrade.getItemAmount() - tradeAmount);
                        if(loadedStallTrade.getItemAmount() - tradeAmount <= 0) {
                            loadedStallTrade.setSellable(false);
                        }
                    }
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    TransactStallTradeC2SPacket packet = new TransactStallTradeC2SPacket(handler.getBlockPos(), currentScreenTab == AbacusScreenTab.BUY, loadedStallTrade, tradeAmount);
                    packet.write(byteBuf);
                    this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.AbacusTransactPacketID, byteBuf));
                    this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 1);
                    return true;
                }
            }
            //this.companyNameBox.setCursorToEnd();
            //this.companyNameBox.setSelectionEnd(0);
            //this.tradeAmountBox.setCursorToEnd();
            //this.tradeAmountBox.setSelectionEnd(0);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        AbacusScreenTab itemGroup = AbacusScreenTab.TABS[selectedTab];
        
        RenderSystem.disableBlend();
        Text translatedText = new TranslatableText(itemGroup.getId());
        titleX = (backgroundWidth - textRenderer.getWidth(translatedText)) / 2;
        this.textRenderer.draw(matrices, translatedText, titleX, 6.0f, 0x404040);
    }
 
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        AbacusScreenTab selectedScreenTab = AbacusScreenTab.TABS[selectedTab];
        for (AbacusScreenTab tab : AbacusScreenTab.TABS) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            if (tab.getIndex() == selectedTab) continue;
            this.renderTabIcon(matrices, tab);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if(selectedScreenTab == AbacusScreenTab.INVENTORY) {
            RenderSystem.setShaderTexture(0, new Identifier(selectedScreenTab.getBackgroundTexture()));
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, 3 * 18 + 17);
            this.drawTexture(matrices, i, j + 3 * 18 + 17, 0, 127, this.backgroundWidth, 96);
        }
        else {
            RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + selectedScreenTab.getBackgroundTexture()));
            this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }
        
        int u = 0;
        int v = 0;
        if(selectedScreenTab == AbacusScreenTab.SETTINGS) {
            //renderSettingPageButtons

            if(selectedSettingsPage != AUTOTRADE_SETTINGS){
                u = mouseX - (this.x + settingsConfirmX);
                v = mouseY - (this.y + settingsConfirmY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    this.drawTexture(matrices, this.x + settingsConfirmX, this.y + settingsConfirmY, 79, 184, 18, 18);
                }
                else {
                    this.drawTexture(matrices, this.x + settingsConfirmX, this.y + settingsConfirmY, 61, 184, 18, 18);
                }
            }
            else {
                this.drawTexture(matrices, this.x + settingsConfirmX, this.y + settingsConfirmY, 0, 166, 18, 18);
            }
            u = mouseX - (this.x + settingsDeleteX);
            v = mouseY - (this.y + settingsDeleteY);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                this.drawTexture(matrices, this.x + settingsDeleteX, this.y + settingsDeleteY, 79, 202, 18, 18);
            }
            else {
                this.drawTexture(matrices, this.x + settingsDeleteX, this.y + settingsDeleteY, 61, 202, 18, 18);
            }

            //renderSettingsList
            //renderCurrentSettingInfo
        }
        else if(selectedScreenTab == AbacusScreenTab.BUY || selectedScreenTab == AbacusScreenTab.SELL){
            u = mouseX - (this.x + tradeConfirmX);
            v = mouseY - (this.y + tradeConfirmY);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                if(loadedStallTrade != null) {
                    if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
                        if (!handler.canBuyWith(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange()) || !isLoadedTradeActive()) {
                            this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                        } else {
                            this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 79, 166, 18, 18);
                        }
                    }
                    else {
                        if (!loadedStallTrade.isSellable()) {
                            this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                        } else {
                            this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 79, 166, 18, 18);
                        }
                    }
                }
                else {
                    this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                }
            }
            else {
                this.drawTexture(matrices, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
            }
            renderStallTrades(matrices, selectedScreenTab, mouseX, mouseY);
            renderLoadedTrade(matrices, selectedScreenTab);
        }
        this.settingsInputBox.render(matrices, mouseX, mouseY, delta);
        this.tradeAmountBox.render(matrices, mouseX, mouseY, delta);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.renderTabIcon(matrices, selectedScreenTab);
    }
 
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.settingsInputBox != null) {
            this.settingsInputBox.tick();
        }
        if (this.tradeAmountBox != null) {
            this.tradeAmountBox.tick();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.settingsInputBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.settingsInputBox.isFocused() && this.settingsInputBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        if (this.tradeAmountBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.tradeAmountBox.isFocused() && this.tradeAmountBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    protected boolean isLoadedTradeActive() {
        if(loadedStallTrade == null) {
            return false;
        }
        return loadedStallTrade.isActive();
    }

    protected boolean isClickInTab(AbacusScreenTab tab, double mouseX, double mouseY) {
        int i = tab.getColumn();
        int j = 28 * i;
        int k = -32;
        if (i > 0) {
            j += i;
        }
        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
    }

    protected void renderTabIcon(MatrixStack matrices, AbacusScreenTab tab) {
        boolean bl = tab.getIndex() == selectedTab;
        int i = tab.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.x + 28 * i;
        int m = this.y - 28;
        if (bl) {
            k += 32;
        }
        if (i > 0) {
            l += i;
        }
        this.drawTexture(matrices, l, m, j, k, 28, 32);
        //this.itemRenderer.zOffset = 100.0f;
        //int n2 = 1;
        //ItemStack itemStack = tab.getIcon();
        //this.itemRenderer.renderInGuiWithOverrides(itemStack, l += 6, m += 8 + n2);
        //this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
        //this.itemRenderer.zOffset = 0.0f;
    }

    protected void selectTab(AbacusScreenTab tab) {
        int oldTab = selectedTab;

        if (tab == AbacusScreenTab.INVENTORY) {
            ((AbacusScreenHandler)this.handler).addSlots();
            super.mouseClicked(0, 0, 0); //Somehow this updates the client window ¯\_(ツ)_/¯
        } else if (oldTab == AbacusScreenTab.INVENTORY.getIndex()) {
            ((AbacusScreenHandler)this.handler).slots.clear();
        }
        if (tab == AbacusScreenTab.BUY || tab == AbacusScreenTab.SELL) {
            if(this.tradeAmountBox != null) {
                this.tradeAmountBox.setVisible(true);
                this.tradeAmountBox.setFocusUnlocked(false);
                this.tradeAmountBox.setTextFieldFocused(true);
            }
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = true;
            }
        }
        else if(oldTab == AbacusScreenTab.BUY.getIndex() || oldTab == AbacusScreenTab.SELL.getIndex()) {
            if(this.tradeAmountBox != null) {
                this.tradeAmountBox.setVisible(false);
                this.tradeAmountBox.setFocusUnlocked(true);
                this.tradeAmountBox.setTextFieldFocused(false);
            }
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = false;
            }
        }
        if (this.settingsInputBox != null) {
            if (tab == AbacusScreenTab.SETTINGS) {
                this.settingsInputBox.setVisible(true);
                this.settingsInputBox.setFocusUnlocked(false);
                this.settingsInputBox.setTextFieldFocused(true);
            } else {
                this.settingsInputBox.setVisible(false);
                this.settingsInputBox.setFocusUnlocked(true);
                this.settingsInputBox.setTextFieldFocused(false);
            }
        }
        selectedTab = tab.getIndex();
    }

    private boolean canTradeScroll(int listSize) {
        return listSize > 6;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = ((AbacusScreenHandler)this.handler).getStallTradeList().size();
        if (this.canTradeScroll(i)) {
            int j = i - 6;
            this.tradeIndexStartOffset = (int)((double)this.tradeIndexStartOffset - amount);
            this.tradeIndexStartOffset = MathHelper.clamp(this.tradeIndexStartOffset, 0, j);
        }
        return true;
    }

    private void renderTradeScrollbar(MatrixStack matrices, int x, int y, StallTradeList tradeOffers) {
        int i = tradeOffers.size() + 1 - 6;
        if (i > 1) {
            int j = 119 - (9 + (i - 1) * 119 / i);
            int k = 1 + j / i + 119 / i;
            int m = Math.min(111, this.tradeIndexStartOffset * k);
            if (this.tradeIndexStartOffset == i - 1) {
                m = 111;
            }
            AbacusScreen.drawTexture(matrices, x + 70, y + 29 + m, this.getZOffset(), 0.0f, 226.0f, 6, 9, 256, 256);
        } else {
            AbacusScreen.drawTexture(matrices, x + 70, y + 29, this.getZOffset(), 6.0f, 226.0f, 6, 9, 256, 256);
        }
    }

    protected void renderStallTrades(MatrixStack matrices, AbacusScreenTab tab, double mouseX, double mouseY) {
        StallTradeList stallTradeList = ((AbacusScreenHandler)this.handler).getStallTradeList();
        if (!stallTradeList.isEmpty()) {
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = j + 29;
            int l = i + 10;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            
            RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + tab.getBackgroundTexture()));
            RenderSystem.enableBlend();
            this.renderTradeScrollbar(matrices, i, j, stallTradeList);
            int m = 0;
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                //if (widgetButtonPage.isHovered()) {
                //    widgetButtonPage.renderTooltip(matrices, (int)mouseX, (int)mouseY);
                //}
                widgetButtonPage.visible = widgetButtonPage.index < stallTradeList.size();
                if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
                    widgetButtonPage.active = stallTradeList.get(widgetButtonPage.index).isActive();
                }
                else {
                    widgetButtonPage.active = true;
                }
            }
            for (StallTrade stallTrade : stallTradeList) { //Can optimize maybe with sublist
                if (this.canTradeScroll(stallTradeList.size()) && (m < this.tradeIndexStartOffset || m >= 6 + this.tradeIndexStartOffset)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = stallTrade.getTradedItem();
                ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
                this.itemRenderer.zOffset = 100.0f;
                //Render first item
                int n = k + 2;
                if(tab == AbacusScreenTab.BUY) {
                    this.itemRenderer.renderInGui(emeraldStack, l, n);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l, n);
                }
                else {
                    this.itemRenderer.renderInGui(itemStack, l, n);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, n);
                }
                //Render arrow
                renderArrow(matrices, stallTrade, i, n);
                if(tab == AbacusScreenTab.BUY) {
                    this.itemRenderer.renderInGui(itemStack, l + 40, n);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l + 40, n);
                }
                else
                {
                    this.itemRenderer.renderInGui(emeraldStack, l + 40, n);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l + 40, n);
                }
                this.itemRenderer.zOffset = 0.0f;
                k += 20;
                ++m;
            }
        }
    }

    private void renderArrow(MatrixStack matrices, StallTrade tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture()));
        if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
            if (!handler.canBuyWith(tradeOffer.getEmeraldAmount(), tradeOffer.getEmeraldChange())) {
                AbacusScreen.drawTexture(matrices, x + 33, y + 3, this.getZOffset() + 1, 22.0f, 226.0f, 10, 9, 256, 256);
            } else {
                AbacusScreen.drawTexture(matrices, x + 33, y + 3, this.getZOffset() + 1, 12.0f, 226.0f, 10, 9, 256, 256);
            }
        }
        else {
            if (!tradeOffer.isSellable()) {
                AbacusScreen.drawTexture(matrices, x + 33, y + 3, this.getZOffset() + 1, 22.0f, 226.0f, 10, 9, 256, 256);
            } else {
                AbacusScreen.drawTexture(matrices, x + 33, y + 3, this.getZOffset() + 1, 12.0f, 226.0f, 10, 9, 256, 256);
            }
        }
    }

    protected void renderLoadedTrade(MatrixStack matrices, AbacusScreenTab tab) {
        if(loadedStallTrade == null) {
            return;
        }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int l = i + 83;
        int k = j + 79;
        int m = 0;
        int n = 0;

        int tradeAmount = 1;
        try{
            tradeAmount = Integer.parseInt(tradeAmountBox.getText());
        }
        catch(Exception exception) {
            tradeAmount = 1;
        }
        int itemOwned = loadedStallTrade.getItemAmount() >= 999 ? 999 : loadedStallTrade.getItemAmount();
        String emeraldOwnedString = handler.getEmeraldString();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        ItemStack itemStack = loadedStallTrade.getTradedItem();
        ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
        this.itemRenderer.zOffset = 100.0f;
        if(tab == AbacusScreenTab.BUY) {
            if(!handler.canBuyWith(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount)) {
                tradeAmount = handler.getMaxCanBuy(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange());
            }
            String emeraldString = handler.getEmeraldString(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount);
            this.itemRenderer.renderInGui(emeraldStack, l, k);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l, k);
            this.itemRenderer.renderInGui(itemStack, l + 48, k);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l + 40, k);
            //top
            m = (handler.getEmeraldFromChangeAmount(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount) >= 100 ? 75 : (handler.getEmeraldFromChangeAmount(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount) >= 10 ? 78 : 81));
            n = (tradeAmount >= 100 ? 131 : (tradeAmount >= 10 ? 134 : 137));
            this.textRenderer.draw(matrices, emeraldString, i + m, j + 68, 0x404040);
            this.textRenderer.draw(matrices, Integer.toString(tradeAmount), i + n, j + 68, 0x404040);
            //bottom
            m = (handler.getEmeraldAmount() >= 100 ? 75 : (handler.getEmeraldAmount() >= 10 ? 78 : 81));
            n = (itemOwned >= 100 ? 131 : (itemOwned >= 10 ? 134 : 137));
            this.textRenderer.draw(matrices, emeraldOwnedString, i + m, j + 98, 0x404040);
            this.textRenderer.draw(matrices, Integer.toString(itemOwned), i + n, j + 98, 0x404040);
        }
        else {
            if(tradeAmount > itemOwned) {
                tradeAmount = itemOwned;
            }
            String emeraldString = handler.getEmeraldString(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount);
            this.itemRenderer.renderInGui(itemStack, l, k);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, k);
            this.itemRenderer.renderInGui(emeraldStack, l + 48, k);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l + 40, k);
            //top
            m = (handler.getEmeraldFromChangeAmount(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount) >= 100 ? 124 : (handler.getEmeraldFromChangeAmount(loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), tradeAmount) >= 10 ? 127 : 130));
            n = (tradeAmount >= 100 ? 82 : (tradeAmount >= 10 ? 85 : 88));
            this.textRenderer.draw(matrices, emeraldString, i + m, j + 68, 0x404040);
            this.textRenderer.draw(matrices, Integer.toString(tradeAmount), i + n, j + 68, 0x404040);
            //bottom
            m = (handler.getEmeraldAmount() >= 100 ? 124 : (handler.getEmeraldAmount() >= 10 ? 127 : 130));
            n = (itemOwned >= 100 ? 82 : (itemOwned >= 10 ? 85 : 88));
            this.textRenderer.draw(matrices, emeraldOwnedString, i + m, j + 98, 0x404040);
            this.textRenderer.draw(matrices, Integer.toString(itemOwned), i + n, j + 98, 0x404040);
        }
        this.itemRenderer.zOffset = 0.0f;
    }

    protected void renderSettingsList(MatrixStack matrices, AbacusScreenTab tab, double mouseX, double mouseY) {
        //if selectedList is empty return
        int listSize = 0; //fill with selected list
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 29;
        int l = i + 10;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        
        RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + tab.getBackgroundTexture()));
        RenderSystem.enableBlend();
        //this.renderTradeScrollbar(matrices, i, j, stallTradeList);
        int m = 0;
        for (WidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
            widgetButtonPage.visible = widgetButtonPage.index < listSize;
            widgetButtonPage.active = true;
        }
            //Render list based on selected

            //for (StallTrade stallTrade : stallTradeList) { //Can optimize maybe with sublist
            //    if (this.canTradeScroll(stallTradeList.size()) && (m < this.tradeIndexStartOffset || m >= 6 + this.tradeIndexStartOffset)) {
            //        ++m;
            //        continue;
            //    }
            //    ItemStack itemStack = stallTrade.getTradedItem();
            //    ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
            //    this.itemRenderer.zOffset = 100.0f;
            //    //Render first item
            //    int n = k + 2;
            //    if(tab == AbacusScreenTab.BUY) {
            //        this.itemRenderer.renderInGui(emeraldStack, l, n);
            //        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l, n);
            //    }
            //    else {
            //        this.itemRenderer.renderInGui(itemStack, l, n);
            //        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, n);
            //    }
            //    //Render arrow
            //    renderArrow(matrices, stallTrade, i, n);
            //    if(tab == AbacusScreenTab.BUY) {
            //        this.itemRenderer.renderInGui(itemStack, l + 40, n);
            //        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l + 40, n);
            //    }
            //    else
            //    {
            //        this.itemRenderer.renderInGui(emeraldStack, l + 40, n);
            //        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l + 40, n);
            //    }
            //    this.itemRenderer.zOffset = 0.0f;
            //    k += 20;
            //    ++m;
            //}
        
    }
 
    @Override
    protected void init() {
        super.init();
        this.client.keyboard.setRepeatEvents(true);
        this.settingsInputBox = new TextFieldWidget(this.textRenderer, this.x + 35, this.y + 37, 80, this.textRenderer.fontHeight, new TranslatableText("itemGroup.search"));
        this.settingsInputBox.setMaxLength(20);
        this.settingsInputBox.setDrawsBackground(false);
        this.settingsInputBox.setVisible(false);
        this.settingsInputBox.setEditableColor(0xFFFFFF);
        this.settingsInputBox.setText(handler.getCurrentCompanyName());

        this.tradeAmountBox = new TextFieldWidget(this.textRenderer, this.x + 106, this.y + 67, 18, this.textRenderer.fontHeight, new TranslatableText("itemGroup.search"));
        this.tradeAmountBox.setMaxLength(3);
        this.tradeAmountBox.setDrawsBackground(false);
        this.tradeAmountBox.setVisible(true);
        this.tradeAmountBox.setEditableColor(0xFFFFFF);
        this.tradeAmountBox.setText("1");

        this.addSelectableChild(this.settingsInputBox);
        this.addSelectableChild(this.tradeAmountBox);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 29;
        for (int l = 0; l < 6; ++l) {
            this.offers[l] = this.addDrawableChild(new WidgetButtonPage(i + 8, k, l, button -> {
                if (button instanceof WidgetButtonPage) {
                    this.selectedTradeIndex = ((WidgetButtonPage)button).getIndex() + this.tradeIndexStartOffset;
                    this.loadedStallTrade = ((AbacusScreenHandler)this.handler).getStallTradeList().get(this.selectedTradeIndex);
                    this.tradeAmountBox.setVisible(true);
                    this.tradeAmountBox.setFocusUnlocked(false);
                    this.tradeAmountBox.setTextFieldFocused(true);
                }
            }));
            k += 20;
        }
        k = j + 18;
        for (int l = 0; l < 7; ++l) {
            this.selectedSettingsList[l] = this.addDrawableChild(new WidgetButtonPage(i + 100, k, l, button -> {
                if (button instanceof WidgetButtonPage) {
                    //Do stuff
                }
            }));
            k += 20;
        }
    }

    class WidgetButtonPage
    extends ButtonWidget {
        final int index;

        public WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, 61, 20, LiteralText.EMPTY, onPress);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            if (this.hovered && ((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().size() > this.index + AbacusScreen.this.tradeIndexStartOffset) {
                if (mouseX < this.x + 20) {
                    if(AbacusScreen.this.selectedTab == AbacusScreenTab.BUY.getIndex()) {
                        AbacusScreen.this.renderTooltip(matrices, Items.EMERALD.getDefaultStack(), mouseX, mouseY);
                    }
                    else {
                        ItemStack itemStack = ((StallTrade)((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().get(this.index + AbacusScreen.this.tradeIndexStartOffset)).getTradedItem();
                        AbacusScreen.this.renderTooltip(matrices, itemStack, mouseX, mouseY);
                    }
                }
                else if (mouseX > this.x + 45) {
                    if(AbacusScreen.this.selectedTab == AbacusScreenTab.BUY.getIndex()) {
                        ItemStack itemStack = ((StallTrade)((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().get(this.index + AbacusScreen.this.tradeIndexStartOffset)).getTradedItem();
                        AbacusScreen.this.renderTooltip(matrices, itemStack, mouseX, mouseY);
                    }
                    else {
                        AbacusScreen.this.renderTooltip(matrices, Items.EMERALD.getDefaultStack(), mouseX, mouseY);
                    }
                }
            }
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + "abacus_screen_trade.png")); //Needs change from hardcode
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, this.x, this.y, 0, 166 + i * 20, this.width, this.height);
            if (this.isHovered()) {
                this.renderTooltip(matrices, mouseX, mouseY);
            }
        }
    }
}
