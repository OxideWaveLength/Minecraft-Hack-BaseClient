package net.minecraft.util;

public class LongHashMap
{
    /** the array of all elements in the hash */
    private transient LongHashMap.Entry[] hashArray = new LongHashMap.Entry[4096];

    /** the number of elements in the hash array */
    private transient int numHashElements;
    private int mask;

    /**
     * the maximum amount of elements in the hash (probably 3/4 the size due to meh hashing function)
     */
    private int capacity = 3072;

    /**
     * percent of the hasharray that can be used without hash colliding probably
     */
    private final float percentUseable = 0.75F;

    /** count of times elements have been added/removed */
    private transient volatile int modCount;
    private static final String __OBFID = "CL_00001492";

    public LongHashMap()
    {
        this.mask = this.hashArray.length - 1;
    }

    /**
     * returns the hashed key given the original key
     */
    private static int getHashedKey(long originalKey)
    {
        return (int)(originalKey ^ originalKey >>> 27);
    }

    /**
     * the hash function
     */
    private static int hash(int integer)
    {
        integer = integer ^ integer >>> 20 ^ integer >>> 12;
        return integer ^ integer >>> 7 ^ integer >>> 4;
    }

    /**
     * gets the index in the hash given the array length and the hashed key
     */
    private static int getHashIndex(int p_76158_0_, int p_76158_1_)
    {
        return p_76158_0_ & p_76158_1_;
    }

    public int getNumHashElements()
    {
        return this.numHashElements;
    }

    /**
     * get the value from the map given the key
     */
    public Object getValueByKey(long p_76164_1_)
    {
        int i = getHashedKey(p_76164_1_);

        for (LongHashMap.Entry longhashmap$entry = this.hashArray[getHashIndex(i, this.mask)]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.nextEntry)
        {
            if (longhashmap$entry.key == p_76164_1_)
            {
                return longhashmap$entry.value;
            }
        }

        return null;
    }

    public boolean containsItem(long p_76161_1_)
    {
        return this.getEntry(p_76161_1_) != null;
    }

    final LongHashMap.Entry getEntry(long p_76160_1_)
    {
        int i = getHashedKey(p_76160_1_);

        for (LongHashMap.Entry longhashmap$entry = this.hashArray[getHashIndex(i, this.mask)]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.nextEntry)
        {
            if (longhashmap$entry.key == p_76160_1_)
            {
                return longhashmap$entry;
            }
        }

        return null;
    }

    /**
     * Add a key-value pair.
     */
    public void add(long p_76163_1_, Object p_76163_3_)
    {
        int i = getHashedKey(p_76163_1_);
        int j = getHashIndex(i, this.mask);

        for (LongHashMap.Entry longhashmap$entry = this.hashArray[j]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.nextEntry)
        {
            if (longhashmap$entry.key == p_76163_1_)
            {
                longhashmap$entry.value = p_76163_3_;
                return;
            }
        }

        ++this.modCount;
        this.createKey(i, p_76163_1_, p_76163_3_, j);
    }

    /**
     * resizes the table
     */
    private void resizeTable(int p_76153_1_)
    {
        LongHashMap.Entry[] alonghashmap$entry = this.hashArray;
        int i = alonghashmap$entry.length;

        if (i == 1073741824)
        {
            this.capacity = Integer.MAX_VALUE;
        }
        else
        {
            LongHashMap.Entry[] alonghashmap$entry1 = new LongHashMap.Entry[p_76153_1_];
            this.copyHashTableTo(alonghashmap$entry1);
            this.hashArray = alonghashmap$entry1;
            this.mask = this.hashArray.length - 1;
            float f = (float)p_76153_1_;
            this.getClass();
            this.capacity = (int)(f * 0.75F);
        }
    }

    /**
     * copies the hash table to the specified array
     */
    private void copyHashTableTo(LongHashMap.Entry[] p_76154_1_)
    {
        LongHashMap.Entry[] alonghashmap$entry = this.hashArray;
        int i = p_76154_1_.length;

        for (int j = 0; j < alonghashmap$entry.length; ++j)
        {
            LongHashMap.Entry longhashmap$entry = alonghashmap$entry[j];

            if (longhashmap$entry != null)
            {
                alonghashmap$entry[j] = null;

                while (true)
                {
                    LongHashMap.Entry longhashmap$entry1 = longhashmap$entry.nextEntry;
                    int k = getHashIndex(longhashmap$entry.hash, i - 1);
                    longhashmap$entry.nextEntry = p_76154_1_[k];
                    p_76154_1_[k] = longhashmap$entry;
                    longhashmap$entry = longhashmap$entry1;

                    if (longhashmap$entry1 == null)
                    {
                        break;
                    }
                }
            }
        }
    }

    /**
     * calls the removeKey method and returns removed object
     */
    public Object remove(long p_76159_1_)
    {
        LongHashMap.Entry longhashmap$entry = this.removeKey(p_76159_1_);
        return longhashmap$entry == null ? null : longhashmap$entry.value;
    }

    /**
     * removes the key from the hash linked list
     */
    final LongHashMap.Entry removeKey(long p_76152_1_)
    {
        int i = getHashedKey(p_76152_1_);
        int j = getHashIndex(i, this.mask);
        LongHashMap.Entry longhashmap$entry = this.hashArray[j];
        LongHashMap.Entry longhashmap$entry1;
        LongHashMap.Entry longhashmap$entry2;

        for (longhashmap$entry1 = longhashmap$entry; longhashmap$entry1 != null; longhashmap$entry1 = longhashmap$entry2)
        {
            longhashmap$entry2 = longhashmap$entry1.nextEntry;

            if (longhashmap$entry1.key == p_76152_1_)
            {
                ++this.modCount;
                --this.numHashElements;

                if (longhashmap$entry == longhashmap$entry1)
                {
                    this.hashArray[j] = longhashmap$entry2;
                }
                else
                {
                    longhashmap$entry.nextEntry = longhashmap$entry2;
                }

                return longhashmap$entry1;
            }

            longhashmap$entry = longhashmap$entry1;
        }

        return longhashmap$entry1;
    }

    /**
     * creates the key in the hash table
     */
    private void createKey(int p_76156_1_, long p_76156_2_, Object p_76156_4_, int p_76156_5_)
    {
        LongHashMap.Entry longhashmap$entry = this.hashArray[p_76156_5_];
        this.hashArray[p_76156_5_] = new LongHashMap.Entry(p_76156_1_, p_76156_2_, p_76156_4_, longhashmap$entry);

        if (this.numHashElements++ >= this.capacity)
        {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    public double getKeyDistribution()
    {
        int i = 0;

        for (int j = 0; j < this.hashArray.length; ++j)
        {
            if (this.hashArray[j] != null)
            {
                ++i;
            }
        }

        return 1.0D * (double)i / (double)this.numHashElements;
    }

    static class Entry
    {
        final long key;
        Object value;
        LongHashMap.Entry nextEntry;
        final int hash;
        private static final String __OBFID = "CL_00001493";

        Entry(int p_i1553_1_, long p_i1553_2_, Object p_i1553_4_, LongHashMap.Entry p_i1553_5_)
        {
            this.value = p_i1553_4_;
            this.nextEntry = p_i1553_5_;
            this.key = p_i1553_2_;
            this.hash = p_i1553_1_;
        }

        public final long getKey()
        {
            return this.key;
        }

        public final Object getValue()
        {
            return this.value;
        }

        public final boolean equals(Object p_equals_1_)
        {
            if (!(p_equals_1_ instanceof LongHashMap.Entry))
            {
                return false;
            }
            else
            {
                LongHashMap.Entry longhashmap$entry = (LongHashMap.Entry)p_equals_1_;
                Long olong = Long.valueOf(this.getKey());
                Long olong1 = Long.valueOf(longhashmap$entry.getKey());

                if (olong == olong1 || olong != null && olong.equals(olong1))
                {
                    Object object = this.getValue();
                    Object object1 = longhashmap$entry.getValue();

                    if (object == object1 || object != null && object.equals(object1))
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode()
        {
            return LongHashMap.getHashedKey(this.key);
        }

        public final String toString()
        {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
