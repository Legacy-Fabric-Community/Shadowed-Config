package io.github.legacy_fabric_community.serialization.json;

import java.lang.reflect.Type;

import blue.endless.jankson.JsonElement;

public interface JanksonDeserializationContext {
    <T> T deserialize(JsonElement json, Type type) throws IllegalStateException;
}
