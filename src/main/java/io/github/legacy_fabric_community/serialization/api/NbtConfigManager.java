package io.github.legacy_fabric_community.serialization.api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import io.github.legacy_fabric_community.serialization.nbt.NbtOps;
import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class NbtConfigManager<T> extends ConfigManager<T> {
    @Nonnull
    private final T defaultValue;

    protected NbtConfigManager(Path configPath, Codec<T> codec, @Nonnull T defaultValue) {
        super(configPath, codec);
        this.defaultValue = defaultValue;
    }

    @Override
    public void serialize() throws IOException {
        NbtIo.writeCompressed(
                (CompoundTag) this.codec.encodeStart(
                        NbtOps.INSTANCE,
                        this.config
                ).getOrThrow(false,
                        PRINT_TO_STDERR
                ),
                new FileOutputStream(this.configPath.toFile())
        );
    }

    @Override
    public void deserialize() throws IOException {
        this.config = this.codec.decode(
                NbtOps.INSTANCE,
                NbtIo.readCompressed(
                        new FileInputStream(this.configPath.toFile())
                )
        ).getOrThrow(
                false,
                PRINT_TO_STDERR
        ).getFirst();
    }

    @Override
    protected void writeDefaultData() throws IOException {
        NbtIo.writeCompressed(
                (CompoundTag) this.codec.encodeStart(
                        NbtOps.INSTANCE,
                        this.defaultValue
                ).getOrThrow(false,
                        PRINT_TO_STDERR
                ),
                new FileOutputStream(this.configPath.toFile())
        );
    }

    @Nonnull
    public T getDefaultValue() {
        return this.defaultValue;
    }
}
