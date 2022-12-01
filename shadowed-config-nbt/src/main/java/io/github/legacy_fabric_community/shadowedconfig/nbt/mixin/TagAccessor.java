package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtElement;

@Mixin(NbtElement.class)
public interface TagAccessor {
	@Invoker
	String invokeAsString();
}
