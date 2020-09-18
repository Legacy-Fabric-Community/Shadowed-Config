package io.github.legacy_fabric_community.serialization.nbt;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/**
 * Helper class for serializing objects to nbt tags.
 */
@Beta
public final class Tags {
    private Tags() {
    }

    public static Tag toTag(Object o) {
        if (o == null) {
            return NbtOps.THE_END_TAG;
        } else {
            if (o instanceof String) {
                return NbtOps.INSTANCE.createString((String) o);
            }
            if (o instanceof Number) {
                return NbtOps.INSTANCE.createNumeric((Number) o);
            }

            CompoundTag tag = new CompoundTag();

            // Easy stuff first
            if (o instanceof Enum) {
                tag.put("_enum_", new StringTag(((Enum<?>) o).name()));
                return tag;
            }

            if (o.getClass().isArray()) {
                List<Tag> tagList = Lists.newArrayList();
                for(int i = 0; i< Array.getLength(o); i++) {
                    Object in = Array.get(o, i);
                    tagList.add(toTag(in));
                }
                tag.put("_array_", NbtOps.INSTANCE.createList(tagList.stream()));
                return tag;
            }

            if (o instanceof Collection) {
                List<Tag> tagList = Lists.newArrayList();
                for (Object in : (Collection<?>) o) {
                    Tag t = toTag(in);
                    tagList.add(t);
                }
                return NbtOps.INSTANCE.createList(tagList.stream());
            }

            if (o instanceof Map) {
                Map<Tag, Tag> tagMap = Maps.newHashMap();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
                    Tag key = NbtOps.INSTANCE.createString(entry.getKey().toString());
                    Tag value = toTag(entry.getValue());
                    tagMap.put(key, value);
                }
                return NbtOps.INSTANCE.createMap(tagMap);
            }


            // Now comes the reflection

            for (Field f : o.getClass().getFields()) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                f.setAccessible(true);
                try {
                    Object fieldObj = f.get(o);
                    String name = f.getName();
                    tag.put(name, toTag(fieldObj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            for (Field f : o.getClass().getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                f.setAccessible(true);
                try {
                    Object fieldObj = f.get(o);
                    String name = f.getName();
                    tag.put(name, toTag(fieldObj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            return tag;
        }
    }
}
