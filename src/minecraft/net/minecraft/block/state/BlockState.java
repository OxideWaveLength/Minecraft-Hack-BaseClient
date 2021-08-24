package net.minecraft.block.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState {
	private static final Joiner COMMA_JOINER = Joiner.on(", ");
	private static final Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>() {
		public String apply(IProperty p_apply_1_) {
			return p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
		}
	};
	private final Block block;
	private final ImmutableList<IProperty> properties;
	private final ImmutableList<IBlockState> validStates;

	public BlockState(Block blockIn, IProperty... properties) {
		this.block = blockIn;
		Arrays.sort(properties, new Comparator<IProperty>() {
			public int compare(IProperty p_compare_1_, IProperty p_compare_2_) {
				return p_compare_1_.getName().compareTo(p_compare_2_.getName());
			}
		});
		this.properties = ImmutableList.copyOf(properties);
		Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map = Maps.<Map<IProperty, Comparable>, BlockState.StateImplementation>newLinkedHashMap();
		List<BlockState.StateImplementation> list = Lists.<BlockState.StateImplementation>newArrayList();

		for (List<Comparable> list1 : Cartesian.cartesianProduct(this.getAllowedValues())) {
			Map<IProperty, Comparable> map1 = MapPopulator.<IProperty, Comparable>createMap(this.properties, list1);
			BlockState.StateImplementation blockstate$stateimplementation = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(map1));
			map.put(map1, blockstate$stateimplementation);
			list.add(blockstate$stateimplementation);
		}

		for (BlockState.StateImplementation blockstate$stateimplementation1 : list) {
			blockstate$stateimplementation1.buildPropertyValueTable(map);
		}

		this.validStates = ImmutableList.<IBlockState>copyOf(list);
	}

	public ImmutableList<IBlockState> getValidStates() {
		return this.validStates;
	}

	private List<Iterable<Comparable>> getAllowedValues() {
		List<Iterable<Comparable>> list = Lists.<Iterable<Comparable>>newArrayList();

		for (int i = 0; i < this.properties.size(); ++i) {
			list.add(((IProperty) this.properties.get(i)).getAllowedValues());
		}

		return list;
	}

	public IBlockState getBaseState() {
		return (IBlockState) this.validStates.get(0);
	}

	public Block getBlock() {
		return this.block;
	}

	public Collection<IProperty> getProperties() {
		return this.properties;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(this.block)).add("properties", Iterables.transform(this.properties, GET_NAME_FUNC)).toString();
	}

	static class StateImplementation extends BlockStateBase {
		private final Block block;
		private final ImmutableMap<IProperty, Comparable> properties;
		private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

		private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
			this.block = blockIn;
			this.properties = propertiesIn;
		}

		public Collection<IProperty> getPropertyNames() {
			return Collections.<IProperty>unmodifiableCollection(this.properties.keySet());
		}

		public <T extends Comparable<T>> T getValue(IProperty<T> property) {
			if (!this.properties.containsKey(property)) {
				throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
			} else {
				return (T) ((Comparable) property.getValueClass().cast(this.properties.get(property)));
			}
		}

		public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
			if (!this.properties.containsKey(property)) {
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
			} else if (!property.getAllowedValues().contains(value)) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value");
			} else {
				return (IBlockState) (this.properties.get(property) == value ? this : (IBlockState) this.propertyValueTable.get(property, value));
			}
		}

		public ImmutableMap<IProperty, Comparable> getProperties() {
			return this.properties;
		}

		public Block getBlock() {
			return this.block;
		}

		public boolean equals(Object p_equals_1_) {
			return this == p_equals_1_;
		}

		public int hashCode() {
			return this.properties.hashCode();
		}

		public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map) {
			if (this.propertyValueTable != null) {
				throw new IllegalStateException();
			} else {
				Table<IProperty, Comparable, IBlockState> table = HashBasedTable.<IProperty, Comparable, IBlockState>create();

				for (IProperty<? extends Comparable> iproperty : this.properties.keySet()) {
					for (Comparable comparable : iproperty.getAllowedValues()) {
						if (comparable != this.properties.get(iproperty)) {
							table.put(iproperty, comparable, map.get(this.getPropertiesWithValue(iproperty, comparable)));
						}
					}
				}

				this.propertyValueTable = ImmutableTable.<IProperty, Comparable, IBlockState>copyOf(table);
			}
		}

		private Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value) {
			Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newHashMap(this.properties);
			map.put(property, value);
			return map;
		}
	}
}
