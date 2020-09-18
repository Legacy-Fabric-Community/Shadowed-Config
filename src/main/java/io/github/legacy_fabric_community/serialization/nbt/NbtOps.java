package io.github.legacy_fabric_community.serialization.nbt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.github.legacy_fabric_community.serialization.codec.ExtendedOps;
import io.github.legacy_fabric_community.serialization.mixin.CompoundTagAccessor;
import io.github.legacy_fabric_community.serialization.mixin.EndTagAccessor;
import io.github.legacy_fabric_community.serialization.mixin.ListTagAccessor;
import io.github.legacy_fabric_community.serialization.mixin.TagAccessor;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NbtOps implements ExtendedOps<Tag> {
    public static final NbtOps INSTANCE = new NbtOps();
    static final EndTag THE_END_TAG = EndTagAccessor.create();

    private NbtOps() {
    }

    private static Tag createListTag(byte b, byte c) {
        if (matches(b, c, (byte) 4)) {
            return new LongArrayTag(new long[0]);
        } else if (matches(b, c, (byte) 1)) {
            return new ByteArrayTag(new byte[0]);
        } else {
            return matches(b, c, (byte) 3) ? new IntArrayTag(new int[0]) : new ListTag();
        }
    }

    private static boolean matches(byte b, byte c, byte d) {
        return b == d && (c == d || c == 0);
    }

    private static void fill(ListTag listTag, Tag tag, Tag tag2) {
        if (tag instanceof ListTag) {
            ListTag abstractListTag2 = (ListTag) tag;
            ((ListTagAccessor) abstractListTag2).getValue().forEach(listTag::add);
        }
        listTag.add(tag2);
    }

    private static void fillAll(ListTag listTag, Tag tag, List<Tag> list) {
        if (tag instanceof ListTag) {
            ListTag tempList = (ListTag) tag;
            ((ListTagAccessor) tempList).getValue().forEach(listTag::add);
        }
        list.forEach(listTag::add);
    }

    public Tag empty() {
        return THE_END_TAG;
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, Tag tag) {
        switch (tag.getType()) {
            case 0:
                return dynamicOps.empty();
            case 1:
                return dynamicOps.createByte(((Tag.NumberTag) tag).getByte());
            case 2:
                return dynamicOps.createShort(((Tag.NumberTag) tag).getShort());
            case 3:
                return dynamicOps.createInt(((Tag.NumberTag) tag).getInt());
            case 4:
                return dynamicOps.createLong(((Tag.NumberTag) tag).getLong());
            case 5:
                return dynamicOps.createFloat(((Tag.NumberTag) tag).getFloat());
            case 6:
                return dynamicOps.createDouble(((Tag.NumberTag) tag).getDouble());
            case 7:
                return dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag) tag).getArray()));
            case 8:
                return dynamicOps.createString(((TagAccessor) tag).invokeAsString());
            case 9:
                return this.convertList(dynamicOps, tag);
            case 10:
                return this.convertMap(dynamicOps, tag);
            case 11:
                return dynamicOps.createIntList(Arrays.stream(((IntArrayTag) tag).getIntArray()));
            case 12:
                return dynamicOps.createLongList(Arrays.stream(((LongArrayTag) tag).getArray()));
            default:
                throw new IllegalStateException("Unknown tag type: " + tag);
        }
    }

    public DataResult<Number> getNumberValue(Tag tag) {
        return tag instanceof Tag.NumberTag ? DataResult.success((((NumericTag) tag)).getNumber()) : DataResult.error("Not a number");
    }

    public Tag createNumeric(Number number) {
        return new DoubleTag(number.doubleValue());
    }

    public Tag createByte(byte b) {
        return new ByteTag(b);
    }

    public Tag createShort(short s) {
        return new ShortTag(s);
    }

    public Tag createInt(int i) {
        return new IntTag(i);
    }

    public Tag createLong(long l) {
        return new LongTag(l);
    }

    public Tag createFloat(float f) {
        return new FloatTag(f);
    }

    public Tag createDouble(double d) {
        return new DoubleTag(d);
    }

    public Tag createBoolean(boolean bl) {
        return new ByteTag((byte) (bl ? 1 : 0));
    }

    public DataResult<String> getStringValue(Tag tag) {
        return tag instanceof StringTag ? DataResult.success(((TagAccessor) tag).invokeAsString()) : DataResult.error("Not a string");
    }

    public Tag createString(String string) {
        return new StringTag(string);
    }

    public DataResult<Tag> mergeToList(Tag tag, Tag tag2) {
        if (!(tag instanceof ListTag) && !(tag instanceof EndTag)) {
            return DataResult.error("mergeToList called with not a list: " + tag, tag);
        } else {
            Tag abstractListTag = createListTag((byte) (tag instanceof ListTag ? ((ListTag) tag).getElementType() : 0), tag2.getType());
            if (abstractListTag instanceof ListTag) {
                fill((ListTag) abstractListTag, tag, tag2);
            }
            return DataResult.success(abstractListTag);
        }
    }

    public DataResult<Tag> mergeToList(Tag tag, List<Tag> list) {
        if (!(tag instanceof ListTag) && !(tag instanceof EndTag)) {
            return DataResult.error("mergeToList called with not a list: " + tag, tag);
        } else {
            Tag abstractListTag = createListTag(tag instanceof ListTag ? (byte) ((ListTag) tag).getElementType() : (byte) 0, list.stream().findFirst().map(Tag::getType).orElse((byte) 0));
            if (abstractListTag instanceof ListTag) {
                fillAll((ListTag) abstractListTag, tag, list);
            }
            return DataResult.success(abstractListTag);
        }
    }

    public DataResult<Tag> mergeToMap(Tag mapTag, Tag keyTag, Tag tag) {
        if (!(mapTag instanceof CompoundTag) && !(mapTag instanceof EndTag)) {
            return DataResult.error("mergeToMap called with not a map: " + mapTag, mapTag);
        } else if (!(keyTag instanceof StringTag)) {
            return DataResult.error("key is not a string: " + keyTag, mapTag);
        } else {
            CompoundTag compoundTag = new CompoundTag();
            if (mapTag instanceof CompoundTag) {
                CompoundTag compoundTag2 = (CompoundTag) mapTag;
                compoundTag2.getKeys().forEach((string) -> compoundTag.put(string, compoundTag2.get(string)));
            }

            compoundTag.put(((TagAccessor) keyTag).invokeAsString(), tag);
            return DataResult.success(compoundTag);
        }
    }

    public DataResult<Tag> mergeToMap(Tag tag, MapLike<Tag> mapLike) {
        if (!(tag instanceof CompoundTag) && !(tag instanceof EndTag)) {
            return DataResult.error("mergeToMap called with not a map: " + tag, tag);
        } else {
            CompoundTag compoundTag = new CompoundTag();
            if (tag instanceof CompoundTag) {
                CompoundTag compoundTag2 = (CompoundTag) tag;
                compoundTag2.getKeys().forEach((string) -> compoundTag.put(string, compoundTag2.get(string)));
            }

            List<Tag> list = Lists.newArrayList();
            mapLike.entries().forEach((pair) -> {
                Tag tag2 = pair.getFirst();
                //noinspection ConstantConditions
                if (!(tag instanceof StringTag)) {
                    list.add(tag);
                } else {
                    compoundTag.put(((TagAccessor) tag2).invokeAsString(), pair.getSecond());
                }
            });
            return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, compoundTag) : DataResult.success(compoundTag);
        }
    }

    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag tag) {
        if (!(tag instanceof CompoundTag)) {
            return DataResult.error("Not a map: " + tag);
        } else {
            CompoundTag compoundTag = (CompoundTag) tag;
            return DataResult.success(compoundTag.getKeys().stream().map((string) -> Pair.of(this.createString(string), compoundTag.get(string))));
        }
    }

    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag tag) {
        if (!(tag instanceof CompoundTag)) {
            return DataResult.error("Not a map: " + tag);
        } else {
            CompoundTag compoundTag = (CompoundTag) tag;
            return DataResult.success((consumer) -> compoundTag.getKeys().forEach((string) -> consumer.accept(this.createString(string), compoundTag.get(string))));
        }
    }

    public DataResult<MapLike<Tag>> getMap(Tag tag) {
        if (!(tag instanceof CompoundTag)) {
            return DataResult.error("Not a map: " + tag);
        } else {
            final CompoundTag compoundTag = (CompoundTag) tag;
            return DataResult.success(new MapLike<Tag>() {
                public Tag get(Tag tag) {
                    return compoundTag.get(((TagAccessor) tag).invokeAsString());
                }

                public Tag get(String string) {
                    return compoundTag.get(string);
                }

                public Stream<Pair<Tag, Tag>> entries() {
                    return compoundTag.getKeys().stream().map((string) -> Pair.of(NbtOps.this.createString(string), compoundTag.get(string)));
                }

                public String toString() {
                    return "MapLike[" + compoundTag + "]";
                }
            });
        }
    }

    public Tag createMap(Stream<Pair<Tag, Tag>> stream) {
        CompoundTag compoundTag = new CompoundTag();
        stream.forEach((pair) -> compoundTag.put(((TagAccessor) pair.getFirst()).invokeAsString(), pair.getSecond()));
        return compoundTag;
    }

    public DataResult<Stream<Tag>> getStream(Tag tag) {
        return tag instanceof ListTag ? DataResult.success(((ListTagAccessor) tag).getValue().stream()) : DataResult.error("Not a list");
    }

    public DataResult<Consumer<Consumer<Tag>>> getList(Tag tag) {
        if (tag instanceof ListTag) {
            ListTag listTag = (ListTag) tag;
            return DataResult.success(((ListTagAccessor) listTag).getValue()::forEach);
        } else {
            return DataResult.error("Not a list: " + tag);
        }
    }

    public DataResult<ByteBuffer> getByteBuffer(Tag tag) {
        return tag instanceof ByteArrayTag ? DataResult.success(ByteBuffer.wrap(((ByteArrayTag) tag).getArray())) : ExtendedOps.super.getByteBuffer(tag);
    }

    public Tag createByteList(ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray(byteBuffer));
    }

    public DataResult<IntStream> getIntStream(Tag tag) {
        return tag instanceof IntArrayTag ? DataResult.success(Arrays.stream(((IntArrayTag) tag).getIntArray())) : ExtendedOps.super.getIntStream(tag);
    }

    public Tag createIntList(IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }

    public DataResult<LongStream> getLongStream(Tag tag) {
        return tag instanceof LongArrayTag ? DataResult.success(Arrays.stream(((LongArrayTag) tag).getArray())) : ExtendedOps.super.getLongStream(tag);
    }

    public Tag createLongList(LongStream longStream) {
        return new LongArrayTag(longStream.toArray());
    }

    public Tag createList(Stream<Tag> stream) {
        PeekingIterator<Tag> peekingIterator = Iterators.peekingIterator(stream.iterator());
        if (!peekingIterator.hasNext()) {
            return new ListTag();
        } else {
            Tag tag = peekingIterator.peek();
            ArrayList<Number> list3;
            if (tag instanceof ByteTag) {
                list3 = Lists.newArrayList(Iterators.transform(peekingIterator, (tagx) -> tagx != null ? ((ByteTag) tagx).getByte() : 0));
                byte[] arr = new byte[list3.size()];
                for (int a = 0; a < arr.length; a++) {
                    arr[a] = (byte) list3.get(a);
                }
                return new ByteArrayTag(arr);
            } else if (tag instanceof IntTag) {
                list3 = Lists.newArrayList(Iterators.transform(peekingIterator, (tagx) -> tagx != null ? ((IntTag) tagx).getInt() : 0));
                int[] arr = new int[list3.size()];
                for (int a = 0; a < arr.length; a++) {
                    arr[a] = (int) list3.get(a);
                }
                return new IntArrayTag(arr);
            } else if (tag instanceof LongTag) {
                list3 = Lists.newArrayList(Iterators.transform(peekingIterator, (tagx) -> tagx != null ? ((LongTag) tagx).getLong() : 0));
                long[] arr = new long[list3.size()];
                for (int a = 0; a < arr.length; a++) {
                    arr[a] = (long) list3.get(a);
                }
                return new LongArrayTag(arr);
            } else {
                ListTag listTag = new ListTag();

                while (peekingIterator.hasNext()) {
                    Tag tag2 = peekingIterator.next();
                    if (!(tag2 instanceof EndTag)) {
                        listTag.add(tag2);
                    }
                }

                return listTag;
            }
        }
    }

    public Tag remove(Tag tag, String string) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag.getKeys().stream().filter((string2) -> !Objects.equals(string2, string)).forEach((stringx) -> compoundTag2.put(stringx, compoundTag.get(stringx)));
            return compoundTag2;
        } else {
            return tag;
        }
    }

    public String toString() {
        return "NBT";
    }

    public RecordBuilder<Tag> mapBuilder() {
        return new NbtOps.MapBuilder();
    }

    class MapBuilder extends AbstractStringBuilder<Tag, CompoundTag> {
        protected MapBuilder() {
            super(NbtOps.this);
        }

        protected CompoundTag initBuilder() {
            return new CompoundTag();
        }

        protected CompoundTag append(String string, Tag tag, CompoundTag compoundTag) {
            compoundTag.put(string, tag);
            return compoundTag;
        }

        protected DataResult<Tag> build(CompoundTag compoundTag, Tag tag) {
            if (tag != null && tag != THE_END_TAG) {
                if (!(tag instanceof CompoundTag)) {
                    return DataResult.error("mergeToMap called with not a map: " + tag, tag);
                } else {
                    CompoundTag compoundTag2 = new CompoundTag();
                    ((CompoundTagAccessor) compoundTag).setData(Maps.newHashMap(((CompoundTagAccessor) tag).getData()));

                    for (Entry<String, Tag> stringTagEntry : ((CompoundTagAccessor) compoundTag).getData().entrySet()) {
                        compoundTag2.put(stringTagEntry.getKey(), (stringTagEntry).getValue());
                    }

                    return DataResult.success(compoundTag2);
                }
            } else {
                return DataResult.success(compoundTag);
            }
        }
    }
}
