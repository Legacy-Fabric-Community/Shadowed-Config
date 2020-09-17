package io.github.legacy_fabric_community.serialization.json;


import blue.endless.jankson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface JanksonSerializer<T> {
    void toJson(JsonObject jsonObject, T t, JsonSerializationContext context);

    T fromJson(JsonObject jsonObject, JanksonDeserializationContext context);
}
