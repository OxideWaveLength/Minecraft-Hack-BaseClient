package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure
{
    private int field_175800_f;
    private int field_175801_g;
    public static final List<BiomeGenBase> field_175802_d = Arrays.<BiomeGenBase>asList(new BiomeGenBase[] {BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver});
    private static final List<BiomeGenBase.SpawnListEntry> field_175803_h = Lists.<BiomeGenBase.SpawnListEntry>newArrayList();

    public StructureOceanMonument()
    {
        this.field_175800_f = 32;
        this.field_175801_g = 5;
    }

    public StructureOceanMonument(Map<String, String> p_i45608_1_)
    {
        this();

        for (Entry<String, String> entry : p_i45608_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("spacing"))
            {
                this.field_175800_f = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_175800_f, 1);
            }
            else if (((String)entry.getKey()).equals("separation"))
            {
                this.field_175801_g = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_175801_g, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.field_175800_f - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.field_175800_f - 1;
        }

        int k = chunkX / this.field_175800_f;
        int l = chunkZ / this.field_175800_f;
        Random random = this.worldObj.setRandomSeed(k, l, 10387313);
        k = k * this.field_175800_f;
        l = l * this.field_175800_f;
        k = k + (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
        l = l + (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;

        if (i == k && j == l)
        {
            if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 64, j * 16 + 8), (BiomeGenBase)null) != BiomeGenBase.deepOcean)
            {
                return false;
            }

            boolean flag = this.worldObj.getWorldChunkManager().areBiomesViable(i * 16 + 8, j * 16 + 8, 29, field_175802_d);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public List<BiomeGenBase.SpawnListEntry> func_175799_b()
    {
        return field_175803_h;
    }

    static
    {
        field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }

    public static class StartMonument extends StructureStart
    {
        private Set<ChunkCoordIntPair> field_175791_c = Sets.<ChunkCoordIntPair>newHashSet();
        private boolean field_175790_d;

        public StartMonument()
        {
        }

        public StartMonument(World worldIn, Random p_i45607_2_, int p_i45607_3_, int p_i45607_4_)
        {
            super(p_i45607_3_, p_i45607_4_);
            this.func_175789_b(worldIn, p_i45607_2_, p_i45607_3_, p_i45607_4_);
        }

        private void func_175789_b(World worldIn, Random p_175789_2_, int p_175789_3_, int p_175789_4_)
        {
            p_175789_2_.setSeed(worldIn.getSeed());
            long i = p_175789_2_.nextLong();
            long j = p_175789_2_.nextLong();
            long k = (long)p_175789_3_ * i;
            long l = (long)p_175789_4_ * j;
            p_175789_2_.setSeed(k ^ l ^ worldIn.getSeed());
            int i1 = p_175789_3_ * 16 + 8 - 29;
            int j1 = p_175789_4_ * 16 + 8 - 29;
            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
            this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(p_175789_2_, i1, j1, enumfacing));
            this.updateBoundingBox();
            this.field_175790_d = true;
        }

        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
        {
            if (!this.field_175790_d)
            {
                this.components.clear();
                this.func_175789_b(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
            }

            super.generateStructure(worldIn, rand, structurebb);
        }

        public boolean func_175788_a(ChunkCoordIntPair pair)
        {
            return this.field_175791_c.contains(pair) ? false : super.func_175788_a(pair);
        }

        public void func_175787_b(ChunkCoordIntPair pair)
        {
            super.func_175787_b(pair);
            this.field_175791_c.add(pair);
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            NBTTagList nbttaglist = new NBTTagList();

            for (ChunkCoordIntPair chunkcoordintpair : this.field_175791_c)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("X", chunkcoordintpair.chunkXPos);
                nbttagcompound.setInteger("Z", chunkcoordintpair.chunkZPos);
                nbttaglist.appendTag(nbttagcompound);
            }

            tagCompound.setTag("Processed", nbttaglist);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);

            if (tagCompound.hasKey("Processed", 9))
            {
                NBTTagList nbttaglist = tagCompound.getTagList("Processed", 10);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                    this.field_175791_c.add(new ChunkCoordIntPair(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Z")));
                }
            }
        }
    }
}
