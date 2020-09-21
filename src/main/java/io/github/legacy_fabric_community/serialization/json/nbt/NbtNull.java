package io.github.legacy_fabric_community.serialization.json.nbt;

public class NbtNull extends NbtElement {
    public static final NbtNull INSTANCE = new NbtNull();

    private NbtNull() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return obj == INSTANCE && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(null);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public NbtElement clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
