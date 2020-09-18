package io.github.legacy_fabric_community.serialization.api;

import java.io.IOException;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;

public class NbtConfigManager<T> extends ConfigManager<T> {
    @Nonnull
    private final T defaultValue;

    protected NbtConfigManager(Path configPath, Codec<T> codec, @Nonnull T defaultValue) {
        super(configPath, codec);
        this.defaultValue = defaultValue;
    }

    @Override
    public void serialize() throws IOException {

    }

    @Override
    public void deserialize() throws IOException {

    }

    @Override
    protected void writeDefaultData() throws IOException {

    }

    @Nonnull
    public T getDefaultValue() {
        return this.defaultValue;
    }
}
