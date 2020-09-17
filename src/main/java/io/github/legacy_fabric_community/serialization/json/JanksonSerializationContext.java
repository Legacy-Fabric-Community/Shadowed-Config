package io.github.legacy_fabric_community.serialization.json;

import java.lang.reflect.Type;

import blue.endless.jankson.JsonElement;

public interface JanksonSerializationContext {
    JsonElement serialize(Object src);

    JsonElement serialize(Object src, Type type);
}
