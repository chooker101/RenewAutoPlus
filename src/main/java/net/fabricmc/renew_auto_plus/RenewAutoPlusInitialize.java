package net.fabricmc.renew_auto_plus;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.potion.Potion;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class RenewAutoPlusInitialize implements ModInitializer {
	public static final ToolItem COPPER_AXE = new CustomAxeItem(CopperToolMaterial.INSTANCE, 6.0f, -3.2F, new Item.Settings().group(ItemGroup.TOOLS));
	public static final ToolItem COPPER_HOE = new CustomHoeItem(CopperToolMaterial.INSTANCE, -2, -1.0F, new Item.Settings().group(ItemGroup.TOOLS));
	public static final ToolItem COPPER_PICKAXE = new CustomPickaxeItem(CopperToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings().group(ItemGroup.TOOLS));
	public static final ToolItem COPPER_SHOVEL = new ShovelItem(CopperToolMaterial.INSTANCE, 1.5f, -3.0F, new Item.Settings().group(ItemGroup.TOOLS));
	public static final ToolItem COPPER_SWORD = new SwordItem(CopperToolMaterial.INSTANCE, 3, -2.4F, new Item.Settings().group(ItemGroup.COMBAT));
	public static final ToolItem BANISHED_SWORD = new SwordItem(BanishedToolMaterial.INSTANCE, 2, -1.2F, new Item.Settings().group(ItemGroup.COMBAT));
	public static final ToolItem BANISHED_SWORD_CLEAR = new SwordItem(BanishedToolMaterial.INSTANCE, 2, -1.2F, new Item.Settings().group(ItemGroup.COMBAT));
	public static final BanishedParryingSwordItem BANISHED_PARRYING_SWORD = new BanishedParryingSwordItem(BanishedToolMaterial.INSTANCE, 0, -1.2F, new Item.Settings().group(ItemGroup.COMBAT));
	public static final BanishedParryingSwordItem BANISHED_PARRYING_SWORD_CLEAR = new BanishedParryingSwordItem(BanishedToolMaterial.INSTANCE, 0, -1.2F, new Item.Settings().group(ItemGroup.COMBAT));

	public static final Item BANISHED_HELMET = new ArmorItem(BanishedArmorMaterial.INSTANCE, EquipmentSlot.HEAD, new Item.Settings());
	public static final Item BANISHED_CHESTPLATE = new ArmorItem(BanishedArmorMaterial.INSTANCE, EquipmentSlot.CHEST, new Item.Settings());
	public static final Item BANISHED_LEGGINGS = new ArmorItem(BanishedArmorMaterial.INSTANCE, EquipmentSlot.LEGS, new Item.Settings());
	public static final Item BANISHED_BOOTS = new ArmorItem(BanishedArmorMaterial.INSTANCE, EquipmentSlot.FEET, new Item.Settings());

	public static final Item RAW_DIAMOND = new Item(new FabricItemSettings().group(ItemGroup.MISC));

	public static final StatusEffect BIG_DICK = Registry.register(Registry.STATUS_EFFECT, "big_dick", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0x44FF44));
	public static final StatusEffect SHADOWED = Registry.register(Registry.STATUS_EFFECT, "shadowed", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 8356754));
	
	public static final FoodComponent LETTUCE_FOOD_COMPONENT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(BIG_DICK, 200, 0), 0.3f).build();

	public static final Potion SHADOW_POTION = Registry.register(Registry.POTION, new Identifier("renew_auto_plus", "shadow_potion"), new Potion(new StatusEffectInstance(SHADOWED, 3600, 0)));

	public static final Block SMELTER = new SmelterBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block CLOCK_BLOCK = new ClockBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block CAPACITOR = new CapacitorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block LETTUCE_BLOCK = new LettuceBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static final Block PUMP = new PumpBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block COPPER_PIPE = new CopperPipeBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block ABACUS = new AbacusBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block STALL = new StallBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
	public static final Block CRATE = new CrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1.0f));
	public static final Block WHITE_SAND = new SandBlock(0xf4f5ebff, FabricBlockSettings.of(Material.AGGREGATE, MapColor.WHITE).strength(0.5f).sounds(BlockSoundGroup.SAND));
	public static final Block WHITE_SANDSTONE = new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).requiresTool().strength(0.8f));
	public static final Block WASTES_RUIN = new PillarBlock(FabricBlockSettings.of(Material.STONE, MapColor.RED).requiresTool().strength(0.8f));

	public static final Item LETTUCE = new AliasedBlockItem(LETTUCE_BLOCK, new Item.Settings().group(ItemGroup.FOOD).food(LETTUCE_FOOD_COMPONENT));

	public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "extractor"), ExtractorScreenHandler::new);
	public static final ScreenHandlerType<ClockBlockScreenHandler> CLOCK_BLOCK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "clock_block"), ClockBlockScreenHandler::new);
	public static final ScreenHandlerType<CapacitorScreenHandler> CAPACITOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "capacitor"), CapacitorScreenHandler::new);
	public static final ScreenHandlerType<PumpScreenHandler> PUMP_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("renew_auto_plus", "pump"), PumpScreenHandler::new);
	public static final ScreenHandlerType<AbacusScreenHandler> ABACUS_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("renew_auto_plus", "abacus"), AbacusScreenHandler::new);
	public static final ScreenHandlerType<CrateScreenHandler> CRATE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("renew_auto_plus", "crate"), CrateScreenHandler::new);

	public static final BlockEntityType<FurnaceBlockEntityReplacement> FURNACE_BLOCK_ENTITY_REPLACEMENT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "furnace_entity_replacement"), FabricBlockEntityTypeBuilder.create(FurnaceBlockEntityReplacement::new, Blocks.FURNACE, SMELTER).build());
	public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "extractor_block_entity"), FabricBlockEntityTypeBuilder.create(ExtractorBlockEntity::new, EXTRACTOR).build());
	public static final BlockEntityType<ClockBlockEntity> CLOCK_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "clock_block_entity"), FabricBlockEntityTypeBuilder.create(ClockBlockEntity::new, CLOCK_BLOCK).build());
	public static final BlockEntityType<CapacitorBlockEntity> CAPACITOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "capacitor_block_entity"), FabricBlockEntityTypeBuilder.create(CapacitorBlockEntity::new, CAPACITOR).build());
	public static final BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "pump_block_entity"), FabricBlockEntityTypeBuilder.create(PumpBlockEntity::new, PUMP).build());
	public static final BlockEntityType<CopperPipeBlockEntity> COPPER_PIPE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "copper_pipe_entity"), FabricBlockEntityTypeBuilder.create(CopperPipeBlockEntity::new, COPPER_PIPE).build());
	public static final BlockEntityType<AbacusBlockEntity> ABACUS_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "abacus_entity"), FabricBlockEntityTypeBuilder.create(AbacusBlockEntity::new, ABACUS).build());
	public static final BlockEntityType<StallBlockEntity> STALL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "stall_entity"), FabricBlockEntityTypeBuilder.create(StallBlockEntity::new, STALL).build());
	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "crate_entity"), FabricBlockEntityTypeBuilder.create(CrateBlockEntity::new, CRATE).build());

	public static final EntityType<BanishedEntity> BANISHED_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier("renew_auto_plus", "banished_entity"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BanishedEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
	public static final Item BANISHED_SPAWN_EGG = new SpawnEggItem(BANISHED_ENTITY, 0xdfe0d9, 0x111111, new FabricItemSettings().group(ItemGroup.MISC));

	public static final PaintingMotive HEY_THERE = Registry.register(Registry.PAINTING_MOTIVE, new Identifier("renew_auto_plus", "hey_there"), new PaintingMotive(64, 64));

	public static SoundEvent EXTRACTOR_ON_SE = new SoundEvent(new Identifier("renew_auto_plus", "extractor_on"));

	public static final Identifier ABACUS_STRING_PACKET_ID = new Identifier("renew_auto_plus", "abacus_string_packet");
	public static final Identifier ABACUS_TRANSACT_PACKET_ID = new Identifier("renew_auto_plus", "abacus_transact_packet");
	public static final Identifier ABACUS_REMOVE_AUTO_TRADE_PACKET_ID = new Identifier("renew_auto_plus", "abacus_remove_auto_packet");
	public static final Identifier ABACUS_REMOVE_ATTACHED_CRATE_PACKET_ID = new Identifier("renew_auto_plus", "abacus_remove_crate_packet");
	public static final Identifier CRATE_STRING_PACKET_ID = new Identifier("renew_auto_plus", "crate_string_packet");

	public static final Identifier THE_WASTES = new Identifier("renew_auto_plus", "the_wastes");
	public static final RegistryKey<World> THE_WASTES_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, THE_WASTES);

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_shovel"), COPPER_SHOVEL);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_pickaxe"), COPPER_PICKAXE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_axe"), COPPER_AXE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_hoe"), COPPER_HOE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_sword"), COPPER_SWORD);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_sword"), BANISHED_SWORD);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_sword_clear"), BANISHED_SWORD_CLEAR);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_parrying_sword"), BANISHED_PARRYING_SWORD);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_parrying_sword_clear"), BANISHED_PARRYING_SWORD_CLEAR);

		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_helmet"), BANISHED_HELMET);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_chestplate"), BANISHED_CHESTPLATE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_leggings"), BANISHED_LEGGINGS);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_boots"), BANISHED_BOOTS);

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
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "copper_pipe"), COPPER_PIPE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "copper_pipe"), new BlockItem(COPPER_PIPE, new Item.Settings().group(ItemGroup.REDSTONE)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "abacus"), ABACUS);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "abacus"), new BlockItem(ABACUS, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "stall"), STALL);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "stall"), new BlockItem(STALL, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "crate"), CRATE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "crate"), new BlockItem(CRATE, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "white_sand"), WHITE_SAND);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "white_sand"), new BlockItem(WHITE_SAND, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "white_sandstone"), WHITE_SANDSTONE);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "white_sandstone"), new BlockItem(WHITE_SANDSTONE, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.BLOCK, new Identifier("renew_auto_plus", "wastes_ruin"), WASTES_RUIN);
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "wastes_ruin"), new BlockItem(WASTES_RUIN, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));

		FabricDefaultAttributeRegistry.register(BANISHED_ENTITY, BanishedEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("renew_auto_plus", "banished_spawn_egg"), BANISHED_SPAWN_EGG);

		Registry.register(Registry.SOUND_EVENT, new Identifier("renew_auto_plus", "extractor_on"), EXTRACTOR_ON_SE);

		registerServerPackets();

		System.out.println("RenewAutoPlus is loaded.");
	}

	public void registerServerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_STRING_PACKET_ID, new StringToAbacusPlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_TRANSACT_PACKET_ID, new TransactStallTradePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_REMOVE_AUTO_TRADE_PACKET_ID, new RemoveAutoTradePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_REMOVE_ATTACHED_CRATE_PACKET_ID, new RemoveAttachedCratePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(CRATE_STRING_PACKET_ID, new StringToCratePlayChannelHandler());
	}
}
