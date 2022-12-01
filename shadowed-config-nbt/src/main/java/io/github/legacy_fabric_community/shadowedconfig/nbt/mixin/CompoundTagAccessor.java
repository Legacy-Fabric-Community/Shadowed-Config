package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

@Mixin(NbtCompound.class)
public interface CompoundTagAccessor {
	@Accessor
	Map<String, NbtElement> getData();

	@Accessor
	void setData(Map<String, NbtElement> data);
}
