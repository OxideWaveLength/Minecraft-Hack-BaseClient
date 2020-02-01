package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface ItemMeshDefinition
{
    ModelResourceLocation getModelLocation(ItemStack stack);
}
