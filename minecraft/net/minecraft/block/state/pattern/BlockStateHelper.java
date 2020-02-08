package net.minecraft.block.state.pattern;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState> {
	private final BlockState blockstate;
	private final Map<IProperty, Predicate> propertyPredicates = Maps.<IProperty, Predicate>newHashMap();

	private BlockStateHelper(BlockState blockStateIn) {
		this.blockstate = blockStateIn;
	}

	public static BlockStateHelper forBlock(Block blockIn) {
		return new BlockStateHelper(blockIn.getBlockState());
	}

	public boolean apply(IBlockState p_apply_1_) {
		if (p_apply_1_ != null && p_apply_1_.getBlock().equals(this.blockstate.getBlock())) {
			for (Entry<IProperty, Predicate> entry : this.propertyPredicates.entrySet()) {
				Object object = p_apply_1_.getValue((IProperty) entry.getKey());

				if (!((Predicate) entry.getValue()).apply(object)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public <V extends Comparable<V>> BlockStateHelper where(IProperty<V> property, Predicate<? extends V> is) {
		if (!this.blockstate.getProperties().contains(property)) {
			throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
		} else {
			this.propertyPredicates.put(property, is);
			return this;
		}
	}
}
