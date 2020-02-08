package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {
	public RenderMinecartMobSpawner(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	protected void func_180560_a(EntityMinecartMobSpawner minecart, float partialTicks, IBlockState state) {
		super.func_180560_a(minecart, partialTicks, state);

		if (state.getBlock() == Blocks.mob_spawner) {
			TileEntityMobSpawnerRenderer.renderMob(minecart.func_98039_d(), minecart.posX, minecart.posY, minecart.posZ, partialTicks);
		}
	}
}
