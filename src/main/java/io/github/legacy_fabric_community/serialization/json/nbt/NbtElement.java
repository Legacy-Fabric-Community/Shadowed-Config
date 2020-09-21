package io.github.legacy_fabric_community.serialization.json.nbt;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class NbtElement implements Cloneable {
    public NbtElement clone() throws CloneNotSupportedException {
        return (NbtElement) super.clone();
    }
    
    public boolean isNbtPrimitive() {
        return this instanceof NbtPrimitive;
    }

    public boolean isNbtNull() {
        return this == NbtNull.INSTANCE;
    }

    public final NbtPrimitive getAsNbtPrimitive() {
        if (this.isNbtPrimitive()) {
            return (NbtPrimitive) this;
        }
        throw new IllegalStateException("Not an Nbt Primitive");
    }
    
    public final NbtNull getAsNbtNull() {
        if (this.isNbtNull()) {
            return NbtNull.INSTANCE;
        }
        throw new IllegalStateException("Not an Nbt Null");
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public Number getAsNumber() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public String getAsString() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public double getAsDouble() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public float getAsFloat() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public long getAsLong() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public int getAsInt() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public byte getAsByte() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public short getAsShort() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

}
