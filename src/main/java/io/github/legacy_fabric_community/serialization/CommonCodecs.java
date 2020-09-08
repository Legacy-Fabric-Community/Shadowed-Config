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

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
public interface CommonCodecs {
    Codec<BlockPos> BLOCK_POS = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("x").forGetter(BlockPos::getX),
                Codec.INT.fieldOf("y").forGetter(BlockPos::getY),
                Codec.INT.fieldOf("z").forGetter(BlockPos::getZ)
        ).apply(instance, BlockPos::new);
    });

    Codec<Vec2f> VEC_2_F = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("u").forGetter((vec) -> {
            return vec.x;
        }), Codec.FLOAT.fieldOf("v").forGetter((vec) -> {
            return vec.y;
        })).apply(instance, Vec2f::new);
    });

    Codec<Vec3d> VEC_3_D = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("x").forGetter((pos) -> {
            return pos.x;
        }), Codec.DOUBLE.fieldOf("y").forGetter((pos) -> {
            return pos.y;
        }), Codec.DOUBLE.fieldOf("z").forGetter((pos) -> {
            return pos.z;
        })).apply(instance, Vec3d::new);
    });

    Codec<Identifier> IDENTIFIER = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("namespace").forGetter((identifier) -> {
            return identifier.toString().split(":")[0];
        }), Codec.STRING.fieldOf("path").forGetter((identifier) -> {
            return identifier.getPath();
        })).apply(instance, (s, s2) -> new Identifier(0, s, s2){});
    });

    Codec<CompoundTag> COMPOUND_TAG = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        return tag instanceof CompoundTag ? DataResult.success((CompoundTag)tag) : DataResult.error("Not a Compound Tag: " + tag);
    }, (compoundTag) -> {
        return new Dynamic<>(NbtOps.INSTANCE, compoundTag);
    });
}
