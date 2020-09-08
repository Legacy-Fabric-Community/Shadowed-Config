package io.github.legacy_fabric_community.serialization.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.Tag;

@Mixin(Tag.class)
public interface TagAccessor {
    @Invoker
    String invokeAsString();
}
