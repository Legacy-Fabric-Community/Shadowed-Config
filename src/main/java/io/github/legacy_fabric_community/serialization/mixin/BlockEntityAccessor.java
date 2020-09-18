package io.github.legacy_fabric_community.serialization.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
    @Accessor
    static Map<String, Class<? extends BlockEntity>> getStringClassMap() {
        throw new AssertionError();
    }

    @Accessor
    static Map<Class<? extends BlockEntity>, String> getClassStringMap() {
        throw new AssertionError();
    }
}