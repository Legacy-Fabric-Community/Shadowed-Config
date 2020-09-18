package io.github.legacy_fabric_community.serialization.codec;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface VanillaRegistryCodecs {
    Codec<Block> BLOCK = (Codec<Block>) Block.REGISTRY;
    Codec<Item> ITEM = (Codec<Item>) Item.REGISTRY;
}
