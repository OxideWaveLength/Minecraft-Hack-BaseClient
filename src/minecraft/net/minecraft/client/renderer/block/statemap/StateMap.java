package net.minecraft.client.renderer.block.statemap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class StateMap extends StateMapperBase {
	private final IProperty<?> name;
	private final String suffix;
	private final List<IProperty<?>> ignored;

	private StateMap(IProperty<?> name, String suffix, List<IProperty<?>> ignored) {
		this.name = name;
		this.suffix = suffix;
		this.ignored = ignored;
	}

	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());
		String s;

		if (this.name == null) {
			s = ((ResourceLocation) Block.blockRegistry.getNameForObject(state.getBlock())).toString();
		} else {
			s = ((IProperty) this.name).getName((Comparable) map.remove(this.name));
		}

		if (this.suffix != null) {
			s = s + this.suffix;
		}

		for (IProperty<?> iproperty : this.ignored) {
			map.remove(iproperty);
		}

		return new ModelResourceLocation(s, this.getPropertyString(map));
	}

	public static class Builder {
		private IProperty<?> name;
		private String suffix;
		private final List<IProperty<?>> ignored = Lists.<IProperty<?>>newArrayList();

		public StateMap.Builder withName(IProperty<?> builderPropertyIn) {
			this.name = builderPropertyIn;
			return this;
		}

		public StateMap.Builder withSuffix(String builderSuffixIn) {
			this.suffix = builderSuffixIn;
			return this;
		}

		public StateMap.Builder ignore(IProperty<?>... p_178442_1_) {
			Collections.addAll(this.ignored, p_178442_1_);
			return this;
		}

		public StateMap build() {
			return new StateMap(this.name, this.suffix, this.ignored);
		}
	}
}
