package io.github.legacy_fabric_community.serialization.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Mixin(CompoundTag.class)
public interface CompoundTagAccessor {
    @Accessor
    Map<String, Tag> getData();

    @Accessor
    void setData(Map<String, Tag> data);
}
