package net.fabricmc.renew_auto_plus;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public abstract class AbacusScreenTab {
    public static final AbacusScreenTab[] TABS = new AbacusScreenTab[4];
    private final String id;
    private final int index;
    private ItemStack icon;
    private String backgroundTexture;

    public static final AbacusScreenTab BUY = new AbacusScreenTab("renew_auto_plus.abacus_tab.buy", 0, RenewAutoPlusInitialize.ABACUS.asItem().getDefaultStack(), "abacus_screen_trade.png"){};
    public static final AbacusScreenTab SELL = new AbacusScreenTab("renew_auto_plus.abacus_tab.sell", 1, RenewAutoPlusInitialize.ABACUS.asItem().getDefaultStack(), "abacus_screen_trade.png"){};
    public static final AbacusScreenTab SETTINGS = new AbacusScreenTab("renew_auto_plus.abacus_tab.settings", 2, RenewAutoPlusInitialize.ABACUS.asItem().getDefaultStack(), "abacus_screen_settings.png"){};
    public static final AbacusScreenTab INVENTORY = new AbacusScreenTab("renew_auto_plus.abacus_tab.inventory", 3, Items.CHEST.asItem().getDefaultStack(), "textures/gui/container/generic_54.png"){};

    AbacusScreenTab(String id, int index, ItemStack icon, String backgroundTexture) {
        this.id = id;
        this.index = index;
        this.icon = icon;
        this.backgroundTexture = backgroundTexture;
        AbacusScreenTab.TABS[index] = this;
    }

    public String getId(){
        return id;
    }

    public int getIndex(){
        return index;
    }

    public ItemStack getIcon(){
        return icon;
    }

    public int getColumn() {
        return this.index % 4;
    }

    public String getBackgroundTexture(){
        return backgroundTexture;
    }
}
