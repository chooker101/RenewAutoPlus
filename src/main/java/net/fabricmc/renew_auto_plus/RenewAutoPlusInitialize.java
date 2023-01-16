package net.fabricmc.renew_auto_plus;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class RenewAutoPlusInitialize implements ModInitializer {
	public static ToolItem COPPER_AXE = new CustomAxeItem(CopperToolMaterial.INSTANCE, 6.0f, -3.2F, new Item.Settings().group(ItemGroup.TOOLS));
	public static ToolItem COPPER_HOE = new CustomHoeItem(CopperToolMaterial.INSTANCE, -2, -1.0F, new Item.Settings().group(ItemGroup.TOOLS));
	public static ToolItem COPPER_PICKAXE = new CustomPickaxeItem(CopperToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS));
	public static ToolItem COPPER_SHOVEL = new ShovelItem(CopperToolMaterial.INSTANCE, 1.5f, -3.0F, new Item.Settings().group(ItemGroup.TOOLS));
	public static ToolItem COPPER_SWORD = new SwordItem(CopperToolMaterial.INSTANCE, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT));

	public static final Item RAW_DIAMOND = new Item(new FabricItemSettings().group(ItemGroup.MISC));

	public static final Block SMELTER = new SmelterBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block CLOCK_BLOCK = new ClockBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block CAPACITOR = new CapacitorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block LETTUCE_BLOCK = new LettuceBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static final Block PUMP = new PumpBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());

	public static final StatusEffect BIG_DICK = Registry.register(Registry.STATUS_EFFECT, 65, "big_dick", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0x44FF44));
	public static final FoodComponent LETTUC_FOOD_COMPONENT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(BIG_DICK, 200, 0), 0.3f).build();

	public static final Item LETTUCE = new AliasedBlockItem(LETTUCE_BLOCK, new Item.Settings().group(ItemGroup.FOOD).food(LETTUC_FOOD_COMPONENT));

	public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "extractor"), ExtractorScreenHandler::new);
	public static final ScreenHandlerType<ClockBlockScreenHandler> CLOCK_BLOCK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "clock_block"), ClockBlockScreenHandler::new);
	public static final ScreenHandlerType<CapacitorScreenHandler> CAPACITOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "capacitor"), CapacitorScreenHandler::new);

	public static final BlockEntityType<FurnaceBlockEntityReplacement> FURNACE_BLOCK_ENTITY_REPLACEMENT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "furnace_entity_replacement"), FabricBlockEntityTypeBuilder.create(FurnaceBlockEntityReplacement::new, Blocks.FURNACE, SMELTER).build());
	public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "extractor_block_entity"), FabricBlockEntityTypeBuilder.create(ExtractorBlockEntity::new, EXTRACTOR).build());
	public static final BlockEntityType<ClockBlockEntity> CLOCK_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "clock_block_entity"), FabricBlockEntityTypeBuilder.create(ClockBlockEntity::new, CLOCK_BLOCK).build());
	public static final BlockEntityType<CapacitorBlockEntity> CAPACITOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "capacitor_block_entity"), FabricBlockEntityTypeBuilder.create(CapacitorBlockEntity::new, CAPACITOR).build());
	public static final BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "pump_block_entity"), FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, PUMP).build());

	public static final PaintingMotive HEY_THERE = Registry.register(Registry.PAINTING_MOTIVE, new Identifier("renew_auto_plus", "hey_there"), new PaintingMotive(64, 64));

	public static SoundEvent EXTRACTOR_ON_SE = new SoundEvent(new Identifier("renew_auto_plus", "extractor_on"));

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_shovel"), COPPER_SHOVEL);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_pickaxe"), COPPER_PICKAXE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_axe"), COPPER_AXE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_hoe"), COPPER_HOE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_sword"), COPPER_SWORD);

		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "raw_diamond"), RAW_DIAMOND);

		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "smelter"), SMELTER);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "smelter"), new BlockItem(SMELTER, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "extractor"), EXTRACTOR);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "extractor"), new BlockItem(EXTRACTOR, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "clock_block"), CLOCK_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "clock_block"), new BlockItem(CLOCK_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "capacitor"), CAPACITOR);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "capacitor"), new BlockItem(CAPACITOR, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "pump"), PUMP);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "pump"), new BlockItem(PUMP, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "lettuce"), LETTUCE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "lettuce"), LETTUCE);

		Registry.register(Registry.SOUND_EVENT, new Identifier("renew_auto_plus", "extractor_on"), EXTRACTOR_ON_SE);

		System.out.println("RenewAutoPlus is loaded.");
	}
}
