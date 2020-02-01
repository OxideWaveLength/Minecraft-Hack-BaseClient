package net.minecraft.client.particle;

import net.minecraft.world.World;

public interface IParticleFactory
{
    EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_);
}
