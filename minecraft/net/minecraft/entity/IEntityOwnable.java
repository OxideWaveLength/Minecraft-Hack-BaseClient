package net.minecraft.entity;

public interface IEntityOwnable
{
    String getOwnerId();

    Entity getOwner();
}
