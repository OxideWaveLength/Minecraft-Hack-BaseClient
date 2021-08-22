package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow {
	public EntityMooshroom(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.3F);
		this.spawnableBlock = Blocks.mycelium;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets
	 * into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer player) {
		ItemStack itemstack = player.inventory.getCurrentItem();

		if (itemstack != null && itemstack.getItem() == Items.bowl && this.getGrowingAge() >= 0) {
			if (itemstack.stackSize == 1) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.mushroom_stew));
				return true;
			}

			if (player.inventory.addItemStackToInventory(new ItemStack(Items.mushroom_stew)) && !player.capabilities.isCreativeMode) {
				player.inventory.decrStackSize(player.inventory.currentItem, 1);
				return true;
			}
		}

		if (itemstack != null && itemstack.getItem() == Items.shears && this.getGrowingAge() >= 0) {
			this.setDead();
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + (double) (this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);

			if (!this.worldObj.isRemote) {
				EntityCow entitycow = new EntityCow(this.worldObj);
				entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				entitycow.setHealth(this.getHealth());
				entitycow.renderYawOffset = this.renderYawOffset;

				if (this.hasCustomName()) {
					entitycow.setCustomNameTag(this.getCustomNameTag());
				}

				this.worldObj.spawnEntityInWorld(entitycow);

				for (int i = 0; i < 5; ++i) {
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + (double) this.height, this.posZ, new ItemStack(Blocks.red_mushroom)));
				}

				itemstack.damageItem(1, player);
				this.playSound("mob.sheep.shear", 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.interact(player);
		}
	}

	public EntityMooshroom createChild(EntityAgeable ageable) {
		return new EntityMooshroom(this.worldObj);
	}
}
