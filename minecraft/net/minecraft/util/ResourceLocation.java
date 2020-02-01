package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation
{
    protected final String resourceDomain;
    protected final String resourcePath;

    protected ResourceLocation(int p_i45928_1_, String... resourceName)
    {
        this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
        this.resourcePath = resourceName[1];
        Validate.notNull(this.resourcePath);
    }

    public ResourceLocation(String resourceName)
    {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        this(0, new String[] {resourceDomainIn, resourcePathIn});
    }

    /**
     * Splits an object name (such as minecraft:apple) into the domain and path parts and returns these as an array of
     * length 2. If no colon is present in the passed value the returned array will contain {null, toSplit}.
     */
    protected static String[] splitObjectName(String toSplit)
    {
        String[] astring = new String[] {null, toSplit};
        int i = toSplit.indexOf(58);

        if (i >= 0)
        {
            astring[1] = toSplit.substring(i + 1, toSplit.length());

            if (i > 1)
            {
                astring[0] = toSplit.substring(0, i);
            }
        }

        return astring;
    }

    public String getResourcePath()
    {
        return this.resourcePath;
    }

    public String getResourceDomain()
    {
        return this.resourceDomain;
    }

    public String toString()
    {
        return this.resourceDomain + ':' + this.resourcePath;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ResourceLocation))
        {
            return false;
        }
        else
        {
            ResourceLocation resourcelocation = (ResourceLocation)p_equals_1_;
            return this.resourceDomain.equals(resourcelocation.resourceDomain) && this.resourcePath.equals(resourcelocation.resourcePath);
        }
    }

    public int hashCode()
    {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
