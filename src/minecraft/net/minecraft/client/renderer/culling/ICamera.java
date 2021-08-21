package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public interface ICamera {
	/**
	 * Returns true if the bounding box is inside all 6 clipping planes, otherwise
	 * returns false.
	 */
	boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_);

	void setPosition(double p_78547_1_, double p_78547_3_, double p_78547_5_);
}
