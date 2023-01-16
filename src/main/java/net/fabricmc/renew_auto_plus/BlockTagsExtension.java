package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class BlockTagsExtension {
    public static final Tag<Block> NEEDS_COPPER_TOOL = TagFactory.BLOCK.create(new Identifier("renew_auto_plus", "needs_copper_tool"));
    public static final Tag<Block> NEEDS_GOLD_TOOL = TagFactory.BLOCK.create(new Identifier("renew_auto_plus", "needs_gold_tool"));
}
