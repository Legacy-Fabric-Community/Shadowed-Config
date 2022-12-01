package io.github.legacy_fabric_community.shadowedconfig.codecs

import java.util
import java.util.Objects
import com.google.common.collect.HashBiMap
import com.mojang.serialization.{Codec, DataResult}
import io.github.legacy_fabric_community.shadowedconfig.codecs.mixin.{BiomeAccessor, BlockEntityAccessor}
import net.legacyfabric.fabric.mixin.registry.sync.EntityTypeAccessor
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import net.minecraft.world.dimension.Dimension

// TODO: xmap instead of comapFlatMap for most stuff that cant go wrong
object StringMappedCodecs {
	val identifier: Codec[Identifier] = Codec.STRING.comapFlatMap((s: String) => Converters.toIdentifier(s), (identifier: Identifier) => identifier.toString)
	val item: Codec[Item] = identifier.comapFlatMap((id: Identifier) => Converters.toItem(id), (`object`: Item) => Item.REGISTRY.getIdentifier(`object`))
	val block: Codec[Block] = identifier.comapFlatMap((id: Identifier) => Converters.toBlock(id), (`object`: Block) => Block.REGISTRY.getIdentifier(`object`))
	val entity: Codec[Class[_$1]] forSome {type _$1 <: Entity} = Codec.STRING.comapFlatMap((name: String) => Converters.toEntity(name), (key: Class[_ <: Entity]) => EntityTypeAccessor.getClassNameMap.get(key))
	val blockEntity: Codec[Class[_$1]] forSome {type _$1 <: BlockEntity} = Codec.STRING.comapFlatMap((name: String) => Converters.toBlockEntity(name), (key: Class[_ <: BlockEntity]) => Converters.BE_CLASS_STRING_MAP.get(key))
	val biome: Codec[Biome] = Codec.STRING.comapFlatMap((name: String) => Converters.toBiome(name), (key: Biome) => Converters.BIOME_OBJECT_STRING_MAP.get(key))
	val world: Codec[ServerWorld] = Codec.STRING.comapFlatMap((name: String) => Converters.toWorld(name), (world: ServerWorld) => Converters.fromWorld(world))
	val character: Codec[Character] = Codec.STRING.comapFlatMap((name: String) => Converters.toCharacter(name), (obj: Character) => String.valueOf(obj))

	private object Converters {
		val BE_CLASS_STRING_MAP: util.Map[Class[_$1], String] forSome {type _$1 <: BlockEntity} = BlockEntityAccessor.getClassStringMap
		val BE_STRING_CLASS_MAP: util.Map[String, Class[_$1]] forSome {type _$1 <: BlockEntity} = BlockEntityAccessor.getStringClassMap
		val BIOME_OBJECT_STRING_MAP: HashBiMap[Biome, String] = HashBiMap.create
		val DIM_STRING_OBJECT_MAP: HashBiMap[String, Dimension] = HashBiMap.create
		val DIM_OBJECT_WORLD_MAP: HashBiMap[Dimension, ServerWorld] = HashBiMap.create

		def fromWorld(world: ServerWorld): String = DIM_STRING_OBJECT_MAP.inverse.get(DIM_OBJECT_WORLD_MAP.inverse.get(world))

		def toIdentifier(s: String): DataResult[Identifier] = DataResult.success(new Identifier(s))

		def toItem(id: Identifier): DataResult[Item] = DataResult.success(Objects.requireNonNull(Item.REGISTRY.get(id)))

		def toBlock(id: Identifier): DataResult[Block] = DataResult.success(Objects.requireNonNull(Block.REGISTRY.get(id)))

		def toEntity(name: String): DataResult[Class[_ <: Entity]] = DataResult.success(Objects.requireNonNull(EntityTypeAccessor.getNAME_CLASS_MAP.get(name)))

		def toBlockEntity(name: String): DataResult[Class[_ <: BlockEntity]] = DataResult.success(Objects.requireNonNull(BE_STRING_CLASS_MAP.get(name)))

		def toBiome(name: String): DataResult[Biome] = DataResult.success(Objects.requireNonNull(BIOME_OBJECT_STRING_MAP.inverse.get(name)))

		def toWorld(name: String): DataResult[ServerWorld] = DataResult.success(Objects.requireNonNull(DIM_OBJECT_WORLD_MAP.get(DIM_STRING_OBJECT_MAP.get(name))))

		def toCharacter(name: String): DataResult[Character] = DataResult.success(String.valueOf(name).charAt(0))

		try for (biome <- BiomeAccessor.getBiomes) {
			BIOME_OBJECT_STRING_MAP.put(biome, biome.name)
		}
		for (world <- MinecraftServer.getServer.worlds) {
			DIM_STRING_OBJECT_MAP.put(world.dimension.getName, world.dimension)
			DIM_OBJECT_WORLD_MAP.put(world.dimension, world)
		}
	}
}
