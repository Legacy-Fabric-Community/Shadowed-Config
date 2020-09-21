package io.github.legacy_fabric_community.serialization.json.nbt;

import java.math.BigInteger;

public abstract class NbtUtils {
    private NbtUtils() {
    }

    // TODO: char support
    private static final Class<?>[] PRIMITIVE_TYPES = {
            int.class,
            long.class,
            short.class,
            float.class,
            double.class,
            byte.class,
            boolean.class,
            Integer.class,
            Long.class,
            Short.class,
            Float.class,
            Double.class,
            Byte.class,
            Boolean.class
    };

    public static boolean isPrimitiveOrString(Object target) {
        if (target instanceof String) {
            return true;
        }

        Class<?> classOfPrimitive = target.getClass();
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntegral(NbtPrimitive primitive) {
        if (primitive.getValue() instanceof Number) {
            Number number = (Number) primitive.getValue();
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte;
        }
        return false;
    }
}
