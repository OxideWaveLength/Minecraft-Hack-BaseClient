package net.minecraft.client.renderer.chunk;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledChunk {
	public static final CompiledChunk DUMMY = new CompiledChunk() {
		protected void setLayerUsed(EnumWorldBlockLayer layer) {
			throw new UnsupportedOperationException();
		}

		public void setLayerStarted(EnumWorldBlockLayer layer) {
			throw new UnsupportedOperationException();
		}

		public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
			return false;
		}
	};
	private final boolean[] layersUsed = new boolean[EnumWorldBlockLayer.values().length];
	private final boolean[] layersStarted = new boolean[EnumWorldBlockLayer.values().length];
	private boolean empty = true;
	private final List<TileEntity> tileEntities = Lists.<TileEntity>newArrayList();
	private SetVisibility setVisibility = new SetVisibility();
	private WorldRenderer.State state;

	public boolean isEmpty() {
		return this.empty;
	}

	protected void setLayerUsed(EnumWorldBlockLayer layer) {
		this.empty = false;
		this.layersUsed[layer.ordinal()] = true;
	}

	public boolean isLayerEmpty(EnumWorldBlockLayer layer) {
		return !this.layersUsed[layer.ordinal()];
	}

	public void setLayerStarted(EnumWorldBlockLayer layer) {
		this.layersStarted[layer.ordinal()] = true;
	}

	public boolean isLayerStarted(EnumWorldBlockLayer layer) {
		return this.layersStarted[layer.ordinal()];
	}

	public List<TileEntity> getTileEntities() {
		return this.tileEntities;
	}

	public void addTileEntity(TileEntity tileEntityIn) {
		this.tileEntities.add(tileEntityIn);
	}

	public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
		return this.setVisibility.isVisible(facing, facing2);
	}

	public void setVisibility(SetVisibility visibility) {
		this.setVisibility = visibility;
	}

	public WorldRenderer.State getState() {
		return this.state;
	}

	public void setState(WorldRenderer.State stateIn) {
		this.state = stateIn;
	}
}
