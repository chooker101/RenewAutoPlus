package net.renew_auto_plus;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ColoredFallingBlock;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.potion.Potion;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class RenewAutoPlusInitialize implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("RenewAutoPlus");
	public static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	public static BlockPos wastesSpawnPos = null;

	public static final ToolItem COPPER_AXE = new CustomAxeItem(CopperToolMaterial.INSTANCE, 6.0f, -3.2F, new Item.Settings());
	public static final ToolItem COPPER_HOE = new CustomHoeItem(CopperToolMaterial.INSTANCE, -2, -1.0F, new Item.Settings());
	public static final ToolItem COPPER_PICKAXE = new CustomPickaxeItem(CopperToolMaterial.INSTANCE, 1, -2.8F, new Item.Settings());
	public static final ToolItem COPPER_SHOVEL = new ShovelItem(CopperToolMaterial.INSTANCE, 1.5f, -3.0F, new Item.Settings());
	public static final ToolItem COPPER_SWORD = new SwordItem(CopperToolMaterial.INSTANCE, 3, -2.4F, new Item.Settings());
	public static final ToolItem BANISHED_SWORD = new SwordItem(BanishedToolMaterial.INSTANCE, 2, -1.2F, new Item.Settings());
	public static final ToolItem BANISHED_SWORD_CLEAR = new SwordItem(BanishedToolMaterial.INSTANCE, 2, -1.2F, new Item.Settings());
	public static final BanishedParryingSwordItem BANISHED_PARRYING_SWORD = new BanishedParryingSwordItem(BanishedToolMaterial.INSTANCE, 0, -1.2F, new Item.Settings());
	public static final BanishedParryingSwordItem BANISHED_PARRYING_SWORD_CLEAR = new BanishedParryingSwordItem(BanishedToolMaterial.INSTANCE, 0, -1.2F, new Item.Settings());
	public static final AmethystWandItem AMETHYST_WAND = new AmethystWandItem(new Item.Settings().maxDamage(750));
	public static final AquamarineWandItem AQUAMARINE_WAND = new AquamarineWandItem(new Item.Settings().maxDamage(750));
	public static final RubyWandItem RUBY_WAND = new RubyWandItem(new Item.Settings().maxDamage(750));
	public static final TopazWandItem TOPAZ_WAND = new TopazWandItem(new Item.Settings().maxDamage(750));
	public static final OnyxWandItem ONYX_WAND = new OnyxWandItem(new Item.Settings().maxDamage(750));

	public static final Item BANISHED_HELMET = new ArmorItem(BanishedArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings());
	public static final Item BANISHED_CHESTPLATE = new ArmorItem(BanishedArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings());
	public static final Item BANISHED_LEGGINGS = new ArmorItem(BanishedArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings());
	public static final Item BANISHED_BOOTS = new ArmorItem(BanishedArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings());
	public static final Item COPPER_HELMET = new ArmorItem(CopperArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings());
	public static final Item COPPER_CHESTPLATE = new ArmorItem(CopperArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings());
	public static final Item COPPER_LEGGINGS = new ArmorItem(CopperArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings());
	public static final Item COPPER_BOOTS = new ArmorItem(CopperArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings());
	public static final Item LINEN_HOOD = new ArmorItem(LinenArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings());
	public static final Item LINEN_ROBE = new ArmorItem(LinenArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings());
	public static final Item LINEN_LEGGINGS = new ArmorItem(LinenArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings());
	public static final Item LINEN_BOOTS = new ArmorItem(LinenArmorMaterial.INSTANCE, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings());

	public static final Item RAW_DIAMOND = new Item(new FabricItemSettings());
	public static final Item SPICE = new Item(new FabricItemSettings());
	public static final Item LINEN = new Item(new FabricItemSettings());
	public static final Item AQUAMARINE_SHARD = new Item(new FabricItemSettings());
	public static final Item ONYX_SHARD = new Item(new FabricItemSettings());
	public static final Item RUBY_SHARD = new Item(new FabricItemSettings());
	public static final Item TOPAZ_SHARD = new Item(new FabricItemSettings());

	public static final StatusEffect BIG_DICK = Registry.register(Registries.STATUS_EFFECT, "big_dick", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0x44FF44));
	public static final StatusEffect SHADOWED = Registry.register(Registries.STATUS_EFFECT, "shadowed", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 8356754));
	public static final StatusEffect ICEBOUND = Registry.register(Registries.STATUS_EFFECT, "icebound", new PublicStatusEffect(StatusEffectCategory.HARMFUL, 0xA2D2DF){
		private LivingEntity attachedEntity = null;

		@Override
		public void onApplied(LivingEntity entity, int amplifier) {
			super.onApplied(entity, amplifier);
			attachedEntity = entity;
		}

		@Override
		public void onRemoved(AttributeContainer attributes) {
			super.onRemoved(attributes);
			if(attachedEntity == null) return;
			if(!attachedEntity.hasStatusEffect(ICEBOUND_RESISTANCE)){
				//Needs a delay when clearStatusEffects is called
				singleThreadExecutor.execute(() -> {
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					attachedEntity.addStatusEffect(new StatusEffectInstance(RenewAutoPlusInitialize.ICEBOUND_RESISTANCE, 60, 1));
					attachedEntity = null;
				});
			}
		}
	}.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "ef39cfb8-d51b-41d7-b02d-dcf5a94d5f05", -1.0f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final StatusEffect ICEBOUND_RESISTANCE = Registry.register(Registries.STATUS_EFFECT, "icebound_resistance", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0xEBEB3F){
		@Override
		public void onApplied(LivingEntity entity, int amplifier) {
			super.onApplied(entity, amplifier);
			if(entity.hasStatusEffect(ICEBOUND)){
				entity.removeStatusEffect(ICEBOUND);
			}
		}
	});
	public static final StatusEffect CHARGED = Registry.register(Registries.STATUS_EFFECT, "charged", new PublicStatusEffect(StatusEffectCategory.BENEFICIAL, 0xF58442));

	public static final RegistryKey<DamageType> DIRECT_MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("renew_auto_plus", "direct_magic"));
	
	public static final FoodComponent LETTUCE_FOOD_COMPONENT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(BIG_DICK, 200, 0), 0.3f).build();
	public static final FoodComponent MAYONNAISE_FOOD_COMPONENT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).build();

	public static final Potion SHADOW_POTION = Registry.register(Registries.POTION, new Identifier("renew_auto_plus", "shadow_potion"), new Potion(new StatusEffectInstance(SHADOWED, 3600, 0)));
	public static final Potion ICEBOUND_POTION = Registry.register(Registries.POTION, new Identifier("renew_auto_plus", "icebound_potion"), new Potion(new StatusEffectInstance(ICEBOUND, 3600, 0)));

	public static final Block SMELTER = new SmelterBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block CLOCK_BLOCK = new ClockBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block CAPACITOR = new CapacitorBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block LETTUCE_BLOCK = new LettuceBlock(FabricBlockSettings.create().nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static final Block PUMP = new PumpBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block COPPER_PIPE = new CopperPipeBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block ABACUS = new AbacusBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block STALL = new StallBlock(FabricBlockSettings.create().strength(4.0f).requiresTool());
	public static final Block CRATE = new CrateBlock(FabricBlockSettings.create().strength(1.0f));
	public static final Block WHITE_SAND = new ColoredFallingBlock(new ColorCode(0xf4f5ebff), FabricBlockSettings.create().mapColor(MapColor.WHITE).strength(0.5f).sounds(BlockSoundGroup.SAND));
	public static final Block WHITE_SANDSTONE = new Block(FabricBlockSettings.create().mapColor(MapColor.WHITE).requiresTool().strength(0.8f));
	public static final Block WASTES_RUIN = new PillarBlock(FabricBlockSettings.create().mapColor(MapColor.RED).requiresTool().strength(50.0f, 1200.0f));
	public static final Block WASTES_PORTAL_FRAME = new WastesPortalFrameBlock(FabricBlockSettings.create().mapColor(MapColor.DULL_RED).requiresTool().strength(0.8f).luminance(state -> state.get(Properties.POWERED) ? 13 : 0).nonOpaque());
	public static final Block WASTES_PORTAL_CONTROLLER = new WastesPortalControllerBlock(FabricBlockSettings.create().mapColor(MapColor.DULL_RED).requiresTool().strength(0.8f));
	public static final Block RUINED_WASTES_PORTAL_FRAME = new WastesPortalFrameBlock(FabricBlockSettings.create().mapColor(MapColor.DULL_RED).requiresTool().strength(0.8f).luminance(state -> state.get(Properties.POWERED) ? 13 : 0).nonOpaque());
	public static final Block RUINED_WASTES_PORTAL_CONTROLLER = new RuinedWastesPortalControllerBlock(FabricBlockSettings.create().mapColor(MapColor.DULL_RED).requiresTool().strength(0.8f));
	public static final Block WASTES_PORTAL_SURFACE = new WastesPortalSurfaceBlock(FabricBlockSettings.create().mapColor(MapColor.BLACK).noCollision().strength(-1.0f, 3600000.0f).dropsNothing());
	public static final Block TERRACOTTA_COAL_ORE = new ExperienceDroppingBlock(UniformIntProvider.create(0, 2), FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().strength(2.0f, 3.0f));
	public static final Block TERRACOTTA_IRON_ORE = new ExperienceDroppingBlock(ConstantIntProvider.create(0), FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().strength(2.0f, 3.0f));
	public static final Block TERRACOTTA_GOLD_ORE = new ExperienceDroppingBlock(ConstantIntProvider.create(0), FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().strength(2.0f, 3.0f));
	public static final Block TERRACOTTA_REDSTONE_ORE = new RedstoneOreBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().ticksRandomly().luminance(state -> state.get(Properties.LIT) ? 9 : 0).strength(2.0f, 3.0f));
	public static final Block TERRACOTTA_DIAMOND_ORE = new ExperienceDroppingBlock(UniformIntProvider.create(3, 7), FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().strength(2.0f, 3.0f));
	public static final Block TERRACOTTA_COPPER_ORE = new ExperienceDroppingBlock(ConstantIntProvider.create(0), FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).requiresTool().strength(2.0f, 3.0f));
	public static final Block WASTES_STONE_BRICKS = new Block(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_PINK).requiresTool().strength(50.0f, 1200.0f));
	public static final Block WASTES_STONE_SLAB = new SlabBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_PINK).requiresTool().strength(50.0f, 1200.0f));
	public static final Block WASTES_STONE_STAIRS = new StairsBlock(WASTES_STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(WASTES_STONE_BRICKS));
	public static final Block WASTES_WHITE_STONE_BRICKS = new Block(FabricBlockSettings.create().mapColor(MapColor.WHITE).requiresTool().strength(50.0f, 1200.0f));
	public static final Block WASTES_WHITE_STONE_SLAB = new SlabBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE).requiresTool().strength(50.0f, 1200.0f));
	public static final Block WASTES_WHITE_STONE_STAIRS = new StairsBlock(WASTES_WHITE_STONE_BRICKS.getDefaultState(), AbstractBlock.Settings.copy(WASTES_WHITE_STONE_BRICKS));
	public static final Block WASTES_LAMP = new Block(FabricBlockSettings.create().mapColor(MapColor.ORANGE).requiresTool().strength(50.0f, 1200.0f).luminance(15));
	public static final Block WASTES_GLASS = new TransparentBlock(FabricBlockSettings.create().strength(50.0f, 1200.0f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never));

	public static final Item LETTUCE = new AliasedBlockItem(LETTUCE_BLOCK, new Item.Settings().food(LETTUCE_FOOD_COMPONENT));
	public static final Item MAYONNAISE = new MayonnaiseItem(new Item.Settings().food(MAYONNAISE_FOOD_COMPONENT).maxCount(1));

	public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR_SCREEN_HANDLER = new ScreenHandlerType<>(ExtractorScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<ClockBlockScreenHandler> CLOCK_BLOCK_SCREEN_HANDLER = new ScreenHandlerType<>(ClockBlockScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<CapacitorScreenHandler> CAPACITOR_SCREEN_HANDLER = new ScreenHandlerType<>(CapacitorScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<PumpScreenHandler> PUMP_SCREEN_HANDLER = new ScreenHandlerType<>(PumpScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<AbacusScreenHandler> ABACUS_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier("renew_auto_plus", "abacus_screen_handler"), new ExtendedScreenHandlerType<>(AbacusScreenHandler::new));
	public static final ScreenHandlerType<CrateScreenHandler> CRATE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier("renew_auto_plus", "crate_screen_handler"), new ExtendedScreenHandlerType<>(CrateScreenHandler::new));
	public static final ScreenHandlerType<WastesPortalControllerScreenHandler> WASTES_PORTAL_SCREEN_HANDLER = new ScreenHandlerType<>(WastesPortalControllerScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

	public static final BlockEntityType<FurnaceBlockEntityReplacement> FURNACE_BLOCK_ENTITY_REPLACEMENT = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "furnace_entity_replacement"), BlockEntityType.Builder.create(FurnaceBlockEntityReplacement::new, Blocks.FURNACE, SMELTER).build());
	public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "extractor_block_entity"), BlockEntityType.Builder.create(ExtractorBlockEntity::new, EXTRACTOR).build());
	public static final BlockEntityType<ClockBlockEntity> CLOCK_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "clock_block_entity"), BlockEntityType.Builder.create(ClockBlockEntity::new, CLOCK_BLOCK).build());
	public static final BlockEntityType<CapacitorBlockEntity> CAPACITOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "capacitor_block_entity"), BlockEntityType.Builder.create(CapacitorBlockEntity::new, CAPACITOR).build());
	public static final BlockEntityType<PumpBlockEntity> PUMP_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "pump_block_entity"), BlockEntityType.Builder.create(PumpBlockEntity::new, PUMP).build());
	public static final BlockEntityType<CopperPipeBlockEntity> COPPER_PIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "copper_pipe_entity"), BlockEntityType.Builder.create(CopperPipeBlockEntity::new, COPPER_PIPE).build());
	public static final BlockEntityType<AbacusBlockEntity> ABACUS_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "abacus_entity"), BlockEntityType.Builder.create(AbacusBlockEntity::new, ABACUS).build());
	public static final BlockEntityType<StallBlockEntity> STALL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "stall_entity"), BlockEntityType.Builder.create(StallBlockEntity::new, STALL).build());
	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "crate_entity"), BlockEntityType.Builder.create(CrateBlockEntity::new, CRATE).build());
	public static final BlockEntityType<WastesPortalControllerBlockEntity> WASTES_PORTAL_CONTROLLER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "wastes_portal_controller_entity"), BlockEntityType.Builder.create(WastesPortalControllerBlockEntity::new, WASTES_PORTAL_CONTROLLER).build());
	public static final BlockEntityType<WastesPortalSurfaceBlockEntity> WASTES_PORTAL_SURFACE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("renew_auto_plus", "wastes_portal_surface_entity"), BlockEntityType.Builder.create(WastesPortalSurfaceBlockEntity::new, WASTES_PORTAL_SURFACE).build());

	public static final EntityType<AmethystBasicProjectileEntity> AMETHYST_BASIC_PROJECTILE_ENTITY =  Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "amethyst_basic_projectile"), EntityType.Builder.<AmethystBasicProjectileEntity>create(AmethystBasicProjectileEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).trackingTickInterval(20).maxTrackingRange(4).build());
	public static final EntityType<AquamarineBasicProjectileEntity> AQUAMARINE_BASIC_PROJECTILE_ENTITY =  Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "aquamarine_basic_projectile"), EntityType.Builder.<AquamarineBasicProjectileEntity>create(AquamarineBasicProjectileEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).trackingTickInterval(20).maxTrackingRange(4).build());
	public static final EntityType<AquamarineSpecialProjectileEntity> AQUAMARINE_SPECIAL_PROJECTILE_ENTITY =  Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "aquamarine_special_projectile"), EntityType.Builder.<AquamarineSpecialProjectileEntity>create(AquamarineSpecialProjectileEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).trackingTickInterval(20).maxTrackingRange(4).build());
	public static final EntityType<RubyBasicProjectileEntity> RUBY_BASIC_PROJECTILE_ENTITY =  Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "ruby_basic_projectile"), EntityType.Builder.<RubyBasicProjectileEntity>create(RubyBasicProjectileEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).trackingTickInterval(20).maxTrackingRange(4).build());
	public static final EntityType<OnyxBasicProjectileEntity> ONYX_BASIC_PROJECTILE_ENTITY =  Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "onyx_basic_projectile"), EntityType.Builder.<OnyxBasicProjectileEntity>create(OnyxBasicProjectileEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).trackingTickInterval(20).maxTrackingRange(4).build());

	public static final EntityType<BanishedEntity> BANISHED_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "banished_entity"), EntityType.Builder.create(BanishedEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).build());
	public static final Item BANISHED_SPAWN_EGG = new SpawnEggItem(BANISHED_ENTITY, 0xdfe0d9, 0x111111, new FabricItemSettings());
	public static final EntityType<GhostBanishedEntity> GHOST_BANISHED_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "ghost_banished_entity"), EntityType.Builder.create(GhostBanishedEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 1.95f).build());
	public static final Item GHOST_BANISHED_SPAWN_EGG = new SpawnEggItem(GHOST_BANISHED_ENTITY, 0xf05dec, 0x111111, new FabricItemSettings());
	public static final EntityType<FallenEntity> FALLEN_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "fallen_entity"), EntityType.Builder.create(FallenEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 2.65f).build());
	public static final Item FALLEN_SPAWN_EGG = new SpawnEggItem(FALLEN_ENTITY, 0x59ea63, 0xc87194, new FabricItemSettings());
	public static final EntityType<FrilledBasiliskEntity> FRILLED_BASILISK_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "frilled_basilisk_entity"), EntityType.Builder.create(FrilledBasiliskEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.6f).build());
	public static final Item FRILLED_BASILISK_SPAWN_EGG = new SpawnEggItem(FRILLED_BASILISK_ENTITY, 0x1c1b19, 0xdbdb18, new FabricItemSettings());
	public static final EntityType<VoidVillagerEntity> VOID_VILLAGER_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier("renew_auto_plus", "void_villager_entity"), EntityType.Builder.create(VoidVillagerEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 1.95f).build());
	public static final Item VOID_VILLAGER_SPAWN_EGG = new SpawnEggItem(VOID_VILLAGER_ENTITY, 0x1c1b19, 0xdbdb18, new FabricItemSettings());

	public static final PaintingVariant HEY_THERE = Registry.register(Registries.PAINTING_VARIANT, new Identifier("renew_auto_plus", "hey_there"), new PaintingVariant(64, 64));

	public static SoundEvent EXTRACTOR_ON_SE = SoundEvent.of(new Identifier("renew_auto_plus", "extractor_on"));

	public static final Identifier ABACUS_STRING_PACKET_ID = new Identifier("renew_auto_plus", "abacus_string_packet");
	public static final Identifier ABACUS_TRANSACT_PACKET_ID = new Identifier("renew_auto_plus", "abacus_transact_packet");
	public static final Identifier ABACUS_REMOVE_AUTO_TRADE_PACKET_ID = new Identifier("renew_auto_plus", "abacus_remove_auto_packet");
	public static final Identifier ABACUS_REMOVE_ATTACHED_CRATE_PACKET_ID = new Identifier("renew_auto_plus", "abacus_remove_crate_packet");
	public static final Identifier CRATE_STRING_PACKET_ID = new Identifier("renew_auto_plus", "crate_string_packet");
	public static final Identifier REPLACED_ON_ATTACK_PACKET_ID = new Identifier("renew_auto_plus", "replaced_on_attack");
	public static final Identifier REPLACED_STOP_ATTACK_PACKET_ID = new Identifier("renew_auto_plus", "replaced_stop_attack");

	public static final Identifier ABACUS_SCREEN_LIST_PACKET_ID = new Identifier("renew_auto_plus", "abacus_screen_list_packet"); //This is ass, why can't i get the definition accross

	public static final Identifier RUBY_WAND_SPECIAL_EFFECT = new Identifier("renew_auto_plus", "textures/entity/ruby_special_effect.png");

	public static final Identifier THE_WASTES = new Identifier("renew_auto_plus", "the_wastes");
	public static final RegistryKey<World> THE_WASTES_REGISTRY_KEY = RegistryKey.of(RegistryKeys.WORLD, THE_WASTES);

	public static final TrackedData<Boolean> IS_ICEBOUND = new TrackedData<Boolean>(60, TrackedDataHandlerRegistry.BOOLEAN); //Try and use 60 range for entire mod if needed again

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_shovel"), COPPER_SHOVEL);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_pickaxe"), COPPER_PICKAXE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_axe"), COPPER_AXE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_hoe"), COPPER_HOE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_sword"), COPPER_SWORD);

		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_sword"), BANISHED_SWORD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_sword_clear"), BANISHED_SWORD_CLEAR);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_parrying_sword"), BANISHED_PARRYING_SWORD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_parrying_sword_clear"), BANISHED_PARRYING_SWORD_CLEAR);

		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "amethyst_wand"), AMETHYST_WAND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "aquamarine_wand"), AQUAMARINE_WAND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "ruby_wand"), RUBY_WAND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "topaz_wand"), TOPAZ_WAND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "onyx_wand"), ONYX_WAND);

		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_helmet"), BANISHED_HELMET);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_chestplate"), BANISHED_CHESTPLATE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_leggings"), BANISHED_LEGGINGS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_boots"), BANISHED_BOOTS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_helmet"), COPPER_HELMET);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_chestplate"), COPPER_CHESTPLATE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_leggings"), COPPER_LEGGINGS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_boots"), COPPER_BOOTS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "linen_hood"), LINEN_HOOD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "linen_robe"), LINEN_ROBE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "linen_leggings"), LINEN_LEGGINGS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "linen_boots"), LINEN_BOOTS);

		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "raw_diamond"), RAW_DIAMOND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "spice"), SPICE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "linen"), LINEN);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "mayonnaise"), MAYONNAISE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "aquamarine_shard"), AQUAMARINE_SHARD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "onyx_shard"), ONYX_SHARD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "ruby_shard"), RUBY_SHARD);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "topaz_shard"), TOPAZ_SHARD);

		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "smelter"), SMELTER);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "smelter"), new BlockItem(SMELTER, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "extractor"), EXTRACTOR);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "extractor"), new BlockItem(EXTRACTOR, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "clock_block"), CLOCK_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "clock_block"), new BlockItem(CLOCK_BLOCK, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "capacitor"), CAPACITOR);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "capacitor"), new BlockItem(CAPACITOR, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "pump"), PUMP);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "pump"), new BlockItem(PUMP, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "lettuce"), LETTUCE_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "lettuce"), LETTUCE);
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "copper_pipe"), COPPER_PIPE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "copper_pipe"), new BlockItem(COPPER_PIPE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "abacus"), ABACUS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "abacus"), new BlockItem(ABACUS, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "stall"), STALL);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "stall"), new BlockItem(STALL, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "crate"), CRATE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "crate"), new BlockItem(CRATE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "white_sand"), WHITE_SAND);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "white_sand"), new BlockItem(WHITE_SAND, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "white_sandstone"), WHITE_SANDSTONE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "white_sandstone"), new BlockItem(WHITE_SANDSTONE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_ruin"), WASTES_RUIN);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_ruin"), new BlockItem(WASTES_RUIN, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_portal_frame"), WASTES_PORTAL_FRAME);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_portal_frame"), new BlockItem(WASTES_PORTAL_FRAME, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_portal_controller"), WASTES_PORTAL_CONTROLLER);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_portal_controller"), new BlockItem(WASTES_PORTAL_CONTROLLER, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "ruined_wastes_portal_frame"), RUINED_WASTES_PORTAL_FRAME);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "ruined_wastes_portal_frame"), new BlockItem(RUINED_WASTES_PORTAL_FRAME, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "ruined_wastes_portal_controller"), RUINED_WASTES_PORTAL_CONTROLLER);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "ruined_wastes_portal_controller"), new BlockItem(RUINED_WASTES_PORTAL_CONTROLLER, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_portal_surface"), WASTES_PORTAL_SURFACE);
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_coal_ore"), TERRACOTTA_COAL_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_coal_ore"), new BlockItem(TERRACOTTA_COAL_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_iron_ore"), TERRACOTTA_IRON_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_iron_ore"), new BlockItem(TERRACOTTA_IRON_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_gold_ore"), TERRACOTTA_GOLD_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_gold_ore"), new BlockItem(TERRACOTTA_GOLD_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_redstone_ore"), TERRACOTTA_REDSTONE_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_redstone_ore"), new BlockItem(TERRACOTTA_REDSTONE_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_diamond_ore"), TERRACOTTA_DIAMOND_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_diamond_ore"), new BlockItem(TERRACOTTA_DIAMOND_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "terracotta_copper_ore"), TERRACOTTA_COPPER_ORE);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "terracotta_copper_ore"), new BlockItem(TERRACOTTA_COPPER_ORE, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_stone_bricks"), WASTES_STONE_BRICKS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_stone_bricks"), new BlockItem(WASTES_STONE_BRICKS, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_stone_stairs"), WASTES_STONE_STAIRS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_stone_stairs"), new BlockItem(WASTES_STONE_STAIRS, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_stone_slab"), WASTES_STONE_SLAB);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_stone_slab"), new BlockItem(WASTES_STONE_SLAB, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_white_stone_bricks"), WASTES_WHITE_STONE_BRICKS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_white_stone_bricks"), new BlockItem(WASTES_WHITE_STONE_BRICKS, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_white_stone_stairs"), WASTES_WHITE_STONE_STAIRS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_white_stone_stairs"), new BlockItem(WASTES_WHITE_STONE_STAIRS, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_white_stone_slab"), WASTES_WHITE_STONE_SLAB);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_white_stone_slab"), new BlockItem(WASTES_WHITE_STONE_SLAB, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_lamp"), WASTES_LAMP);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_lamp"), new BlockItem(WASTES_LAMP, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier("renew_auto_plus", "wastes_glass"), WASTES_GLASS);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "wastes_glass"), new BlockItem(WASTES_GLASS, new Item.Settings()));

		FabricDefaultAttributeRegistry.register(BANISHED_ENTITY, BanishedEntity.createMobAttributes());
		SpawnRestriction.register(BANISHED_ENTITY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BanishedEntity::canSpawn);
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "banished_spawn_egg"), BANISHED_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(GHOST_BANISHED_ENTITY, GhostBanishedEntity.createMobAttributes());
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "ghost_banished_spawn_egg"), GHOST_BANISHED_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(FALLEN_ENTITY, FallenEntity.createMobAttributes());
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "fallen_spawn_egg"), FALLEN_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(FRILLED_BASILISK_ENTITY, FrilledBasiliskEntity.createMobAttributes());
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "frilled_basilisk_spawn_egg"), FRILLED_BASILISK_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(VOID_VILLAGER_ENTITY, VoidVillagerEntity.createMobAttributes());
		Registry.register(Registries.ITEM, new Identifier("renew_auto_plus", "void_villager_spawn_egg"), VOID_VILLAGER_SPAWN_EGG);

		Registry.register(Registries.SOUND_EVENT, new Identifier("renew_auto_plus", "extractor_on"), EXTRACTOR_ON_SE);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> { content.add(COPPER_AXE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> { content.add(COPPER_HOE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> { content.add(COPPER_PICKAXE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> { content.add(COPPER_SHOVEL); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(COPPER_SWORD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_SWORD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_SWORD_CLEAR); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_PARRYING_SWORD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_PARRYING_SWORD_CLEAR); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(AMETHYST_WAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(AQUAMARINE_WAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(RUBY_WAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(TOPAZ_WAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(ONYX_WAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_HELMET); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_CHESTPLATE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_LEGGINGS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(BANISHED_BOOTS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(COPPER_HELMET); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(COPPER_CHESTPLATE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(COPPER_LEGGINGS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(COPPER_BOOTS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(LINEN_HOOD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(LINEN_ROBE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(LINEN_LEGGINGS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> { content.add(LINEN_BOOTS); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(RAW_DIAMOND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(SPICE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(LINEN); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(AQUAMARINE_SHARD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(ONYX_SHARD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(RUBY_SHARD); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> { content.add(TOPAZ_SHARD); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> { content.add(BANISHED_SPAWN_EGG); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> { content.add(FALLEN_SPAWN_EGG); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> { content.add(GHOST_BANISHED_SPAWN_EGG); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> { content.add(FRILLED_BASILISK_SPAWN_EGG); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> { content.add(VOID_VILLAGER_SPAWN_EGG); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> { content.add(LETTUCE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> { content.add(MAYONNAISE); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> { content.add(EXTRACTOR); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> { content.add(CLOCK_BLOCK); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> { content.add(CAPACITOR); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> { content.add(PUMP); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> { content.add(COPPER_PIPE); });
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(SMELTER); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(ABACUS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(STALL); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(CRATE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(WASTES_PORTAL_FRAME); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(WASTES_PORTAL_CONTROLLER); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(RUINED_WASTES_PORTAL_FRAME); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(RUINED_WASTES_PORTAL_CONTROLLER); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_COAL_ORE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_COPPER_ORE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_DIAMOND_ORE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_GOLD_ORE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_IRON_ORE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> { content.add(TERRACOTTA_REDSTONE_ORE); });

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WHITE_SAND); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WHITE_SANDSTONE); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_STONE_BRICKS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_STONE_SLAB); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_STONE_STAIRS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_WHITE_STONE_BRICKS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_WHITE_STONE_SLAB); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_WHITE_STONE_STAIRS); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_LAMP); });
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> { content.add(WASTES_GLASS); });

		registerServerPackets();

		System.out.println("RenewAutoPlus is loaded.");
	}

	public void registerServerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_STRING_PACKET_ID, new StringToAbacusPlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_TRANSACT_PACKET_ID, new TransactStallTradePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_REMOVE_AUTO_TRADE_PACKET_ID, new RemoveAutoTradePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(ABACUS_REMOVE_ATTACHED_CRATE_PACKET_ID, new RemoveAttachedCratePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(CRATE_STRING_PACKET_ID, new StringToCratePlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(REPLACED_ON_ATTACK_PACKET_ID, new ReplacedOnAttackPlayChannelHandler());
		ServerPlayNetworking.registerGlobalReceiver(REPLACED_STOP_ATTACK_PACKET_ID, new ReplacedStopAttackPlayChannelHandler());
	}
}
