package io.github.legacy_fabric_community.shadowedconfig.codecs;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.legacy_fabric_community.shadowedconfig.codecs.mixin.BiomeAccessor;
import io.github.legacy_fabric_community.shadowedconfig.codecs.mixin.BlockEntityAccessor;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import net.fabricmc.fabric.mixin.content.registries.EntityTypeAccessor;

// TODO: xmap instead of comapFlatMap for most stuff that cant go wrong
public interface StringMappedCodecs {
	Codec<Identifier> IDENTIFIER = Codec.STRING.comapFlatMap(Converters::toIdentifier, Identifier::toString);
	Codec<Item> ITEM = IDENTIFIER.comapFlatMap(Converters::toItem, Item.REGISTRY::getIdentifier);
	Codec<Block> BLOCK = IDENTIFIER.comapFlatMap(Converters::toBlock, Block.REGISTRY::getIdentifier);
	Codec<Class<? extends Entity>> ENTITY = Codec.STRING.comapFlatMap(Converters::toEntity, EntityTypeAccessor.getClassNameMap()::get);
	Codec<Class<? extends BlockEntity>> BLOCK_ENTITY = Codec.STRING.comapFlatMap(Converters::toBlockEntity, Converters.BE_CLASS_STRING_MAP::get);
	Codec<Biome> BIOME = Codec.STRING.comapFlatMap(Converters::toBiome, Converters.BIOME_OBJECT_STRING_MAP::get);
	Codec<ServerWorld> WORLD = Codec.STRING.comapFlatMap(Converters::toWorld, Converters::fromWorld);
	Codec<Character> CHARACTER = Codec.STRING.comapFlatMap(Converters::toCharacter, String::valueOf);

	class Converters {
		private static final Map<Class<? extends BlockEntity>, String> BE_CLASS_STRING_MAP = BlockEntityAccessor.getClassStringMap();
		private static final Map<String, Class<? extends BlockEntity>> BE_STRING_CLASS_MAP = BlockEntityAccessor.getStringClassMap();
		private static final BiMap<Biome, String> BIOME_OBJECT_STRING_MAP = HashBiMap.create();
		private static final BiMap<String, Dimension> DIM_STRING_OBJECT_MAP = HashBiMap.create();
		private static final BiMap<Dimension, ServerWorld> DIM_OBJECT_WORLD_MAP = HashBiMap.create();

		private static String fromWorld(ServerWorld world) {
			return DIM_STRING_OBJECT_MAP.inverse().get(DIM_OBJECT_WORLD_MAP.inverse().get(world));
		}

		private static DataResult<Identifier> toIdentifier(String s) {
			return DataResult.success(new Identifier(s));
		}

		private static DataResult<Item> toItem(Identifier id) {
			return DataResult.success(Objects.requireNonNull(Item.REGISTRY.get(id)));
		}

		private static DataResult<Block> toBlock(Identifier id) {
			return DataResult.success(Objects.requireNonNull(Block.REGISTRY.get(id)));
		}

		private static DataResult<Class<? extends Entity>> toEntity(String name) {
			return DataResult.success(Objects.requireNonNull(EntityTypeAccessor.getNameClassMap().get(name)));
		}

		private static DataResult<Class<? extends BlockEntity>> toBlockEntity(String name) {
			return DataResult.success(Objects.requireNonNull(BE_STRING_CLASS_MAP.get(name)));
		}

		private static DataResult<Biome> toBiome(String name) {
			return DataResult.success(Objects.requireNonNull(BIOME_OBJECT_STRING_MAP.inverse().get(name)));
		}

		private static DataResult<ServerWorld> toWorld(String name) {
			return DataResult.success(Objects.requireNonNull(DIM_OBJECT_WORLD_MAP.get(DIM_STRING_OBJECT_MAP.get(name))));
		}

		private static DataResult<Character> toCharacter(String name) {
			return DataResult.success(String.valueOf(name).charAt(0));
		}

		static {
			for (Biome biome : BiomeAccessor.getBiomes()) {
				BIOME_OBJECT_STRING_MAP.put(biome, biome.name);
			}
			for (ServerWorld world : MinecraftServer.getServer().worlds) {
				DIM_STRING_OBJECT_MAP.put(world.dimension.getName(), world.dimension);
				DIM_OBJECT_WORLD_MAP.put(world.dimension, world);
			}
		}
	}
}
