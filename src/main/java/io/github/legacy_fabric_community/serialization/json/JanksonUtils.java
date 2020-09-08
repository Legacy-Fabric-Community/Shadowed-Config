package io.github.legacy_fabric_community.serialization.json;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;

import java.math.BigDecimal;

import com.google.gson.internal.LazilyParsedNumber;

public final class JanksonUtils {
    private JanksonUtils() {
    }

    public static JsonPrimitive getAsJsonPrimitive(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            return (JsonPrimitive) input;
        }
        throw new IllegalStateException("Not a Json Primitive: " + input);
    }

    public static JsonArray getAsJsonArray(JsonElement input) {
        if (input instanceof JsonArray) {
            return (JsonArray) input;
        }
        throw new IllegalStateException("Not a Json Array: " + input);
    }

    public static JsonObject getAsJsonObject(JsonElement input) {
        if (input instanceof JsonObject) {
            return (JsonObject) input;
        }
        throw new IllegalStateException("Not a Json Object: " + input);
    }

    public static BigDecimal getAsBigDecimal(JsonPrimitive value) {
        return value.getValue() instanceof BigDecimal ? (BigDecimal) value.getValue() : new BigDecimal(value.getValue().toString());
    }

    public static Number getAsNumber(JsonPrimitive value) {
        return value.getValue() instanceof String ? new LazilyParsedNumber((String) value.getValue()) : (Number) value.getValue();
    }

    public static boolean getAsBoolean(JsonPrimitive value) {
        if (value.getValue() instanceof Boolean) {
            return (Boolean) value.getValue();
        } else {
            return Boolean.parseBoolean(String.valueOf(value.getValue()));
        }
    }
}