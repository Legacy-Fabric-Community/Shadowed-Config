package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import io.github.legacy_fabric_community.shadowedconfig.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;

public class NumberTagMixins {
	@Mixin(ShortTag.class)
	public static class ShortTagMixin implements NumericTag {
		@Shadow
		private short value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(DoubleTag.class)
	public static class DoubleTagMixin implements NumericTag {
		@Shadow
		private double value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(FloatTag.class)
	public static class FloatTagMixin implements NumericTag {
		@Shadow
		private float value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(ByteTag.class)
	public static class ByteTagMixin implements NumericTag {
		@Shadow
		private byte value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(IntTag.class)
	public static class IntTagMixin implements NumericTag {
		@Shadow
		private int value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(LongTag.class)
	public static class LongTagMixin implements NumericTag {
		@Shadow
		private long value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}
}
