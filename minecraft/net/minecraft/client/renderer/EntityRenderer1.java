package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;

class EntityRenderer1 implements Predicate
{
    final EntityRenderer field_90032_a;

    EntityRenderer1(EntityRenderer p_i1243_1_)
    {
        this.field_90032_a = p_i1243_1_;
    }

    public boolean apply(Entity p_apply_1_)
    {
        return p_apply_1_.canBeCollidedWith();
    }

    public boolean apply(Object p_apply_1_)
    {
        return this.apply((Entity)p_apply_1_);
    }
}
