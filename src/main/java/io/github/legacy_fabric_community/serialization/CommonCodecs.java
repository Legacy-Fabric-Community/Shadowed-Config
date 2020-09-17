package io.github.legacy_fabric_community.serialization;

import io.github.legacy_fabric_community.serialization.math.Vec2f;
import io.github.legacy_fabric_community.serialization.nbt.NbtOps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface CommonCodecs {
    Codec<BlockPos> BLOCK_POS = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("x").forGetter(BlockPos::getX),
            Codec.INT.fieldOf("y").forGetter(BlockPos::getY),
            Codec.INT.fieldOf("z").forGetter(BlockPos::getZ)
    ).apply(instance, BlockPos::new));

    Codec<Vec2f> VEC_2_F = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.FLOAT.fieldOf("u").forGetter((vec) -> vec.x),
            Codec.FLOAT.fieldOf("v").forGetter((vec) -> vec.y)
    ).apply(instance, Vec2f::new));

    Codec<Vec3d> VEC_3_D = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter((vec) -> vec.x),
            Codec.DOUBLE.fieldOf("y").forGetter((vec) -> vec.y),
            Codec.DOUBLE.fieldOf("z").forGetter((vec) -> vec.z)
    ).apply(instance, Vec3d::new));

    Codec<Identifier> IDENTIFIER = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("namespace").forGetter((identifier) -> identifier.toString().split(":")[0]),
            Codec.STRING.fieldOf("path").forGetter(Identifier::getPath)
    ).apply(instance, Identifier::new));

    Codec<CompoundTag> COMPOUND_TAG = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        return tag instanceof CompoundTag ? DataResult.success((CompoundTag) tag) : DataResult.error("Not a Compound Tag: " + tag);
    }, (compoundTag) -> new Dynamic<>(NbtOps.INSTANCE, compoundTag));
}
