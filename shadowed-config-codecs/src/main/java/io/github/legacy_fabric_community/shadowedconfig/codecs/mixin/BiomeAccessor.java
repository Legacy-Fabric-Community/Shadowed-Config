package io.github.legacy_fabric_community.shadowedconfig.codecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.Biome;

@Mixin(Biome.class)
public interface BiomeAccessor {
	@Accessor("BIOMES")
	static Biome[] getBiomes() {
		throw new AssertionError();
	}
}
