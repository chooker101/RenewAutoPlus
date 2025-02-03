package net.renew_auto_plus;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class FurnaceBlockEntityReplacement extends AbstractFurnaceBlockEntity {
    public int furnaceMaxHeatLevel;
    TranslatableTextContent containerName;

    public FurnaceBlockEntityReplacement(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.FURNACE_BLOCK_ENTITY_REPLACEMENT, pos, state, RecipeType.SMELTING);
        furnaceMaxHeatLevel = 38599;
        containerName = new TranslatableTextContent("container.furnace", "Furnace", null); //Might be fucked IDK?
    }

    public FurnaceBlockEntityReplacement(BlockPos pos, BlockState state, int maxHeatLevel, TranslatableTextContent name) {
        super(RenewAutoPlusInitialize.FURNACE_BLOCK_ENTITY_REPLACEMENT, pos, state, RecipeType.SMELTING);
        furnaceMaxHeatLevel = maxHeatLevel;
        containerName = name;
    }

    @Override
    protected Text getContainerName() {
        return Text.of(containerName.toString());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public static boolean isBurning(FurnaceBlockEntityReplacement blockEntity) {
        return getBurnTime(blockEntity) > 0;
    }

    public static int getBurnTime(FurnaceBlockEntityReplacement blockEntity) {
        return blockEntity.propertyDelegate.get(0);
    }

    public static int getFuelTime(FurnaceBlockEntityReplacement blockEntity) {
        String name = blockEntity.inventory.get(1).getName().getString();
        if(name.equals("Charcoal")) {
            return 800;
        }
        if(name.equals("Coal Block")) {
            return 3200;
        }
        if(name.equals("Lava Bucket")) {
            return 6400;
        }
        return blockEntity.getFuelTime(blockEntity.inventory.get(1));
    }

    public static int getCookTime(FurnaceBlockEntityReplacement blockEntity) {
        return blockEntity.propertyDelegate.get(2);
    }
    
    public static int getCookTimeTotal(FurnaceBlockEntityReplacement blockEntity) {
        return blockEntity.propertyDelegate.get(3);
    }

    public static void setBurnTime(FurnaceBlockEntityReplacement blockEntity, int i) {
        blockEntity.propertyDelegate.set(0, i);
    }

    public static void setFuelTime(FurnaceBlockEntityReplacement blockEntity, int i) {
        blockEntity.propertyDelegate.set(1, i);
    }

    public static void setCookTime(FurnaceBlockEntityReplacement blockEntity, int i) {
        blockEntity.propertyDelegate.set(2, i);
    }
    
    public static void setCookTimeTotal(FurnaceBlockEntityReplacement blockEntity, int i) {
        blockEntity.propertyDelegate.set(3, i);
    }

    public static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (slots.get(0).isEmpty() || recipe == null) {
            return false;
        }
        @SuppressWarnings("resource")
        ItemStack itemStack = recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager());
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!ItemStack.areEqual(itemStack, itemStack2)) {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    private static boolean craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null || !canAcceptRecipeOutput(recipe, slots, count)) {
            return false;
        }
        ItemStack itemStack = slots.get(0);
        @SuppressWarnings("resource")
        ItemStack itemStack2 = recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager());
        ItemStack itemStack3 = slots.get(2);
        if (itemStack3.isEmpty()) {
            slots.set(2, itemStack2.copy());
        } else if (itemStack3.isOf(itemStack2.getItem())) {
            itemStack3.increment(1);
        }
        if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !slots.get(1).isEmpty() && slots.get(1).isOf(Items.BUCKET)) {
            slots.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        itemStack.decrement(1);
        return true;
    }

    public static int getMinHeatLevel(ItemStack itemStack) {
        String name = itemStack.getName().getString();
        if(name.equals("Raw Copper")) {
            return 2400;
        }
        if(name.equals("Raw Gold")) {
            return 4800;
        }
        if(name.equals("Raw Iron")) {
            return 9600;
        }
        if(name.equals("Raw Diamond")) {
            return 19200;
        }
        return 0;
    }

    public static int getMaxHeatLevel(ItemStack itemStack) {
        String name = itemStack.getName().getString();
        if(name.equals("Charcoal")) {
            return 4799;
        }
        if(name.equals("Coal")) {
            return 9599;
        }
        if(name.equals("Coal Block")) {
            return 19199;
        }
        if(name.equals("Lava Bucket")) {
            return 38599;
        }
        return 38599;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntityRaw) {
        if(!(blockEntityRaw instanceof FurnaceBlockEntityReplacement) || world.isClient()){
            return;
        }
        FurnaceBlockEntityReplacement blockEntity = (FurnaceBlockEntityReplacement)blockEntityRaw;
        boolean wasBurning = isBurning(blockEntity);
        boolean updateBlock = false;
        float remainder = 0.0f;
        if (isBurning(blockEntity)) {
            setBurnTime(blockEntity, getBurnTime(blockEntity) - 1);
            remainder = (float)getBurnTime(blockEntity) % 48.0f;
        }
        ItemStack itemStack = blockEntity.inventory.get(1);
        if (isBurning(blockEntity) || !itemStack.isEmpty() && !blockEntity.inventory.get(0).isEmpty()) {
            RecipeEntry<SmeltingRecipe> recipeEntry = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, blockEntity, world).orElse(null); //Changed from raw
            if(recipeEntry == null) { return; }
            SmeltingRecipe recipe = recipeEntry.value(); //Changed to smelting recipe from Recipe<Inventory>
            int i = blockEntity.getMaxCountPerStack();
            if(remainder == 0.0f) {
                if((isBurning(blockEntity) && getMinHeatLevel(blockEntity.inventory.get(0)) >= getBurnTime(blockEntity)) && getMaxHeatLevel(itemStack) > (getBurnTime(blockEntity) + getFuelTime(blockEntity))) {
                    updateBlock = true;
                    setBurnTime(blockEntity, getBurnTime(blockEntity) + getFuelTime(blockEntity));
                    if(getBurnTime(blockEntity) > blockEntity.furnaceMaxHeatLevel) {
                        setBurnTime(blockEntity, blockEntity.furnaceMaxHeatLevel);
                    }
                    if (!itemStack.isEmpty()) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            blockEntity.inventory.set(1, ItemStack.EMPTY);
                        }
                    }
                }
            }
            if (!isBurning(blockEntity) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                setFuelTime(blockEntity, blockEntity.furnaceMaxHeatLevel);
                setBurnTime(blockEntity,  getFuelTime(blockEntity));
                if (isBurning(blockEntity)) {
                    updateBlock = true;
                    if (!itemStack.isEmpty()) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            blockEntity.inventory.set(1, ItemStack.EMPTY);
                        }
                    }
                }
            }
            if ((isBurning(blockEntity) && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) && getMinHeatLevel(blockEntity.inventory.get(0)) <= getBurnTime(blockEntity)) {
                setCookTime(blockEntity, getCookTime(blockEntity) + 1);
                if (getCookTime(blockEntity) == getCookTimeTotal(blockEntity)) {
                    setCookTime(blockEntity, 0);
                    setCookTimeTotal(blockEntity, recipe.getCookingTime()); //Changed from full regrab for smelting recipe
                    if (craftRecipe(recipe, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipeEntry); //Changed to use entry from above as now requires entry
                    }
                    updateBlock = true;
                }
            } else {
                setCookTime(blockEntity, 0);
            }
        } else if (!isBurning(blockEntity) && getCookTime(blockEntity) > 0) {
            setCookTime(blockEntity, MathHelper.clamp(getCookTime(blockEntity) - 2, 0, getCookTimeTotal(blockEntity)));
        }
        if (wasBurning != isBurning(blockEntity)) {
            updateBlock = true;
            state = (BlockState)state.with(AbstractFurnaceBlock.LIT, isBurning(blockEntity));
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (updateBlock) {
            AbstractFurnaceBlockEntity.markDirty(world, pos, state);
        }
    }
}
