package net.renew_auto_plus;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AbacusScreen extends HandledScreen<AbacusScreenHandler> {
    private final Set<TagKey<Item>> searchResultTags = new HashSet<TagKey<Item>>();
    private static final Identifier BASE_TAB_TEXTURE = new Identifier("renew_auto_plus", "textures/gui/container/abacus/tab_icons.png");
    private static final String TAB_TEXTURE_PREFIX = "textures/gui/container/abacus/";
    private static final int COMPANY_SETTINGS = 0;
    private static final int CRATE_SETTINGS = 1;
    private static final int OWNER_SETTINGS = 2;
    private static final int AUTOTRADE_SETTINGS = 3;
    private static final int settingsConfirmX = 72;
    private static final int settingsConfirmY = 141;
    private static final int settingsDeleteX = 12;
    private static final int settingsDeleteY = 141;
    private static final int tradeConfirmX = 151;
    private static final int tradeConfirmY = 131;
    private static final int autoTradeConfirmX = 151;
    private static final int autoTradeConfirmY = 15;
    private static final int tradeSearchX = 8;
    private static final int tradeSearchY = 16;
    private int selectedTab = 0;
    private int selectedSettingsPage = 0;
    private int selectedTradeIndex;
    private int selectedSettingListIndex = -1;
    private int tradeIndexStartOffset = 0;
    private int settingIndexStartOffset = 0;

    private StallTrade loadedStallTrade = null;

    private StallTradeList searchTradeList = new StallTradeList();

    private final TradeWidgetButtonPage[] offers = new TradeWidgetButtonPage[6];
    private final TradeWidgetButtonPage[] selectedSettingsList = new TradeWidgetButtonPage[7];

    private TextFieldWidget settingsInputBox;
    private TextFieldWidget tradeAmountBox;
    private TextFieldWidget tradeSearchBox;
 
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
            if (this.isClickInScreenTab(tab, d, e)){
                this.selectTab(tab);
                return true;
            }
        }
        if(currentScreenTab != AbacusScreenTab.INVENTORY){
            int u = 0;
            int v = 0;
            if(currentScreenTab == AbacusScreenTab.SETTINGS){
                for(int i = 0; i < 4; i++) {
                    if(isClickInSettingsTab(i, mouseX - (double)this.x, mouseY - (double)this.y)) {
                        selectSettingsPage(i);
                        return true;
                    }
                }
                u = (int)mouseX - (this.x + settingsConfirmX);
                v = (int)mouseY - (this.y + settingsConfirmY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    if(selectedSettingsPage == COMPANY_SETTINGS) {
                        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                        StringToAbacusC2SPacket packet = new StringToAbacusC2SPacket(handler.getBlockPos(), settingsInputBox.getText(), StringToAbacusC2SPacket.COMPANY_NAME_TYPE);
                        packet.write(byteBuf);
                        this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_STRING_PACKET_ID, byteBuf));
                    } else if (selectedSettingsPage == OWNER_SETTINGS) {
                        if(selectedSettingListIndex != -1) {
                            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                            StringToAbacusC2SPacket packet = new StringToAbacusC2SPacket(handler.getBlockPos(), settingsInputBox.getText(), StringToAbacusC2SPacket.ADD_OWNER_NAME_TYPE);
                            packet.write(byteBuf);
                            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_STRING_PACKET_ID, byteBuf));
                            handler.removeOwnerName(settingsInputBox.getText());
                            handler.addOwnerName(settingsInputBox.getText());
                            selectedSettingListIndex = -1;
                            this.settingsInputBox.setText("");
                        }
                    }
                    this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 0);
                    return true;
                }
                u = (int)mouseX - (this.x + settingsDeleteX);
                v = (int)mouseY - (this.y + settingsDeleteY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    if(selectedSettingsPage == COMPANY_SETTINGS) {
                        settingsInputBox.setText("");
                    } else if (selectedSettingsPage == CRATE_SETTINGS) {
                        if(selectedSettingListIndex != -1) {
                            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                            RemoveAttachedCrateC2SPacket packet = new RemoveAttachedCrateC2SPacket(handler.getBlockPos(), handler.getAttachedCratesList().get(selectedSettingListIndex - 1));
                            packet.write(byteBuf);
                            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_REMOVE_ATTACHED_CRATE_PACKET_ID, byteBuf));
                            handler.getAttachedCratesList().remove(selectedSettingListIndex - 1);
                            selectedSettingListIndex = -1;
                            this.settingsInputBox.setText("");
                        }
                    } else if (selectedSettingsPage == OWNER_SETTINGS) {
                        if(selectedSettingListIndex != -1) {
                            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                            StringToAbacusC2SPacket packet = new StringToAbacusC2SPacket(handler.getBlockPos(), settingsInputBox.getText(), StringToAbacusC2SPacket.REMOVE_OWNER_NAME_TYPE);
                            packet.write(byteBuf);
                            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_STRING_PACKET_ID, byteBuf));
                            handler.removeOwnerName(settingsInputBox.getText());
                            selectedSettingListIndex = -1;
                            this.settingsInputBox.setText("");
                        }
                    } else if (selectedSettingsPage == AUTOTRADE_SETTINGS) {
                        if(selectedSettingListIndex != -1) {
                            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                            RemoveAutoTradeC2SPacket packet = new RemoveAutoTradeC2SPacket(handler.getBlockPos(), selectedSettingListIndex - 1);
                            packet.write(byteBuf);
                            this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_REMOVE_AUTO_TRADE_PACKET_ID, byteBuf));
                            for(StallTrade trade : handler.getStallTradeList()) {
                                if(trade.getTradedItem().equals(handler.getAutoTradeList().get(selectedSettingListIndex - 1).getTradedItem())) {
                                    trade.setAutoTradeEnabled(false);
                                }
                            }
                            handler.getAutoTradeList().remove(selectedSettingListIndex - 1);
                            selectedSettingListIndex = -1;
                        }
                    }
                    //this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 0);
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

                    boolean autoTradeState = loadedStallTrade.isAutoTradeEnabled(); //For rendering

                    int tradeAmount = 1;
                    try{
                        tradeAmount = Integer.parseInt(tradeAmountBox.getText());
                    }
                    catch(Exception exception) {
                        tradeAmount = 1;
                    }
                    if(currentScreenTab == AbacusScreenTab.BUY) {
                        int marketItemOwned = loadedStallTrade.getMarketItemAmount() >= 999 ? 999 : loadedStallTrade.getMarketItemAmount();
                        if (!isLoadedTradeActive()) {
                            return super.mouseClicked(mouseX, mouseY, button);
                        }
                        if(!handler.canBuyWith(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange(), tradeAmount) || marketItemOwned < tradeAmount) {
                            int maxCanBuy = handler.getMaxCanBuy(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange());
                            tradeAmount = maxCanBuy > marketItemOwned ? marketItemOwned : maxCanBuy;
                        }
                        loadedStallTrade.setMarketItemAmount(loadedStallTrade.getMarketItemAmount() - tradeAmount);
                        loadedStallTrade.setActive(loadedStallTrade.getMarketItemAmount() > 0);
                        loadedStallTrade.setCompanyItemAmount(loadedStallTrade.getCompanyItemAmount() + tradeAmount);
                        loadedStallTrade.setSellable(loadedStallTrade.getCompanyItemAmount() > 0);
                        loadedStallTrade.setAutoTradeEnabled(false); //This is not an autoTrade
                    }
                    else {
                        if (!loadedStallTrade.isSellable()) {
                            return super.mouseClicked(mouseX, mouseY, button);
                        }
                        int companyItemOwned = loadedStallTrade.getCompanyItemAmount() >= 999 ? 999 : loadedStallTrade.getCompanyItemAmount();
                        if(tradeAmount > companyItemOwned) {
                            tradeAmount = companyItemOwned;
                        }
                        loadedStallTrade.setCompanyItemAmount(loadedStallTrade.getCompanyItemAmount() - tradeAmount);
                        loadedStallTrade.setSellable(loadedStallTrade.getCompanyItemAmount() > 0);
                        loadedStallTrade.setMarketItemAmount(loadedStallTrade.getMarketItemAmount() + tradeAmount);
                        loadedStallTrade.setActive(loadedStallTrade.getMarketItemAmount() > 0);
                        loadedStallTrade.setAutoTradeEnabled(false); //This is not an autoTrade
                    }
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    TransactStallTradeC2SPacket packet = new TransactStallTradeC2SPacket(handler.getBlockPos(), currentScreenTab == AbacusScreenTab.BUY, loadedStallTrade, tradeAmount);
                    packet.write(byteBuf);
                    this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_TRANSACT_PACKET_ID, byteBuf));
                    this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 1);
                    loadedStallTrade.setAutoTradeEnabled(autoTradeState);
                    return true;
                }
                u = (int)mouseX - (this.x + autoTradeConfirmX);
                v = (int)mouseY - (this.y + autoTradeConfirmY);
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
                    StallTrade autoTrade = new StallTrade(loadedStallTrade.getTradedItem(), loadedStallTrade.getEmeraldAmount(), loadedStallTrade.getEmeraldChange(), loadedStallTrade.getAmountBeforeCommon());
                    loadedStallTrade.setAutoTradeEnabled(true); //For rendering
                    autoTrade.setAutoTradeEnabled(true);
                    autoTrade.setStockState(loadedStallTrade.getStockState()); //Change to button setting
                    //if(currentScreenTab == AbacusScreenTab.BUY) {
                    //    autoTrade.setActive(true);
                    //}
                    //else {
                    //    autoTrade.setActive(false);
                    //}
                    PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                    TransactStallTradeC2SPacket packet = new TransactStallTradeC2SPacket(handler.getBlockPos(), currentScreenTab == AbacusScreenTab.BUY, autoTrade, tradeAmount);
                    packet.write(byteBuf);
                    this.client.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.ABACUS_TRANSACT_PACKET_ID, byteBuf));
                    this.client.interactionManager.clickButton(((AbacusScreenHandler)this.handler).syncId, 1);
                    return true;
                }
                u = (int)mouseX - (this.x + tradeSearchX);
                v = (int)mouseY - (this.y + tradeSearchY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    tradeAmountBox.setFocused(false);
                    tradeSearchBox.setFocused(true);
                    this.tradeSearchBox.setCursorToEnd(false);
                    this.tradeSearchBox.setSelectionEnd(0);
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
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        AbacusScreenTab screenTab = AbacusScreenTab.TABS[selectedTab];
        
        RenderSystem.disableBlend();
        Text translatedText = Text.translatable(screenTab.getId());
        titleX = (backgroundWidth - textRenderer.getWidth(translatedText)) / 2;
        context.drawText(this.textRenderer, translatedText, this.titleX, 6, 0x404040, false);
    }
 
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        AbacusScreenTab selectedScreenTab = AbacusScreenTab.TABS[selectedTab];
        for (AbacusScreenTab tab : AbacusScreenTab.TABS) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, BASE_TAB_TEXTURE);
            if (tab.getIndex() == selectedTab) continue;
            this.renderTabIcon(context, tab);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + selectedScreenTab.getBackgroundTexture());
        if(selectedScreenTab == AbacusScreenTab.INVENTORY) {
            //RenderSystem.setShaderTexture(0, new Identifier(selectedScreenTab.getBackgroundTexture()));
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            context.drawTexture(new Identifier(selectedScreenTab.getBackgroundTexture()), i, j, 0, 0, this.backgroundWidth, 3 * 18 + 17);
            context.drawTexture(new Identifier(selectedScreenTab.getBackgroundTexture()), i, j + 3 * 18 + 17, 0, 127, this.backgroundWidth, 96);
        }
        else {
            //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + selectedScreenTab.getBackgroundTexture()));
            context.drawTexture(currentTabIdentifier, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }
        
        int u = 0;
        int v = 0;
        if(selectedScreenTab == AbacusScreenTab.SETTINGS) {
            //renderSettingPageButtons
            int j = 0;
            int k = 17;
            int l = 0;
            for(int i = 0; i < 4; i++) {
                j = i * 20 + 12;
                u = mouseX - (this.x + j);
                v = mouseY - (this.y + k);
                l = getPageIconRenderHeight(i);

                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    context.drawTexture(currentTabIdentifier, this.x + j, this.y + k, 79, l, 18, 18);
                }
                else {
                    if(i != selectedSettingsPage) {
                        context.drawTexture(currentTabIdentifier, this.x + j, this.y + k, 61, l, 18, 18);
                    }
                    else {
                        context.drawTexture(currentTabIdentifier, this.x + j, this.y + k, 97, l, 18, 18);
                    }
                }
            }
            //this.itemRenderer.zOffset = 100;
            //this.itemRenderer.renderInGui(RenewAutoPlusInitialize.ABACUS.asItem().getDefaultStack(), this.x + 13, this.y + 18);
            //this.itemRenderer.renderInGui(RenewAutoPlusInitialize.CRATE.asItem().getDefaultStack(), this.x + 33, this.y + 18);
            //this.itemRenderer.zOffset = 0;
            context.drawItem(RenewAutoPlusInitialize.ABACUS.asItem().getDefaultStack(), this.x + 13, this.y + 18);
            context.drawItem(RenewAutoPlusInitialize.CRATE.asItem().getDefaultStack(), this.x + 33, this.y + 18);
            //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + selectedScreenTab.getBackgroundTexture()));

            if(selectedSettingsPage != AUTOTRADE_SETTINGS && selectedSettingsPage != CRATE_SETTINGS){
                u = mouseX - (this.x + settingsConfirmX);
                v = mouseY - (this.y + settingsConfirmY);
                if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                    context.drawTexture(currentTabIdentifier, this.x + settingsConfirmX, this.y + settingsConfirmY, 79, 184, 18, 18);
                }
                else {
                    context.drawTexture(currentTabIdentifier, this.x + settingsConfirmX, this.y + settingsConfirmY, 61, 184, 18, 18);
                }
            }
            else {
                context.drawTexture(currentTabIdentifier, this.x + settingsConfirmX, this.y + settingsConfirmY, 61, 184, 18, 18);
            }
            u = mouseX - (this.x + settingsDeleteX);
            v = mouseY - (this.y + settingsDeleteY);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                context.drawTexture(currentTabIdentifier, this.x + settingsDeleteX, this.y + settingsDeleteY, 79, 202, 18, 18);
            }
            else {
                context.drawTexture(currentTabIdentifier, this.x + settingsDeleteX, this.y + settingsDeleteY, 61, 202, 18, 18);
            }
            renderSettingsList(context, mouseX, mouseY);
            this.settingsInputBox.render(context, mouseX, mouseY, delta);
        }
        else if(selectedScreenTab == AbacusScreenTab.BUY || selectedScreenTab == AbacusScreenTab.SELL){
            u = mouseX - (this.x + tradeConfirmX);
            v = mouseY - (this.y + tradeConfirmY);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                if(loadedStallTrade != null) {
                    if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
                        if (!handler.canBuyWith(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange()) || !isLoadedTradeActive()) {
                            context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                        } else {
                            context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 79, 166, 18, 18);
                        }
                    }
                    else {
                        if (!loadedStallTrade.isSellable()) {
                            context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                        } else {
                            context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 79, 166, 18, 18);
                        }
                    }
                }
                else {
                    context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
                }
            }
            else {
                context.drawTexture(currentTabIdentifier, this.x + tradeConfirmX, this.y + tradeConfirmY, 61, 166, 18, 18);
            }
            u = mouseX - (this.x + autoTradeConfirmX);
            v = mouseY - (this.y + autoTradeConfirmY);
            if(u >= 0 && v >= 0 && u < 18 && v < 18) {
                if(loadedStallTrade != null) {
                    context.drawTexture(currentTabIdentifier, this.x + autoTradeConfirmX, this.y + autoTradeConfirmY, 79, 184, 18, 18);
                }
                else {
                    context.drawTexture(currentTabIdentifier, this.x + autoTradeConfirmX, this.y + autoTradeConfirmY, 61, 184, 18, 18);
                }
            }
            else {
                if(loadedStallTrade != null) {
                    if (loadedStallTrade.isAutoTradeEnabled()) {
                        context.drawTexture(currentTabIdentifier, this.x + autoTradeConfirmX, this.y + autoTradeConfirmY, 97, 184, 18, 18);
                    }
                    else {
                        context.drawTexture(currentTabIdentifier, this.x + autoTradeConfirmX, this.y + autoTradeConfirmY, 61, 184, 18, 18);
                    }
                }
                else {
                    context.drawTexture(currentTabIdentifier, this.x + autoTradeConfirmX, this.y + autoTradeConfirmY, 61, 184, 18, 18);
                }
            }
            renderStallTrades(context, selectedScreenTab, mouseX, mouseY);
            renderLoadedTrade(context, selectedScreenTab);
            this.tradeAmountBox.render(context, mouseX, mouseY, delta);
            this.tradeSearchBox.render(context, mouseX, mouseY, delta);
        }
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //RenderSystem.setShaderTexture(0, BASE_TAB_TEXTURE);
        this.renderTabIcon(context, selectedScreenTab);
    }
 
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    //If not needed remove
    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        //if (this.settingsInputBox != null) {
        //    this.settingsInputBox.tick();
        //}
        //if (this.tradeAmountBox != null) {
        //    this.tradeAmountBox.tick();
        //}
        //if (this.tradeSearchBox != null) {
        //    this.tradeSearchBox.tick();
        //}
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (selectedTab == AbacusScreenTab.BUY.getIndex() || selectedTab == AbacusScreenTab.SELL.getIndex()) {
            if(tradeAmountBox.isFocused()) {
                if (this.tradeAmountBox.charTyped(chr, modifiers)) {
                    return true;
                }
            }
            if(tradeSearchBox.isFocused()) {
                if (this.tradeSearchBox.charTyped(chr, modifiers)) {
                    searchTradeOffers();
                    return true;
                }
            }
        }
        else if(selectedTab == AbacusScreenTab.SETTINGS.getIndex()) {
            if(settingsInputBox.isFocused()) {
                if (this.settingsInputBox.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }
        return false;
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
        if (this.tradeSearchBox.keyPressed(keyCode, scanCode, modifiers)) {
            searchTradeOffers();
            return true;
        }
        if (this.tradeSearchBox.isFocused() && this.tradeAmountBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void searchForTags(String id2) {
        Predicate<Identifier> predicate;
        int i = id2.indexOf(58);
        if (i == -1) {
            predicate = id -> id.getPath().contains(id2);
        } else {
            String string = id2.substring(0, i).trim();
            String string2 = id2.substring(i + 1).trim();
            predicate = id -> id.getNamespace().contains(string) && id.getPath().contains(string2);
        }
        Registries.ITEM.streamTags().filter(tag -> predicate.test(tag.id())).forEach(this.searchResultTags::add);
    }

    private void searchTradeOffers() {
        this.searchTradeList.clear();
        String string = this.tradeSearchBox.getText();
        if (!string.isEmpty()) {
            SearchProvider<ItemStack> searchProvider;
            if (string.startsWith("#")) {
                string = string.substring(1);
                searchProvider = this.client.getSearchProvider(SearchManager.ITEM_TAG);
                this.searchForTags(string);
            } else {
                searchProvider = this.client.getSearchProvider(SearchManager.ITEM_TOOLTIP);
            }
            for(ItemStack stack : searchProvider.findAll(string.toLowerCase(Locale.ROOT))) { //Yucky
                for(StallTrade trade : handler.getStallTradeList()) {
                    if(ItemStack.areItemsEqual(stack, trade.getTradedItem())) {
                        this.searchTradeList.add(trade);
                    }
                }
            }
        }
        this.tradeIndexStartOffset = 0;
    }

    protected boolean isLoadedTradeActive() {
        if(loadedStallTrade == null) {
            return false;
        }
        return loadedStallTrade.isActive();
    }

    protected boolean isClickInScreenTab(AbacusScreenTab tab, double mouseX, double mouseY) {
        int i = tab.getColumn();
        int j = 28 * i;
        int k = -32;
        if (i > 0) {
            j += i;
        }
        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
    }

    protected boolean isClickInSettingsTab(int tab, double mouseX, double mouseY) {
        int i = tab * 20 + 12;
        int j = 17;
        return mouseX >= (double)i && mouseX <= (double)(i + 18) && mouseY >= (double)j && mouseY <= (double)(j + 18);
    }

    protected void renderTabIcon(DrawContext context, AbacusScreenTab tab) {
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
        context.drawTexture(BASE_TAB_TEXTURE, l, m, j, k, 28, 32);
        //this.itemRenderer.zOffset = 100.0f;
        int n2 = 1;
        ItemStack itemStack = tab.getIcon();
        //this.itemRenderer.renderInGuiWithOverrides(itemStack, l += 6, m += 8 + n2);
        //this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
        //this.itemRenderer.zOffset = 0.0f;
        context.drawItem(itemStack, l += 6, m += 8 + n2);
        //context.drawItemTooltip(this.textRenderer, itemStack, l, m);
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderTexture(0, BASE_TAB_TEXTURE);
        if(tab == AbacusScreenTab.BUY) {
            context.drawTexture(BASE_TAB_TEXTURE, l + 9, m + 8, 300, 10.0f, 64.0f, 10, 10, 256, 256);
        }
        else if(tab == AbacusScreenTab.SELL) {
            context.drawTexture(BASE_TAB_TEXTURE, l + 9, m + 8, 300, 0.0f, 64.0f, 10, 10, 256, 256);
        }
        else if(tab == AbacusScreenTab.SETTINGS) {
            context.drawTexture(BASE_TAB_TEXTURE, l + 10, m + 9, 300, 20.0f, 64.0f, 7, 7, 256, 256);
        }
        
    }

    protected int getPageIconRenderHeight(int i) {
        if(i < 2) {
            return 166;
        }
        else if(i == OWNER_SETTINGS) {
            return 238;
        }
        else if(i == AUTOTRADE_SETTINGS) {
            return 220;
        }
        return 166;
    }

    protected void selectTab(AbacusScreenTab tab) {
        if (tab == AbacusScreenTab.INVENTORY) {
            ((AbacusScreenHandler)this.handler).slots.clear();
            ((AbacusScreenHandler)this.handler).addSlots();
            super.mouseClicked(0, 0, 0); //Somehow this updates the client window ¯\_(ツ)_/¯
        } else if (selectedTab == AbacusScreenTab.INVENTORY.getIndex()) {
            ((AbacusScreenHandler)this.handler).slots.clear();
        }
        if (tab == AbacusScreenTab.BUY || tab == AbacusScreenTab.SELL) {
            if(this.tradeAmountBox != null) {
                this.tradeAmountBox.setVisible(true);
                this.tradeSearchBox.setFocusUnlocked(true);
            }
            if (this.tradeSearchBox != null) {
                this.tradeSearchBox.setVisible(true);
                this.tradeSearchBox.setFocusUnlocked(true);
                this.tradeSearchBox.setFocused(true);
            }
            for (TradeWidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = true;
            }
        }
        else if(selectedTab == AbacusScreenTab.BUY.getIndex() || selectedTab == AbacusScreenTab.SELL.getIndex()) {
            if(this.tradeAmountBox != null) {
                this.tradeAmountBox.setVisible(false);
                this.tradeAmountBox.setFocusUnlocked(false);
                this.tradeAmountBox.setFocused(false);
            }
            if (this.tradeSearchBox != null) {
                this.tradeSearchBox.setVisible(false);
                this.tradeSearchBox.setFocusUnlocked(false);
                this.tradeSearchBox.setFocused(false);
            }
            for (TradeWidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = false;
            }
        }
        
        if (tab == AbacusScreenTab.SETTINGS) {
            if (this.settingsInputBox != null) {
                this.settingsInputBox.setVisible(true);
                this.settingsInputBox.setFocusUnlocked(true);
                this.settingsInputBox.setFocused(true);
            }
        } else if(selectedTab == AbacusScreenTab.SETTINGS.getIndex()) {
            if (this.settingsInputBox != null) {
                this.settingsInputBox.setVisible(false);
                this.settingsInputBox.setFocusUnlocked(false);
                this.settingsInputBox.setFocused(false);
            }
            for (TradeWidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
                widgetButtonPage.visible = false;
            }
        }
        selectedTab = tab.getIndex();
    }

    protected void selectSettingsPage(int newPage) {
        if(newPage > 3 || newPage < 0) {
            return;
        }

        if(newPage == COMPANY_SETTINGS) {
            this.settingsInputBox.setText(handler.getCurrentCompanyName());
        }
        if(newPage == CRATE_SETTINGS) {
            //turn on list
            this.settingsInputBox.setText("");
        }
        else if(selectedSettingsPage == CRATE_SETTINGS) {
            for (TradeWidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
                widgetButtonPage.visible = false;
            }
        }
        if(newPage == OWNER_SETTINGS) {
            //turn on list
            this.settingsInputBox.setText("");
        }
        else if(selectedSettingsPage == OWNER_SETTINGS) {
            for (TradeWidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
                widgetButtonPage.visible = false;
            }
        }
        if(newPage == AUTOTRADE_SETTINGS) {
            //turn on list
            this.settingsInputBox.setText("");
        }
        else if(selectedSettingsPage == AUTOTRADE_SETTINGS) {
            for (TradeWidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
                widgetButtonPage.visible = false;
            }
        }

        selectedSettingsPage = newPage;
    }

    private boolean canTradeScroll(int listSize) {
        return listSize > 6;
    }

    private boolean canSettingsScroll(int listSize) {
        return listSize > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(selectedTab == AbacusScreenTab.BUY.getIndex() || selectedTab == AbacusScreenTab.SELL.getIndex()) {
            int i;
            if(tradeSearchBox.getText().isEmpty()) {
                i = handler.getStallTradeList().size();
            }
            else {
                i = searchTradeList.size();
            }
            if (this.canTradeScroll(i)) {
                int j = i - 6;
                this.tradeIndexStartOffset = (int)((double)this.tradeIndexStartOffset - verticalAmount);
                this.tradeIndexStartOffset = MathHelper.clamp(this.tradeIndexStartOffset, 0, j);
            }
        }
        else if(selectedTab == AbacusScreenTab.SETTINGS.getIndex()) {
            int i = 0;
            if(selectedSettingsPage == CRATE_SETTINGS) {
                i = handler.getAttachedCratesList().size();
            } else if(selectedSettingsPage == OWNER_SETTINGS) {
                i = handler.getOwnerNameList().size() + 1; //For add button
            } else if(selectedSettingsPage == AUTOTRADE_SETTINGS) {
                i = handler.getAutoTradeList().size();
            }
            if (this.canSettingsScroll(i)) {
                int j = i - 7;
                this.settingIndexStartOffset = (int)((double)this.settingIndexStartOffset - verticalAmount);
                this.settingIndexStartOffset = MathHelper.clamp(this.settingIndexStartOffset, 0, j);
            }
        }
        return true;
    }

    private void renderTradeScrollbar(DrawContext context, int x, int y, StallTradeList tradeOffers) {
        int i = tradeOffers.size() + 1 - 6;
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture());
        if (i > 1) {
            int j = 119 - (9 + (i - 1) * 119 / i);
            int k = 1 + j / i + 119 / i;
            int m = Math.min(111, this.tradeIndexStartOffset * k);
            if (this.tradeIndexStartOffset == i - 1) {
                m = 111;
            }
            //AbacusScreen.drawTexture(matrices, x + 70, y + 29 + m, this.getZOffset(), 0.0f, 226.0f, 6, 9, 256, 256);
            context.drawTexture(currentTabIdentifier, x + 70, y + 29 + m, 0.0f, 226.0f, 6, 9, 256, 256);
        } else {
            //AbacusScreen.drawTexture(matrices, x + 70, y + 29, this.getZOffset(), 6.0f, 226.0f, 6, 9, 256, 256);
            context.drawTexture(currentTabIdentifier, x + 70, y + 29, 0.0f, 226.0f, 6, 9, 256, 256);
        }
    }

    private void renderSettingsScrollbar(DrawContext context, int x, int y, int size) {
        int i = size + 1 - 7;
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture());
        if (i > 1) {
            int j = 119 - (9 + (i - 1) * 119 / i);
            int k = 1 + j / i + 119 / i;
            int m = Math.min(111, this.settingIndexStartOffset * k);
            if (this.settingIndexStartOffset == i - 1) {
                m = 111;
            }
            context.drawTexture(currentTabIdentifier, x + 162, y + 18 + m, 0.0f, 226.0f, 6, 9, 256, 256);
        } else {
            context.drawTexture(currentTabIdentifier, x + 162, y + 18, 6.0f, 226.0f, 6, 9, 256, 256);
        }
    }

    protected void renderStallTrades(DrawContext context, AbacusScreenTab tab, double mouseX, double mouseY) {
        StallTradeList stallTradeList;
        if(this.tradeSearchBox.getText().isEmpty()) {
            stallTradeList = handler.getStallTradeList();
        } else {
            stallTradeList = this.searchTradeList;
        }
        if (stallTradeList == null) return;
        if (!stallTradeList.isEmpty()) {
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = j + 29;
            int l = i + 10;
            //RenderSystem.setShader(GameRenderer::getPositionTexShader);
            
            //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + tab.getBackgroundTexture()));
            RenderSystem.enableBlend();
            this.renderTradeScrollbar(context, i, j, stallTradeList);
            int m = 0;
            for (TradeWidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = widgetButtonPage.index < stallTradeList.size();
                if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
                    if((widgetButtonPage.index + this.tradeIndexStartOffset) < stallTradeList.size()) {
                        widgetButtonPage.active = stallTradeList.get(widgetButtonPage.index + this.tradeIndexStartOffset).isActive();
                    }
                    else {
                        widgetButtonPage.active = false;
                    }
                }
                else {
                    widgetButtonPage.active = true;
                }
                if (widgetButtonPage.isSelected()) {
                    widgetButtonPage.renderTooltip(context, (int)mouseX, (int)mouseY);
                }
            }
            for (StallTrade stallTrade : stallTradeList) { //Can optimize maybe with sublist
                if (this.canTradeScroll(stallTradeList.size()) && (m < this.tradeIndexStartOffset || m >= 6 + this.tradeIndexStartOffset)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = stallTrade.getTradedItem();
                ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
                //this.itemRenderer.zOffset = 100.0f;
                //Render first item
                int n = k + 2;
                if(tab == AbacusScreenTab.BUY) {
                    //this.itemRenderer.renderInGui(emeraldStack, l, n);
                    //this.itemRenderer.renderGuiItemOverlay(this.textRenderer, emeraldStack, l, n);
                    context.drawItem(emeraldStack, l, n);
                    //context.drawItemTooltip(this.textRenderer, emeraldStack, l, n);
                }
                else {
                    context.drawItem(itemStack, l, n);
                    //context.drawItemTooltip(this.textRenderer, itemStack, l, n);
                    renderStock(context, stallTrade, l, n);
                }
                //Render arrow
                renderArrow(context, stallTrade, i, n);
                if(tab == AbacusScreenTab.BUY) {
                    context.drawItem(itemStack, l + 40, n);
                    //context.drawItemTooltip(this.textRenderer, itemStack, l + 40, n);
                    renderStock(context, stallTrade, l + 40, n);
                }
                else
                {
                    context.drawItem(emeraldStack, l + 40, n);
                    //context.drawItemTooltip(this.textRenderer, emeraldStack, l + 40, n);
                }
                //this.itemRenderer.zOffset = 0.0f;
                k += 20;
                ++m;
            }
        }
        else {
            for (TradeWidgetButtonPage widgetButtonPage : this.offers) {
                widgetButtonPage.visible = false;
            }
        }
    }

    private void renderStock(DrawContext context, StallTrade tradeOffer, int x, int y){
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture()));
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture());
        if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
            if(tradeOffer.getStockState() == StallTrade.StockState.LOW) {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 135.0f, 166.0f, 10, 10, 256, 256);
            }
            else if(tradeOffer.getStockState() == StallTrade.StockState.HIGH) {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 115.0f, 166.0f, 10, 10, 256, 256);
            }
            else {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 125.0f, 166.0f, 10, 10, 256, 256);
            }
        }
        else if (AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.SELL) {
            if(tradeOffer.getStockState() == StallTrade.StockState.LOW) {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 115.0f, 166.0f, 10, 10, 256, 256);
            }
            else if(tradeOffer.getStockState() == StallTrade.StockState.HIGH) {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 135.0f, 166.0f, 10, 10, 256, 256);
            }
            else {
                context.drawTexture(currentTabIdentifier, x + 7, y + 6, 300, 125.0f, 166.0f, 10, 10, 256, 256);
            }
        }
    }

    private void renderArrow(DrawContext context, StallTrade tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture()));
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture());
        if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.BUY) {
            if (!handler.canBuyWith(tradeOffer.getBuyEmeraldAmount(), tradeOffer.getBuyEmeraldChange())) {
                context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 22.0f, 226.0f, 10, 9, 256, 256);
            } else {
                context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 12.0f, 226.0f, 10, 9, 256, 256);
            }
        }
        else if (AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.SELL) {
            if (!tradeOffer.isSellable()) {
                context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 22.0f, 226.0f, 10, 9, 256, 256);
            } else {
                context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 12.0f, 226.0f, 10, 9, 256, 256);
            }
        }
        else if(AbacusScreenTab.TABS[selectedTab] == AbacusScreenTab.SETTINGS) {
            if(tradeOffer.isActive()) {
                if (!handler.canBuyWith(tradeOffer.getBuyEmeraldAmount(), tradeOffer.getBuyEmeraldChange())) {
                    context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 22.0f, 226.0f, 10, 9, 256, 256);
                } else {
                    context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 12.0f, 226.0f, 10, 9, 256, 256);
                }
            }
            else {
                if (!tradeOffer.isSellable()) {
                    context.drawTexture(currentTabIdentifier, x + 33, y + 3, 1, 22.0f, 226.0f, 10, 9, 256, 256);
                } else {
                    context.drawTexture(currentTabIdentifier, x + 33, y + 3,  1, 12.0f, 226.0f, 10, 9, 256, 256);
                }
            }
        }
    }

    protected void renderLoadedTrade(DrawContext context, AbacusScreenTab tab) {
        if(loadedStallTrade == null) {
            return;
        }
        if(this.handler.shouldUpdateLoadedTrade()) {
            loadedStallTrade = this.handler.getStallTradeList().get(this.selectedTradeIndex);
            this.handler.updateLoadedTradeCompleted();
        }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int l = i + 93;
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
        int companyItemOwned = loadedStallTrade.getCompanyItemAmount() >= 999 ? 999 : loadedStallTrade.getCompanyItemAmount();
        int marketItemOwned = loadedStallTrade.getMarketItemAmount() >= 999 ? 999 : loadedStallTrade.getMarketItemAmount();
        String emeraldOwnedString = handler.getEmeraldString();

        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        ItemStack itemStack = loadedStallTrade.getTradedItem();
        ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
        //this.itemRenderer.zOffset = 100.0f;
        if(tab == AbacusScreenTab.BUY) {
            if(!handler.canBuyWith(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange(), tradeAmount) || tradeAmount > marketItemOwned) {
                int maxCanBuy = handler.getMaxCanBuy(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange());
                tradeAmount = maxCanBuy > marketItemOwned ? marketItemOwned : maxCanBuy;
            }
            String emeraldString = handler.getEmeraldString(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange(), tradeAmount);
            context.drawItem(emeraldStack, l, k);
            //context.drawItemTooltip(this.textRenderer, emeraldStack, l, k);
            context.drawItem(itemStack, l + 48, k);
            //context.drawItemTooltip(this.textRenderer, itemStack, l + 40, k);
            //top
            m = (handler.getEmeraldFromChangeAmount(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange(), tradeAmount) >= 100 ? 85 : (handler.getEmeraldFromChangeAmount(loadedStallTrade.getBuyEmeraldAmount(), loadedStallTrade.getBuyEmeraldChange(), tradeAmount) >= 10 ? 88 : 91));
            n = (tradeAmount >= 100 ? 141 : (tradeAmount >= 10 ? 144 : 147)); // Garbage and raplace with aligning code, Text.width
            context.drawText(textRenderer, emeraldString, i + m, j + 68, 0x404040, false);
            context.drawText(textRenderer, Integer.toString(tradeAmount), i + n, j + 68, 0x404040, false);
            n = ((22 - this.textRenderer.getWidth(Integer.toString(marketItemOwned))) / 2) + 114;
            context.drawText(textRenderer, Integer.toString(marketItemOwned), i + n, j + 58, 0x404040, false);
            //bottom
            m = (handler.getEmeraldAmount() >= 100 ? 85 : (handler.getEmeraldAmount() >= 10 ? 88 : 91));
            n = (companyItemOwned >= 100 ? 141 : (companyItemOwned >= 10 ? 144 : 147));
            context.drawText(textRenderer, emeraldOwnedString, i + m, j + 98, 0x404040, false);
            context.drawText(textRenderer, Integer.toString(companyItemOwned), i + n, j + 98, 0x404040, false);
        }
        else {
            if(tradeAmount > companyItemOwned) {
                tradeAmount = companyItemOwned;
            }
            String emeraldString = handler.getEmeraldString(loadedStallTrade.getSellEmeraldAmount(), loadedStallTrade.getSellEmeraldChange(), tradeAmount);
            context.drawItem(itemStack, l, k);
            //context.drawItemTooltip(this.textRenderer, itemStack, l, k);
            context.drawItem(emeraldStack, l + 48, k);
            //context.drawItemTooltip(this.textRenderer, emeraldStack, l + 40, k);
            //top
            m = (handler.getEmeraldFromChangeAmount(loadedStallTrade.getSellEmeraldAmount(), loadedStallTrade.getSellEmeraldChange(), tradeAmount) >= 100 ? 134 : (handler.getEmeraldFromChangeAmount(loadedStallTrade.getSellEmeraldAmount(), loadedStallTrade.getSellEmeraldChange(), tradeAmount) >= 10 ? 137 : 140));
            n = (tradeAmount >= 100 ? 92 : (tradeAmount >= 10 ? 95 : 98));
            context.drawText(textRenderer, emeraldString, i + m, j + 68, 0x404040, false);
            context.drawText(textRenderer, Integer.toString(tradeAmount), i + n, j + 68, 0x404040, false);
            //bottom
            m = (handler.getEmeraldAmount() >= 100 ? 134 : (handler.getEmeraldAmount() >= 10 ? 137 : 140));
            n = (companyItemOwned >= 100 ? 92 : (companyItemOwned >= 10 ? 95 : 98));
            context.drawText(textRenderer, emeraldOwnedString, i + m, j + 98, 0x404040, false);
            context.drawText(textRenderer, Integer.toString(companyItemOwned), i + n, j + 98, 0x404040, false);
        }
        //this.itemRenderer.zOffset = 0.0f;
    }

    protected void renderSettingsList(DrawContext context, double mouseX, double mouseY) {
        //if selectedList is empty return
        if(selectedSettingsPage == COMPANY_SETTINGS) {
            return;
        }
        int listSize = 0; //fill with selected list
        if(selectedSettingsPage == CRATE_SETTINGS) {
            listSize = handler.getAttachedCratesList().size();
        } else if(selectedSettingsPage == OWNER_SETTINGS) {
            listSize = handler.getOwnerNameList().size() + 1; //For add button
        } else if(selectedSettingsPage == AUTOTRADE_SETTINGS) {
            listSize = handler.getAutoTradeList().size();
        }
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int l = i + 100;
        int k = j + 18;
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture()));
        RenderSystem.enableBlend();
        Identifier currentTabIdentifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture());
        this.renderSettingsScrollbar(context, i, j, listSize);
        int m = 0;
        for (TradeWidgetButtonPage widgetButtonPage : this.selectedSettingsList) {
            widgetButtonPage.visible = widgetButtonPage.index < listSize;
            widgetButtonPage.active = true;
            if (widgetButtonPage.isSelected() && selectedSettingsPage == AUTOTRADE_SETTINGS) {
                widgetButtonPage.renderTooltip(context, (int)mouseX, (int)mouseY);
            }
        }

        //Render list based on selected
        if(selectedSettingsPage == CRATE_SETTINGS) {
            DefaultedList<BlockPos> crateList = handler.getAttachedCratesList();
            for (BlockPos cratePos : crateList) {
                if (this.canSettingsScroll(crateList.size()) && (m < this.settingIndexStartOffset || m >= 7 + this.settingIndexStartOffset)) {
                    ++m;
                    continue;
                }
                String cratePosString = cratePos.getX() + "," + cratePos.getY() + "," + cratePos.getZ();
                if(cratePosString.length() > 11) {
                    cratePosString = cratePosString.substring(0, 8) + "...";
                }
                int stringX = (62 - this.textRenderer.getWidth(cratePosString)) / 2;
                context.drawText(textRenderer, cratePosString, l + stringX, k + 6, 0xFFFFFFFF, true);
                k += 20;
                ++m;
            }
        }
        else if(selectedSettingsPage == OWNER_SETTINGS) {
            DefaultedList<String> ownerList = handler.getOwnerNameList();
            for (String owner : ownerList) {
                if (this.canSettingsScroll(ownerList.size()) && (m < this.settingIndexStartOffset || m >= 7 + this.settingIndexStartOffset)) {
                    ++m;
                    continue;
                }
                if(owner.length() > 11) {
                    owner = owner.substring(0, 8) + "...";
                }
                int stringX = (62 - this.textRenderer.getWidth(owner)) / 2;
                context.drawText(textRenderer, owner, l + stringX, k + 6, 0xFFFFFFFF, true);
                k += 20;
                ++m;
            }
            if(m >= ownerList.size()) {
                //RenderSystem.setShader(GameRenderer::getPositionTexShader);
                //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + AbacusScreenTab.TABS[selectedTab].getBackgroundTexture()));
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                context.drawTexture(currentTabIdentifier, l + 23, k + 3, 1, 115.0f, 166.0f, 15, 15, 256, 256);
            }
        }
        else if(selectedSettingsPage == AUTOTRADE_SETTINGS) {
            StallTradeList autoTradeList = handler.getAutoTradeList();
            for (StallTrade stallTrade : autoTradeList) { //Can optimize maybe with sublist
                if (this.canSettingsScroll(autoTradeList.size()) && (m < this.settingIndexStartOffset || m >= 7 + this.settingIndexStartOffset)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = stallTrade.getTradedItem();
                ItemStack emeraldStack = Items.EMERALD.getDefaultStack();
                //this.itemRenderer.zOffset = 100.0f;
                //Render first item
                int n = k + 2;
                if(stallTrade.isAutoPurchase()) {
                    context.drawItem(emeraldStack, l + 4, n);
                    //context.drawItemTooltip(this.textRenderer, emeraldStack, l, n);
                }
                else {
                    context.drawItem(itemStack, l + 4, n);
                    //context.drawItemTooltip(this.textRenderer, itemStack, l, n);
                }
                //Render arrow
                renderArrow(context, stallTrade, l + 20, n);
                if(stallTrade.isAutoPurchase()) {
                    context.drawItem(itemStack, l + 40, n);
                    //context.drawItemTooltip(this.textRenderer, itemStack, l + 40, n);
                }
                else
                {
                    context.drawItem(emeraldStack, l + 40, n);
                    //context.drawItemTooltip(this.textRenderer, emeraldStack, l + 40, n);
                }
                //this.itemRenderer.zOffset = 0.0f;
                k += 20;
                ++m;
            }
        }
    }
 
    @Override
    protected void init() {
        super.init();
        //this.client.keyboard.setRepeatEvents(true);
        this.settingsInputBox = new TextFieldWidget(this.textRenderer, this.x + 13, this.y + 84, 78, this.textRenderer.fontHeight, Text.of(""));
        this.settingsInputBox.setMaxLength(20);
        this.settingsInputBox.setDrawsBackground(false);
        this.settingsInputBox.setVisible(false);
        this.settingsInputBox.setFocusUnlocked(true);
        this.settingsInputBox.setFocused(false);
        this.settingsInputBox.setEditableColor(0xFFFFFF);
        this.settingsInputBox.setText(handler.getCurrentCompanyName());

        this.tradeAmountBox = new TextFieldWidget(this.textRenderer, this.x + 116, this.y + 67, 18, this.textRenderer.fontHeight, Text.of(""));
        this.tradeAmountBox.setMaxLength(3);
        this.tradeAmountBox.setDrawsBackground(false);
        this.tradeAmountBox.setVisible(true);
        this.tradeAmountBox.setFocusUnlocked(true);
        this.tradeAmountBox.setFocused(false);
        this.tradeAmountBox.setEditableColor(0xFFFFFF);
        this.tradeAmountBox.setText("1");

        this.tradeSearchBox = new TextFieldWidget(this.textRenderer, this.x + 8, this.y + 17, 60, this.textRenderer.fontHeight, Text.of(""));
        this.tradeSearchBox.setMaxLength(20);
        this.tradeSearchBox.setDrawsBackground(false);
        this.tradeSearchBox.setVisible(true);
        this.tradeSearchBox.setFocusUnlocked(true);
        this.tradeSearchBox.setFocused(true);
        this.tradeSearchBox.setEditableColor(0xFFFFFF);
        this.tradeSearchBox.setText("");

        this.addSelectableChild(this.settingsInputBox);
        this.addSelectableChild(this.tradeAmountBox);
        this.addSelectableChild(this.tradeSearchBox);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 29;
        for (int l = 0; l < 6; ++l) {
            this.offers[l] = this.addDrawableChild(new TradeWidgetButtonPage(i + 8, k, l, button -> {
                if (button instanceof TradeWidgetButtonPage) {
                    this.selectedTradeIndex = ((TradeWidgetButtonPage)button).getIndex() + this.tradeIndexStartOffset;
                    if(AbacusScreen.this.tradeSearchBox.getText().isEmpty()) {
                        this.loadedStallTrade = this.handler.getStallTradeList().get(this.selectedTradeIndex);
                    } else {
                        if(this.selectedTradeIndex < this.searchTradeList.size()) {
                            this.loadedStallTrade = this.searchTradeList.get(this.selectedTradeIndex);
                        }
                    }
                    this.tradeAmountBox.setFocused(true);
                    this.tradeAmountBox.setCursorToEnd(false);
                    this.tradeSearchBox.setFocused(false);
                }
            }));
            k += 20;
        }
        k = j + 18;
        for (int l = 0; l < 7; ++l) {
            this.selectedSettingsList[l] = this.addDrawableChild(new TradeWidgetButtonPage(i + 100, k, l, button -> {
                if (button instanceof TradeWidgetButtonPage) {
                    this.selectedSettingListIndex = ((TradeWidgetButtonPage)button).getIndex() + this.tradeIndexStartOffset;
                    if(selectedSettingsPage == CRATE_SETTINGS) {
                        this.settingsInputBox.setText(this.handler.getAttachedCratesList().get(selectedSettingListIndex).toShortString());
                    } 
                    else if(selectedSettingsPage == OWNER_SETTINGS) {
                        if(selectedSettingListIndex < this.handler.getOwnerNameList().size()) {
                            this.settingsInputBox.setText(this.handler.getOwnerNameList().get(selectedSettingListIndex));
                        }
                        else {
                            this.settingsInputBox.setText("");
                        }
                    }
                }
            }));
            k += 20;
        }
    }

    //Make seperate for settings
    class TradeWidgetButtonPage
    extends ButtonWidget {
        final int index;

        public TradeWidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, 61, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            if (this.hovered && ((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().size() > this.index + AbacusScreen.this.tradeIndexStartOffset) {
                if (mouseX < this.getX() + 20) {
                    if(AbacusScreen.this.selectedTab == AbacusScreenTab.BUY.getIndex()) {
                        context.drawItemTooltip(textRenderer, Items.EMERALD.getDefaultStack(), mouseX, mouseY);
                    }
                    else {
                        ItemStack itemStack;
                        if(AbacusScreen.this.tradeSearchBox.getText().isEmpty()) {
                            itemStack = ((StallTrade)((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().get(this.index + AbacusScreen.this.tradeIndexStartOffset)).getTradedItem();
                            context.drawItemTooltip(textRenderer, itemStack, mouseX, mouseY);
                        }
                        else {
                            if(this.index + AbacusScreen.this.tradeIndexStartOffset < AbacusScreen.this.searchTradeList.size()) {
                                itemStack = AbacusScreen.this.searchTradeList.get(this.index + AbacusScreen.this.tradeIndexStartOffset).getTradedItem();
                                context.drawItemTooltip(textRenderer, itemStack, mouseX, mouseY);
                            }
                        }
                        
                    }
                }
                else if (mouseX > this.getX() + 45) {
                    if(AbacusScreen.this.selectedTab == AbacusScreenTab.BUY.getIndex()) {
                        ItemStack itemStack;
                        if(AbacusScreen.this.tradeSearchBox.getText().isEmpty()) {
                            itemStack = ((StallTrade)((AbacusScreenHandler)AbacusScreen.this.handler).getStallTradeList().get(this.index + AbacusScreen.this.tradeIndexStartOffset)).getTradedItem();
                            context.drawItemTooltip(textRenderer, itemStack, mouseX, mouseY);
                        }
                        else {
                            if(this.index + AbacusScreen.this.tradeIndexStartOffset < AbacusScreen.this.searchTradeList.size()) {
                                itemStack = AbacusScreen.this.searchTradeList.get(this.index + AbacusScreen.this.tradeIndexStartOffset).getTradedItem();
                                context.drawItemTooltip(textRenderer, itemStack, mouseX, mouseY);
                            }
                        }
                    }
                    else {
                        context.drawItemTooltip(textRenderer, Items.EMERALD.getDefaultStack(), mouseX, mouseY);
                    }
                }
            }
        }

        //public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        //    if(!this.visible) {
        //        return;
        //    }
        //    //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //    //RenderSystem.setShaderTexture(0, new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + "abacus_screen_trade.png")); //Needs change to own separate texture
        //    //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        //    Identifier identifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + "abacus_screen_trade.png");
        //    //int i = this.getY(this.isHovered());
        //    int i = 0;
        //    //RenderSystem.enableBlend();
        //    //RenderSystem.defaultBlendFunc();
        //    //RenderSystem.enableDepthTest();
        //    context.drawTexture(identifier, x, y, 0, 166 + i * 20, this.width, this.height);
        //    if (this.isHovered()) {
        //        this.renderTooltip(context, mouseX, mouseY);
        //    }
        //}

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            Identifier identifier = new Identifier("renew_auto_plus", TAB_TEXTURE_PREFIX + "abacus_screen_trade.png"); //Needs change to own separate texture
            int i = this.active ? this.isHovered() ? 2 : 1 : 0;
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawTexture(identifier, this.getX(), this.getY(), 0, 166 + i * 20, this.width, this.height);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
