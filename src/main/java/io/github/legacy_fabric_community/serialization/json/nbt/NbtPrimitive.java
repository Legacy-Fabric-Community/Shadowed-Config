package io.github.legacy_fabric_community.serialization.json.nbt;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;

public final class NbtPrimitive extends NbtElement {
    private Object value;

    public NbtPrimitive(String value) {
        this.value = value;
    }

    public NbtPrimitive(Number value) {
        this.value = value;
    }

    public NbtPrimitive(Boolean value) {
        this.value = value;
    }

    @Override
    public NbtPrimitive clone() throws CloneNotSupportedException {
        return (NbtPrimitive) super.clone();
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    @Override
    public boolean getAsBoolean() {
        if (this.isBoolean()) {
            return (Boolean) this.value;
        } else {
            return Boolean.parseBoolean(this.getAsString());
        }
    }

    @Override
    public Number getAsNumber() {
        return this.value instanceof String ? new LazilyParsedNumber((String) this.value) : (Number) this.value;
    }

    @Override
    public String getAsString() {
        if (this.isNumber()) {
            return this.getAsNumber().toString();
        } else if (this.isBoolean()) {
            return (Boolean) this.value ? "true" : "false";
        } else {
            return (String) this.value;
        }
    }

    @Override
    public double getAsDouble() {
        return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return this.value instanceof BigDecimal ? (BigDecimal) this.value : new BigDecimal(this.value.toString());
    }

    @Override
    public BigInteger getAsBigInteger() {
        return this.value instanceof BigInteger ? (BigInteger) this.value : new BigInteger(this.value.toString());
    }

    @Override
    public float getAsFloat() {
        return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
    }

    @Override
    public long getAsLong() {
        return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
    }

    @Override
    public short getAsShort() {
        return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
    }

    @Override
    public int getAsInt() {
        return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
    }

    @Override
    public byte getAsByte() {
        return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
    }
}
