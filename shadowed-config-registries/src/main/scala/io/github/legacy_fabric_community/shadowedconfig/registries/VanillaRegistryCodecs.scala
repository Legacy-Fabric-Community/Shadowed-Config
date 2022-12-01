package io.github.legacy_fabric_community.shadowedconfig.registries

import com.mojang.serialization.Codec
import net.minecraft.block.Block
import net.minecraft.item.Item

@SuppressWarnings(Array("unchecked")) object VanillaRegistryCodecs {
	val BLOCK: Codec[Block] = Block.REGISTRY.asInstanceOf[Codec[Block]]
	val ITEM: Codec[Item] = Item.REGISTRY.asInstanceOf[Codec[Item]]
}
