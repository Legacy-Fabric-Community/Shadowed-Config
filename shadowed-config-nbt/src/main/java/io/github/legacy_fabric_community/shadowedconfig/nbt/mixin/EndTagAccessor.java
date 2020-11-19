package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.EndTag;

@Mixin(EndTag.class)
public interface EndTagAccessor {
	@Invoker("<init>")
	static EndTag create() {
		throw new AssertionError();
	}
}
