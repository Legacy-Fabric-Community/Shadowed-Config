package io.github.legacy_fabric_community.serialization.math;

public class Vec2f implements Cloneable {
    public final float x;
    public final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vec2f) {
            return this.x == ((Vec2f) other).x && this.y == ((Vec2f) other).y;
        }
        return super.equals(other);
    }

    @Override
    public Vec2f clone() {
        try {
            return (Vec2f) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}
