package io.github.legacy_fabric_community.shadowedconfig.nbt.mixin;

import io.github.legacy_fabric_community.shadowedconfig.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;

public class NumberTagMixins {
	@Mixin(NbtShort.class)
	public static class ShortTagMixin implements NumericTag {
		@Shadow
		private short value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(NbtDouble.class)
	public static class DoubleTagMixin implements NumericTag {
		@Shadow
		private double value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(NbtFloat.class)
	public static class FloatTagMixin implements NumericTag {
		@Shadow
		private float value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(NbtByte.class)
	public static class ByteTagMixin implements NumericTag {
		@Shadow
		private byte value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(NbtInt.class)
	public static class IntTagMixin implements NumericTag {
		@Shadow
		private int value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}

	@Mixin(NbtLong.class)
	public static class LongTagMixin implements NumericTag {
		@Shadow
		private long value;

		@Override
		public Number getNumber() {
			return this.value;
		}
	}
}
