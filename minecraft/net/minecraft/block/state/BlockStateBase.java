package net.minecraft.block.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState {
	private static final Joiner COMMA_JOINER = Joiner.on(',');
	private static final Function MAP_ENTRY_TO_STRING = new Function() {
		

		public String apply(Entry p_apply_1_) {
			if (p_apply_1_ == null) {
				return "<NULL>";
			} else {
				IProperty iproperty = (IProperty) p_apply_1_.getKey();
				return iproperty.getName() + "=" + iproperty.getName((Comparable) p_apply_1_.getValue());
			}
		}

		public Object apply(Object p_apply_1_) {
			return this.apply((Entry) p_apply_1_);
		}
	};
	
	private int blockId = -1;
	private int blockStateId = -1;
	private int metadata = -1;
	private ResourceLocation blockLocation = null;

	public int getBlockId() {
		if (this.blockId < 0) {
			this.blockId = Block.getIdFromBlock(this.getBlock());
		}

		return this.blockId;
	}

	public int getBlockStateId() {
		if (this.blockStateId < 0) {
			this.blockStateId = Block.getStateId(this);
		}

		return this.blockStateId;
	}

	public int getMetadata() {
		if (this.metadata < 0) {
			this.metadata = this.getBlock().getMetaFromState(this);
		}

		return this.metadata;
	}

	public ResourceLocation getBlockLocation() {
		if (this.blockLocation == null) {
			this.blockLocation = (ResourceLocation) Block.blockRegistry.getNameForObject(this.getBlock());
		}

		return this.blockLocation;
	}

	/**
	 * Create a version of this BlockState with the given property cycled to the
	 * next value in order. If the property was at the highest possible value, it is
	 * set to the lowest one instead.
	 */
	public IBlockState cycleProperty(IProperty property) {
		return this.withProperty(property, (Comparable) cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
	}

	/**
	 * Helper method for cycleProperty.
	 */
	protected static Object cyclePropertyValue(Collection values, Object currentValue) {
		Iterator iterator = values.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(currentValue)) {
				if (iterator.hasNext()) {
					return iterator.next();
				}

				return values.iterator().next();
			}
		}

		return iterator.next();
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));

		if (!this.getProperties().isEmpty()) {
			stringbuilder.append("[");
			COMMA_JOINER.appendTo(stringbuilder, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
			stringbuilder.append("]");
		}

		return stringbuilder.toString();
	}
}
