package io.github.legacy_fabric_community.serialization.codec;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class VanillaRegistryCodecs {
    public static final Codec<Block> BLOCK = (Codec<Block>) Block.REGISTRY;
    public static final Codec<Item> ITEM = (Codec<Item>) Item.REGISTRY;
}
