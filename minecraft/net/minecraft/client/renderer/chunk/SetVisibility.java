package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;

import net.minecraft.util.EnumFacing;

public class SetVisibility {
	private static final int COUNT_FACES = EnumFacing.values().length;
	private final BitSet bitSet;

	public SetVisibility() {
		this.bitSet = new BitSet(COUNT_FACES * COUNT_FACES);
	}

	public void setManyVisible(Set<EnumFacing> p_178620_1_) {
		for (EnumFacing enumfacing : p_178620_1_) {
			for (EnumFacing enumfacing1 : p_178620_1_) {
				this.setVisible(enumfacing, enumfacing1, true);
			}
		}
	}

	public void setVisible(EnumFacing facing, EnumFacing facing2, boolean p_178619_3_) {
		this.bitSet.set(facing.ordinal() + facing2.ordinal() * COUNT_FACES, p_178619_3_);
		this.bitSet.set(facing2.ordinal() + facing.ordinal() * COUNT_FACES, p_178619_3_);
	}

	public void setAllVisible(boolean visible) {
		this.bitSet.set(0, this.bitSet.size(), visible);
	}

	public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
		return this.bitSet.get(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(' ');

		for (EnumFacing enumfacing : EnumFacing.values()) {
			stringbuilder.append(' ').append(enumfacing.toString().toUpperCase().charAt(0));
		}

		stringbuilder.append('\n');

		for (EnumFacing enumfacing2 : EnumFacing.values()) {
			stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));

			for (EnumFacing enumfacing1 : EnumFacing.values()) {
				if (enumfacing2 == enumfacing1) {
					stringbuilder.append("  ");
				} else {
					boolean flag = this.isVisible(enumfacing2, enumfacing1);
					stringbuilder.append(' ').append((char) (flag ? 'Y' : 'n'));
				}
			}

			stringbuilder.append('\n');
		}

		return stringbuilder.toString();
	}
}
