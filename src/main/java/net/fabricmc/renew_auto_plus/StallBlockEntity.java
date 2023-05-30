package net.fabricmc.renew_auto_plus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class StallBlockEntity extends BlockEntity implements AutoCloseable {
    private HashMap<Item, Integer> inventory = new HashMap<>();
    private StallTradeList stallTradeList;
    private Vector<VillagerProfession> villageProfessions = new Vector<>();
    private int myIndex;

    public StallBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.STALL_BLOCK_ENTITY, pos, state);
        myIndex = MarketManager.instance().addMarket(this);
    }

    @Override
    public void close() throws Exception {
        MarketManager.instance().removeMarket(myIndex);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtList itemList = nbt.getList("Items", NbtCompound.COMPOUND_TYPE);
        if(itemList != null) {
            for(NbtElement nbtCompound : itemList) {
                if(nbtCompound instanceof NbtCompound) {
                    Identifier id = new Identifier(((NbtCompound)nbtCompound).getString("id"));
                    Item item = Registry.ITEM.get(id);
                    int count = ((NbtCompound)nbtCompound).getInt("Count");
                    if(item != null && count >= 0) {
                        inventory.put(item, count);
                    }
                }
            }
        }
        byte[] serializedProfessions = nbt.getByteArray("Professions");
        if(serializedProfessions != null) {
            for(Byte profession : serializedProfessions){
                villageProfessions.add(JankyProfessionSerializer.fromByte(profession));
            }
        }
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList nbtList = new NbtList();
        if(!inventory.isEmpty()) {
            for (Entry<Item, Integer> item : inventory.entrySet()) {
                if (item.getValue() < 0) continue;
                NbtCompound nbtCompound = new NbtCompound();
                Identifier identifier = Registry.ITEM.getId(item.getKey());
                nbtCompound.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
                nbtCompound.putInt("Count", item.getValue());
                nbtList.add(nbtCompound);
            }
            if (!nbtList.isEmpty()) {
                nbt.put("Items", nbtList);
            }
        }
        if(!villageProfessions.isEmpty()) {
            ArrayList<Byte> serializedProffesions = new ArrayList<Byte>();
            for (VillagerProfession profession : villageProfessions) {
                serializedProffesions.add(JankyProfessionSerializer.toByte(profession));
            }
            nbt.putByteArray("Professions", serializedProffesions);
        }
    }

    public void updateVillagerList() {
        if (!this.world.isClient) {
            BlockPos blockPos = this.getPos();
            Box box = new Box(blockPos).expand(48.0);
            List<VillagerEntity> nearbyVillagers = this.world.getNonSpectatingEntities(VillagerEntity.class, box);
            Vector<VillagerProfession> checkedProfessions = new Vector<>(15);
            for (VillagerEntity villager : nearbyVillagers) {
                boolean proffesionExist = false;
                VillagerProfession currentProfession = villager.getVillagerData().getProfession();
                for(VillagerProfession profession : checkedProfessions){
                    if(currentProfession == profession) {
                        proffesionExist = true;
                    }
                }
                if(!proffesionExist) {
                    villageProfessions.add(currentProfession);
                    checkedProfessions.add(currentProfession);
                }
            }
        }
    }
    
    public StallTradeList getStallTradeList(){
        if(this.stallTradeList == null){
            this.stallTradeList = new StallTradeList();
        }
        if(this.stallTradeList.isEmpty()) {
            fillTradeListFromPool();
        }
        return this.stallTradeList;
    }

    private void fillTradeListFromPool() {
        if(villageProfessions != null && !villageProfessions.isEmpty()) {
            for (VillagerProfession profession : villageProfessions) {
                for (StallTrades.ItemValueFactory factory : StallTrades.VILLAGER_TRADES.get(profession)) {
                    StallTrade tradeOffer = factory.create();
                    if (tradeOffer == null) continue;
                    tradeOffer.setActive(isTradeActive(tradeOffer));
                    stallTradeList.add(tradeOffer);
                }
            }
        }
        for (StallTrades.ItemValueFactory factory : StallTrades.STALL_TRADES) {
            StallTrade tradeOffer = factory.create();
            if (tradeOffer == null) continue;
            tradeOffer.setActive(isTradeActive(tradeOffer));
            stallTradeList.add(tradeOffer);
        }
    }

    public Recipe<?> findCraftingRecipeFromId(ItemConvertible outputItem) {
        return (Recipe<?>)world.getRecipeManager().values().stream().filter(r -> r.getOutput().getItem() == outputItem.asItem()).findFirst().map(Recipe::getId).orElse(null);
    }

    private boolean isTradeActive(StallTrade tradeOffer) {
        Item tradeItem = tradeOffer.getTradedItem().getItem();
        if(inventory == null) {
            return false;
        }
        if(inventory.get(tradeItem) == null){
            return false;
        }
        if(inventory.get(tradeItem) >= 1) {
            return true;
        }
        else {
            Recipe<?> recipe = findCraftingRecipeFromId(tradeItem);
            if (recipe != null && recipe instanceof CraftingRecipe) {
                CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
                for(Ingredient ingredient : craftingRecipe.getIngredients()){
                    boolean hasAnIngredient = false;
                    for(ItemStack item : ingredient.getMatchingStacks()) {
                        if(inventory.get(item.getItem()) >= 1) {
                            hasAnIngredient = true;
                        }
                    }
                    if(!hasAnIngredient) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static class JankyProfessionSerializer {
        public static Byte toByte(VillagerProfession profession) {
            if(profession == VillagerProfession.NONE) {
                return 0;
            }
            else if(profession == VillagerProfession.ARMORER) {
                return 1;
            }
            else if(profession == VillagerProfession.BUTCHER) {
                return 2;
            }
            else if(profession == VillagerProfession.CARTOGRAPHER) {
                return 3;
            }
            else if(profession == VillagerProfession.CLERIC) {
                return 4;
            }
            else if(profession == VillagerProfession.FARMER) {
                return 5;
            }
            else if(profession == VillagerProfession.FISHERMAN) {
                return 6;
            }
            else if(profession == VillagerProfession.FLETCHER) {
                return 7;
            }
            else if(profession == VillagerProfession.LEATHERWORKER) {
                return 8;
            }
            else if(profession == VillagerProfession.LIBRARIAN) {
                return 9;
            }
            else if(profession == VillagerProfession.MASON) {
                return 10;
            }
            else if(profession == VillagerProfession.NITWIT) {
                return 11;
            }
            else if(profession == VillagerProfession.SHEPHERD) {
                return 12;
            }
            else if(profession == VillagerProfession.TOOLSMITH) {
                return 13;
            }
            else if(profession == VillagerProfession.WEAPONSMITH) {
                return 14;
            }
            return -1;
        }

        public static VillagerProfession fromByte(Byte profession) {
            if(profession <= 0) {
                return VillagerProfession.NONE;
            }
            else if(profession == 1) {
                return VillagerProfession.ARMORER;
            }
            else if(profession == 2) {
                return VillagerProfession.BUTCHER;
            }
            else if(profession == 3) {
                return VillagerProfession.CARTOGRAPHER;
            }
            else if(profession == 4) {
                return VillagerProfession.CLERIC;
            }
            else if(profession == 5) {
                return VillagerProfession.FARMER;
            }
            else if(profession == 6) {
                return VillagerProfession.FISHERMAN;
            }
            else if(profession == 7) {
                return VillagerProfession.FLETCHER;
            }
            else if(profession == 8) {
                return VillagerProfession.LEATHERWORKER;
            }
            else if(profession == 9) {
                return VillagerProfession.LIBRARIAN;
            }
            else if(profession == 10) {
                return VillagerProfession.MASON;
            }
            else if(profession == 11) {
                return VillagerProfession.NITWIT;
            }
            else if(profession == 12) {
                return VillagerProfession.SHEPHERD;
            }
            else if(profession == 13) {
                return VillagerProfession.TOOLSMITH;
            }
            else if(profession == 14) {
                return VillagerProfession.WEAPONSMITH;
            }
            return VillagerProfession.NONE;
        }
    }
}
