package io.github.legacy_fabric_community.serialization.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
import io.github.legacy_fabric_community.serialization.json.JanksonOps;
import com.mojang.serialization.Codec;

public class JanksonConfigManager<T> extends ConfigManager<T> {
    private static final Jankson JANKSON = Jankson.builder().build();
    @Nullable
    private DummyConfig dummyValueClazz = null;
    @Nullable
    private String defaultValue = null;

    protected JanksonConfigManager(Path configPath, Codec<T> codec) {
        super(configPath, codec);
    }

    protected JanksonConfigManager(Path configPath, Codec<T> codec, @Nonnull DummyConfig dummyValueClazz) {
        this(configPath, codec);
        this.dummyValueClazz = Objects.requireNonNull(dummyValueClazz);
    }

    protected JanksonConfigManager(Path configPath, Codec<T> codec, @Nonnull String defaultValue) {
        this(configPath, codec);
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    @Override
    public void serialize() throws IOException {
        Files.write(
                this.configPath,
                this.codec.encodeStart(
                        JanksonOps.INSTANCE,
                        this.config
                )
                        .getOrThrow(
                        false,
                        PRINT_TO_STDERR
                        )
                        .toJson(
                                true,
                                true
                        )
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public void deserialize() throws IOException {
        try {
            this.config = this.codec.decode(JanksonOps.INSTANCE, JANKSON.load(this.configPath.toFile())).getOrThrow(false, PRINT_TO_STDERR).getFirst();
        } catch (SyntaxError syntaxError) {
            throw new IOException(syntaxError);
        }
    }

    @Override
    protected void writeDefaultData() throws IOException {
        byte[] bytes = "{}".getBytes(StandardCharsets.UTF_8);
        if (this.dummyValueClazz != null) {
            bytes = JANKSON.toJson(this.dummyValueClazz).toJson(true, true).getBytes(StandardCharsets.UTF_8);
        } else if (this.defaultValue != null) {
            bytes = this.defaultValue.getBytes(StandardCharsets.UTF_8);
        }
        Files.write(this.configPath, bytes);
    }

    public void serialize(T config) throws IOException {
        Files.write(this.configPath, this.getCodec().encodeStart(JanksonOps.INSTANCE, config).getOrThrow(false, PRINT_TO_STDERR).toJson().getBytes(StandardCharsets.UTF_8));
    }
}
