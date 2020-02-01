package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition
{
    static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer()).create();
    private final Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.<String, ModelBlockDefinition.Variants>newHashMap();

    public static ModelBlockDefinition parseFromReader(Reader p_178331_0_)
    {
        return (ModelBlockDefinition)GSON.fromJson(p_178331_0_, ModelBlockDefinition.class);
    }

    public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> p_i46221_1_)
    {
        for (ModelBlockDefinition.Variants modelblockdefinition$variants : p_i46221_1_)
        {
            this.mapVariants.put(modelblockdefinition$variants.name, modelblockdefinition$variants);
        }
    }

    public ModelBlockDefinition(List<ModelBlockDefinition> p_i46222_1_)
    {
        for (ModelBlockDefinition modelblockdefinition : p_i46222_1_)
        {
            this.mapVariants.putAll(modelblockdefinition.mapVariants);
        }
    }

    public ModelBlockDefinition.Variants getVariants(String p_178330_1_)
    {
        ModelBlockDefinition.Variants modelblockdefinition$variants = (ModelBlockDefinition.Variants)this.mapVariants.get(p_178330_1_);

        if (modelblockdefinition$variants == null)
        {
            throw new ModelBlockDefinition.MissingVariantException();
        }
        else
        {
            return modelblockdefinition$variants;
        }
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ instanceof ModelBlockDefinition)
        {
            ModelBlockDefinition modelblockdefinition = (ModelBlockDefinition)p_equals_1_;
            return this.mapVariants.equals(modelblockdefinition.mapVariants);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.mapVariants.hashCode();
    }

    public static class Deserializer implements JsonDeserializer<ModelBlockDefinition>
    {
        public ModelBlockDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            List<ModelBlockDefinition.Variants> list = this.parseVariantsList(p_deserialize_3_, jsonobject);
            return new ModelBlockDefinition(list);
        }

        protected List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext p_178334_1_, JsonObject p_178334_2_)
        {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_178334_2_, "variants");
            List<ModelBlockDefinition.Variants> list = Lists.<ModelBlockDefinition.Variants>newArrayList();

            for (Entry<String, JsonElement> entry : jsonobject.entrySet())
            {
                list.add(this.parseVariants(p_178334_1_, entry));
            }

            return list;
        }

        protected ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext p_178335_1_, Entry<String, JsonElement> p_178335_2_)
        {
            String s = (String)p_178335_2_.getKey();
            List<ModelBlockDefinition.Variant> list = Lists.<ModelBlockDefinition.Variant>newArrayList();
            JsonElement jsonelement = (JsonElement)p_178335_2_.getValue();

            if (jsonelement.isJsonArray())
            {
                for (JsonElement jsonelement1 : jsonelement.getAsJsonArray())
                {
                    list.add((ModelBlockDefinition.Variant)p_178335_1_.deserialize(jsonelement1, ModelBlockDefinition.Variant.class));
                }
            }
            else
            {
                list.add((ModelBlockDefinition.Variant)p_178335_1_.deserialize(jsonelement, ModelBlockDefinition.Variant.class));
            }

            return new ModelBlockDefinition.Variants(s, list);
        }
    }

    public class MissingVariantException extends RuntimeException
    {
    }

    public static class Variant
    {
        private final ResourceLocation modelLocation;
        private final ModelRotation modelRotation;
        private final boolean uvLock;
        private final int weight;

        public Variant(ResourceLocation modelLocationIn, ModelRotation modelRotationIn, boolean uvLockIn, int weightIn)
        {
            this.modelLocation = modelLocationIn;
            this.modelRotation = modelRotationIn;
            this.uvLock = uvLockIn;
            this.weight = weightIn;
        }

        public ResourceLocation getModelLocation()
        {
            return this.modelLocation;
        }

        public ModelRotation getRotation()
        {
            return this.modelRotation;
        }

        public boolean isUvLocked()
        {
            return this.uvLock;
        }

        public int getWeight()
        {
            return this.weight;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variant))
            {
                return false;
            }
            else
            {
                ModelBlockDefinition.Variant modelblockdefinition$variant = (ModelBlockDefinition.Variant)p_equals_1_;
                return this.modelLocation.equals(modelblockdefinition$variant.modelLocation) && this.modelRotation == modelblockdefinition$variant.modelRotation && this.uvLock == modelblockdefinition$variant.uvLock;
            }
        }

        public int hashCode()
        {
            int i = this.modelLocation.hashCode();
            i = 31 * i + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
            i = 31 * i + (this.uvLock ? 1 : 0);
            return i;
        }

        public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant>
        {
            public ModelBlockDefinition.Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                String s = this.parseModel(jsonobject);
                ModelRotation modelrotation = this.parseRotation(jsonobject);
                boolean flag = this.parseUvLock(jsonobject);
                int i = this.parseWeight(jsonobject);
                return new ModelBlockDefinition.Variant(this.makeModelLocation(s), modelrotation, flag, i);
            }

            private ResourceLocation makeModelLocation(String p_178426_1_)
            {
                ResourceLocation resourcelocation = new ResourceLocation(p_178426_1_);
                resourcelocation = new ResourceLocation(resourcelocation.getResourceDomain(), "block/" + resourcelocation.getResourcePath());
                return resourcelocation;
            }

            private boolean parseUvLock(JsonObject p_178429_1_)
            {
                return JsonUtils.getBoolean(p_178429_1_, "uvlock", false);
            }

            protected ModelRotation parseRotation(JsonObject p_178428_1_)
            {
                int i = JsonUtils.getInt(p_178428_1_, "x", 0);
                int j = JsonUtils.getInt(p_178428_1_, "y", 0);
                ModelRotation modelrotation = ModelRotation.getModelRotation(i, j);

                if (modelrotation == null)
                {
                    throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
                }
                else
                {
                    return modelrotation;
                }
            }

            protected String parseModel(JsonObject p_178424_1_)
            {
                return JsonUtils.getString(p_178424_1_, "model");
            }

            protected int parseWeight(JsonObject p_178427_1_)
            {
                return JsonUtils.getInt(p_178427_1_, "weight", 1);
            }
        }
    }

    public static class Variants
    {
        private final String name;
        private final List<ModelBlockDefinition.Variant> listVariants;

        public Variants(String nameIn, List<ModelBlockDefinition.Variant> listVariantsIn)
        {
            this.name = nameIn;
            this.listVariants = listVariantsIn;
        }

        public List<ModelBlockDefinition.Variant> getVariants()
        {
            return this.listVariants;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variants))
            {
                return false;
            }
            else
            {
                ModelBlockDefinition.Variants modelblockdefinition$variants = (ModelBlockDefinition.Variants)p_equals_1_;
                return !this.name.equals(modelblockdefinition$variants.name) ? false : this.listVariants.equals(modelblockdefinition$variants.listVariants);
            }
        }

        public int hashCode()
        {
            int i = this.name.hashCode();
            i = 31 * i + this.listVariants.hashCode();
            return i;
        }
    }
}
