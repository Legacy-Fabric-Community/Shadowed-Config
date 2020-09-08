package io.github.legacy_fabric_community.serialization.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;

public class LongArrayTag extends Tag {
    private long[] value;

    public LongArrayTag(long[] value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for (long l : this.value) {
            output.writeLong(l);
        }
    }

    @Override
    public void read(DataInput dataInput, int depth, PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        int j = dataInput.readInt();
        positionTracker.add(64L * j);
        long[] ls = new long[j];
        for (int k = 0; k < j; k++) {
            ls[k] = dataInput.readLong();
        }
        this.value = ls.clone();
    }

    public byte getType() {
        return 12;
    }

    public String toString() {
        return "[" + this.value.length + " bytes]";
    }

    public Tag copy() {
        long[] bs = new long[this.value.length];
        System.arraycopy(this.value, 0, bs, 0, this.value.length);
        return new LongArrayTag(bs);
    }

    public boolean equals(Object object) {
        return super.equals(object) && Arrays.equals(this.value, ((LongArrayTag) object).value);
    }

    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.value);
    }

    public long[] getArray() {
        return this.value;
    }
}
