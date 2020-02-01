package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListOpsEntry extends UserListEntry<GameProfile>
{
    private final int field_152645_a;
    private final boolean field_183025_b;

    public UserListOpsEntry(GameProfile p_i46492_1_, int p_i46492_2_, boolean p_i46492_3_)
    {
        super(p_i46492_1_);
        this.field_152645_a = p_i46492_2_;
        this.field_183025_b = p_i46492_3_;
    }

    public UserListOpsEntry(JsonObject p_i1150_1_)
    {
        super(func_152643_b(p_i1150_1_), p_i1150_1_);
        this.field_152645_a = p_i1150_1_.has("level") ? p_i1150_1_.get("level").getAsInt() : 0;
        this.field_183025_b = p_i1150_1_.has("bypassesPlayerLimit") && p_i1150_1_.get("bypassesPlayerLimit").getAsBoolean();
    }

    /**
     * Gets the permission level of the user, as defined in the "level" attribute of the ops.json file
     */
    public int getPermissionLevel()
    {
        return this.field_152645_a;
    }

    public boolean func_183024_b()
    {
        return this.field_183025_b;
    }

    protected void onSerialization(JsonObject data)
    {
        if (this.getValue() != null)
        {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
            data.addProperty("level", (Number)Integer.valueOf(this.field_152645_a));
            data.addProperty("bypassesPlayerLimit", Boolean.valueOf(this.field_183025_b));
        }
    }

    private static GameProfile func_152643_b(JsonObject p_152643_0_)
    {
        if (p_152643_0_.has("uuid") && p_152643_0_.has("name"))
        {
            String s = p_152643_0_.get("uuid").getAsString();
            UUID uuid;

            try
            {
                uuid = UUID.fromString(s);
            }
            catch (Throwable var4)
            {
                return null;
            }

            return new GameProfile(uuid, p_152643_0_.get("name").getAsString());
        }
        else
        {
            return null;
        }
    }
}
