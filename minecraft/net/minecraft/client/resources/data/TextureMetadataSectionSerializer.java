package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection>
{
    public TextureMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        boolean flag = JsonUtils.getBoolean(jsonobject, "blur", false);
        boolean flag1 = JsonUtils.getBoolean(jsonobject, "clamp", false);
        List<Integer> list = Lists.<Integer>newArrayList();

        if (jsonobject.has("mipmaps"))
        {
            try
            {
                JsonArray jsonarray = jsonobject.getAsJsonArray("mipmaps");

                for (int i = 0; i < jsonarray.size(); ++i)
                {
                    JsonElement jsonelement = jsonarray.get(i);

                    if (jsonelement.isJsonPrimitive())
                    {
                        try
                        {
                            list.add(Integer.valueOf(jsonelement.getAsInt()));
                        }
                        catch (NumberFormatException numberformatexception)
                        {
                            throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement, numberformatexception);
                        }
                    }
                    else if (jsonelement.isJsonObject())
                    {
                        throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement);
                    }
                }
            }
            catch (ClassCastException classcastexception)
            {
                throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonobject.get("mipmaps"), classcastexception);
            }
        }

        return new TextureMetadataSection(flag, flag1, list);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "texture";
    }
}
