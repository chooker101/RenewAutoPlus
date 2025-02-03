package net.renew_auto_plus.extension;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class BlockTagsExtension {
    public static final TagKey<Block> NEEDS_COPPER_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("renew_auto_plus", "needs_copper_tool"));
    public static final TagKey<Block> NEEDS_GOLD_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("renew_auto_plus", "needs_gold_tool"));
}
