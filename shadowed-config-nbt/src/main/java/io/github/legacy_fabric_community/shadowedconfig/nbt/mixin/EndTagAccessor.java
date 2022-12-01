package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtEnd;

@Mixin(NbtEnd.class)
public interface EndTagAccessor {
	@Invoker("<init>")
	static NbtEnd create() {
		throw new AssertionError();
	}
}
