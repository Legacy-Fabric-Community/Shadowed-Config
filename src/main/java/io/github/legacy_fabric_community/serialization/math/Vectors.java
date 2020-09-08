package io.github.legacy_fabric_community.serialization.math;

public interface Vectors {
    Vec2f ZERO_F = new Vec2f(0.0F, 0.0F);
    Vec2f SOUTH_EAST_UNIT_F = new Vec2f(1.0F, 1.0F);
    Vec2f EAST_UNIT_F = new Vec2f(1.0F, 0.0F);
    Vec2f WEST_UNIT_F = new Vec2f(-1.0F, 0.0F);
    Vec2f SOUTH_UNIT_F = new Vec2f(0.0F, 1.0F);
    Vec2f NORTH_UNIT_F = new Vec2f(0.0F, -1.0F);
    Vec2f MAX_SOUTH_EAST_F = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
    Vec2f MIN_SOUTH_EAST_F = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);
}
