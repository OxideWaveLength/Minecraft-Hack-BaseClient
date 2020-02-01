package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource
{
    private Entity indirectEntity;

    public EntityDamageSourceIndirect(String p_i1568_1_, Entity p_i1568_2_, Entity indirectEntityIn)
    {
        super(p_i1568_1_, p_i1568_2_);
        this.indirectEntity = indirectEntityIn;
    }

    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    public Entity getEntity()
    {
        return this.indirectEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public IChatComponent getDeathMessage(EntityLivingBase p_151519_1_)
    {
        IChatComponent ichatcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
        ItemStack itemstack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] {p_151519_1_.getDisplayName(), ichatcomponent, itemstack.getChatComponent()}): new ChatComponentTranslation(s, new Object[] {p_151519_1_.getDisplayName(), ichatcomponent});
    }
}
