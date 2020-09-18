package io.github.legacy_fabric_community.serialization.json;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.legacy_fabric_community.serialization.codec.ExtendedOps;
import io.github.legacy_fabric_community.serialization.codec.SetBuilder;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

public class JanksonOps implements ExtendedOps<JsonElement> {
    public static final JanksonOps INSTANCE = new JanksonOps(false);
    public static final JanksonOps COMPRESSED = new JanksonOps(true);
    private final boolean compressed;

    private JanksonOps(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject) {
            return this.convertMap(outOps, input);
        }
        if (input instanceof JsonArray) {
            return this.convertList(outOps, input);
        }
        if (input instanceof JsonNull) {
            return outOps.empty();
        }
        final JsonPrimitive primitive = JanksonUtils.getAsJsonPrimitive(input);
        if (primitive.getValue() instanceof String) {
            return outOps.createString(String.valueOf(primitive.getValue()));
        }
        if (primitive.getValue() instanceof Boolean) {
            return outOps.createBoolean((Boolean) primitive.getValue());
        }
        final BigDecimal value = JanksonUtils.getAsBigDecimal(primitive);
        try {
            final long l = value.longValueExact();
            if ((byte) l == l) {
                return outOps.createByte((byte) l);
            }
            if ((short) l == l) {
                return outOps.createShort((short) l);
            }
            if ((int) l == l) {
                return outOps.createInt((int) l);
            }
            return outOps.createLong(l);
        } catch (final ArithmeticException e) {
            final double d = value.doubleValue();
            if ((float) d == d) {
                return outOps.createFloat((float) d);
            }
            return outOps.createDouble(d);
        }
    }

    @Override
    public DataResult<Number> getNumberValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            if (JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Number) {
                return DataResult.success(JanksonUtils.getAsNumber((JsonPrimitive) input));
            } else if (JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Boolean) {
                return DataResult.success(JanksonUtils.getAsBoolean((JsonPrimitive) input) ? 1 : 0);
            }
            if (this.compressed && JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof String) {
                try {
                    return DataResult.success(Integer.parseInt(String.valueOf(((JsonPrimitive) input).getValue())));
                } catch (final NumberFormatException e) {
                    return DataResult.error("Not a number: " + e + " " + input);
                }
            }
        }
        if (input instanceof JsonPrimitive && JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Boolean) {
            return DataResult.success(JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Boolean ? 1 : 0);
        }
        return DataResult.error("Not a number: " + input);
    }

    @Override
    public JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i);
    }

    @Override
    public JsonElement createBoolean(final boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            if (JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Boolean) {
                return DataResult.success(JanksonUtils.getAsBoolean((JsonPrimitive) input));
            } else if (JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Number) {
                return DataResult.success(JanksonUtils.getAsNumber((JsonPrimitive) input).byteValue() != 0);
            }
        }
        return DataResult.error("Not a boolean: " + input);
    }

    @Override
    public DataResult<String> getStringValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            if (JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof String || JanksonUtils.getAsJsonPrimitive(input).getValue() instanceof Number && this.compressed) {
                return DataResult.success(String.valueOf(((JsonPrimitive) input).getValue()));
            }
        }
        return DataResult.error("Not a string: " + input);
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (!(list instanceof JsonArray) && list != this.empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final JsonArray result = new JsonArray();
        if (list != this.empty()) {
            result.addAll(JanksonUtils.getAsJsonArray(list));
        }
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(map instanceof JsonObject) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        if (!(key instanceof JsonPrimitive) || !(JanksonUtils.getAsJsonPrimitive(key).getValue() instanceof String) && !this.compressed) {
            return DataResult.error("key is not a string: " + key, map);
        }

        final JsonObject output = new JsonObject();
        if (map != this.empty()) {
            JanksonUtils.getAsJsonObject(map).forEach(output::put);
        }
        output.put(String.valueOf(((JsonPrimitive) key).getValue()), value);

        return DataResult.success(output);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> values) {
        if (!(map instanceof JsonObject) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }

        final JsonObject output = new JsonObject();
        if (map != this.empty()) {
            JanksonUtils.getAsJsonObject(map).forEach(output::put);
        }

        final List<JsonElement> missed = Lists.newArrayList();

        values.entries().forEach(entry -> {
            final JsonElement key = entry.getFirst();
            if (!(key instanceof JsonPrimitive) || !(JanksonUtils.getAsJsonPrimitive(map).getValue() instanceof String) && !this.compressed) {
                missed.add(key);
                return;
            }
            output.put(String.valueOf(((JsonPrimitive) key).getValue()), entry.getSecond());
        });

        if (!missed.isEmpty()) {
            return DataResult.error("some keys are not strings: " + missed, output);
        }

        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (!(input instanceof JsonObject)) {
            return DataResult.error("Not a JSON object: " + input);
        }
        return DataResult.success((((JsonObject) input).entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue() instanceof JsonNull ? null : entry.getValue()))));
    }

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        final JsonObject result = new JsonObject();
        map.forEach(p -> result.put(p.getFirst().toJson(), p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<JsonElement>> getStream(JsonElement input) {
        if (input instanceof JsonArray) {
            return DataResult.success(JanksonUtils.getAsJsonArray(input).stream().map(e -> e instanceof JsonNull ? null : e));
        }
        return DataResult.error("Not a json array: " + input);
    }

    @Override
    public JsonElement createList(Stream<JsonElement> input) {
        final JsonArray result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public JsonElement remove(JsonElement input, String key) {
        if (input instanceof JsonObject) {
            final JsonObject result = new JsonObject();
            JanksonUtils.getAsJsonObject(input).entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }
}