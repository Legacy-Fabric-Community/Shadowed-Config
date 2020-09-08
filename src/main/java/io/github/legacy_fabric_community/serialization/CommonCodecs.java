package io.github.legacy_fabric_community.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
}
