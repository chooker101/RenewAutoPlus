package net.fabricmc.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.village.VillagerProfession;

public class StallTrades {
    //Generic trades, always available, NO DUPLICATES ALLOWED
    public static final ItemValueFactory[] STALL_TRADES = new ItemValueFactory[]{
        new ItemValueFactory(Items.WHEAT, 0, 10, 1000), 
        new ItemValueFactory(Items.POTATO, 0, 8, 1000), 
        new ItemValueFactory(Items.CARROT, 0, 9, 1000), 
        new ItemValueFactory(Items.COAL, 0, 20, 1000), 
        new ItemValueFactory(Items.DIAMOND, 2, 0, 1000),
        new ItemValueFactory(Items.STICK, 0, 4, 1000),
        new ItemValueFactory(Items.COBBLESTONE, 0, 8, 1000)
    };

    //Duplicates allowed
    public static final HashMap<VillagerProfession, ItemValueFactory[]> VILLAGER_TRADES = Util.make(Maps.newHashMap(), map -> {
        map.put(VillagerProfession.FARMER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.BEETROOT, 0, 10, 1000)
        });
        map.put(VillagerProfession.FISHERMAN, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.STRING, 0, 10, 1000)
        });
        map.put(VillagerProfession.SHEPHERD, 
        new ItemValueFactory[]{
            new ItemValueFactory(Blocks.WHITE_WOOL, 0, 10, 1000),
            new ItemValueFactory(Items.STRING, 0, 10, 1000)
        });
        map.put(VillagerProfession.FLETCHER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.ARROW, 0, 10, 100)
        });
        map.put(VillagerProfession.LIBRARIAN, 
        new ItemValueFactory[]{
            new ItemValueFactory(Blocks.BOOKSHELF, 0, 10, 100)
        });
        map.put(VillagerProfession.CARTOGRAPHER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.MAP, 0, 10, 100)
        });
        map.put(VillagerProfession.CLERIC, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.REDSTONE, 0, 10, 1000)
        });
        map.put(VillagerProfession.ARMORER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.IRON_LEGGINGS, 0, 10, 100)
        });
        map.put(VillagerProfession.WEAPONSMITH, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.IRON_SWORD, 0, 10, 100)
        });
        map.put(VillagerProfession.TOOLSMITH, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.STONE_AXE, 0, 10, 100)
        });
        map.put(VillagerProfession.BUTCHER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.PORKCHOP, 0, 10, 1000)
        });
        map.put(VillagerProfession.LEATHERWORKER, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.LEATHER, 0, 10, 1000)
        });
        map.put(VillagerProfession.MASON, 
        new ItemValueFactory[]{
            new ItemValueFactory(Items.CLAY_BALL, 0, 10, 1000)
        });
    });

    //Not sure if this is needed, but keeping incase exp
    public static class ItemValueFactory {
        private final Item tradeItem;
        private final int emeraldAmount;
        private final int emeraldChange;
        private final int amountBeforeCommon;

        ItemValueFactory(ItemConvertible item, int emeraldAmount, int emeraldChange, int amountBeforeCommon){
            this.tradeItem = item.asItem();
            this.emeraldAmount = emeraldAmount;
            this.emeraldChange = emeraldChange;
            this.amountBeforeCommon = amountBeforeCommon;
        }

        public @Nullable StallTrade create() {
            ItemStack itemStack = new ItemStack(this.tradeItem);
            return new StallTrade(itemStack, emeraldAmount, emeraldChange, amountBeforeCommon);
        }
        
    }
}
