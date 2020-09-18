package io.github.legacy_fabric_community.serialization.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

public abstract class ConfigManager<T> {
    protected static final Consumer<String> PRINT_TO_STDERR = System.err::println;
    protected final Path configPath;
    protected final Codec<T> codec;
    protected T config;

    protected ConfigManager(Path configPath, Codec<T> codec) {
        this.configPath = configPath;
        this.codec = codec;
        this.check();
        this.deserializeQuietly();
    }

    public final Path getConfigPath() {
        return this.configPath;
    }

    public final Codec<T> getCodec() {
        return this.codec;
    }

    public final T getConfig() {
        return this.config;
    }

    public abstract void serialize() throws IOException;

    public abstract void deserialize() throws IOException;

    protected abstract void writeDefaultData() throws IOException;

    public void serializeQuietly() {
        try {
            this.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeQuietly() {
        try {
            this.deserialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void check() {
        try {
            if (Files.isDirectory(this.configPath)) {
                Files.delete(this.configPath);
            }
            if (!Files.exists(this.configPath)) {
                Files.createFile(this.configPath);
                this.writeDefaultData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> ConfigManager<T> jankson(Path configPath, Codec<T> codec) {
        return new JanksonConfigManager<>(configPath, codec);
    }

    public static <T> ConfigManager<T> jankson(Path configPath, Codec<T> codec, DummyConfig dummy) {
        return new JanksonConfigManager<>(configPath, codec, dummy);
    }

    public static <T> ConfigManager<T> jankson(Path configPath, Codec<T> codec, String defaultValue) {
        return new JanksonConfigManager<>(configPath, codec, defaultValue);
    }
}
