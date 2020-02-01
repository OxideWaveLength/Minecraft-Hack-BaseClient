package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import optfine.BlockPosM;
import optfine.Reflector;

public class RenderChunk
{
    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    private BlockPos position;
    public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
    private final ReentrantLock lockCompileTask = new ReentrantLock();
    private final ReentrantLock lockCompiledChunk = new ReentrantLock();
    private ChunkCompileTaskGenerator compileTask = null;
    private final Set field_181056_j = Sets.newHashSet();
    private final int index;
    private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final VertexBuffer[] vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
    public AxisAlignedBB boundingBox;
    private int frameIndex = -1;
    private boolean needsUpdate = true;
    private EnumMap field_181702_p;
    private static final String __OBFID = "CL_00002452";
    private BlockPos[] positionOffsets16 = new BlockPos[EnumFacing.VALUES.length];
    private static EnumWorldBlockLayer[] ENUM_WORLD_BLOCK_LAYERS = EnumWorldBlockLayer.values();
    private EnumWorldBlockLayer[] blockLayersSingle = new EnumWorldBlockLayer[1];

    public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, BlockPos blockPosIn, int indexIn)
    {
        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;

        if (!blockPosIn.equals(this.getPosition()))
        {
            this.setPosition(blockPosIn);
        }

        if (OpenGlHelper.useVbo())
        {
            for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i)
            {
                this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
            }
        }
    }

    public boolean setFrameIndex(int frameIndexIn)
    {
        if (this.frameIndex == frameIndexIn)
        {
            return false;
        }
        else
        {
            this.frameIndex = frameIndexIn;
            return true;
        }
    }

    public VertexBuffer getVertexBufferByLayer(int layer)
    {
        return this.vertexBuffers[layer];
    }

    public void setPosition(BlockPos pos)
    {
        this.stopCompileTask();
        this.position = pos;
        this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;
        this.initModelviewMatrix();

        for (int j = 0; j < this.positionOffsets16.length; ++j)
        {
            this.positionOffsets16[j] = null;
        }
    }

    public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = generator.getCompiledChunk();

        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT))
        {
            WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
            this.preRenderBlocks(worldrenderer, this.position);
            worldrenderer.setVertexState(compiledchunk.getState());
            this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, worldrenderer, compiledchunk);
        }
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = new CompiledChunk();
        boolean flag = true;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);
        generator.getLock().lock();
        RegionRenderCache regionrendercache;

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            if (this.world == null)
            {
                return;
            }

            regionrendercache = new RegionRenderCache(this.world, blockpos.add(-1, -1, -1), blockpos1.add(1, 1, 1), 1);
            generator.setCompiledChunk(compiledchunk);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph var10 = new VisGraph();
        HashSet var11 = Sets.newHashSet();

        if (!regionrendercache.extendedLevelsInChunkCache())
        {
            ++renderChunksUpdated;
            boolean[] aboolean = new boolean[EnumWorldBlockLayer.values().length];
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            Iterator iterator = BlockPosM.getAllInBoxMutable(blockpos, blockpos1).iterator();
            boolean flag1 = Reflector.ForgeBlock_hasTileEntity.exists();
            boolean flag2 = Reflector.ForgeBlock_canRenderInLayer.exists();
            boolean flag3 = Reflector.ForgeHooksClient_setRenderLayer.exists();

            while (iterator.hasNext())
            {
                BlockPosM blockposm = (BlockPosM)iterator.next();
                IBlockState iblockstate = regionrendercache.getBlockState(blockposm);
                Block block = iblockstate.getBlock();

                if (block.isOpaqueCube())
                {
                    var10.func_178606_a(blockposm);
                }

                boolean flag4;

                if (flag1)
                {
                    flag4 = Reflector.callBoolean(iterator, Reflector.ForgeBlock_hasTileEntity, new Object[] {blockrendererdispatcher});
                }
                else
                {
                    flag4 = block.hasTileEntity();
                }

                if (flag4)
                {
                    TileEntity tileentity = regionrendercache.getTileEntity(new BlockPos(blockposm));
                    TileEntitySpecialRenderer tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileentity);

                    if (tileentity != null && tileentityspecialrenderer != null)
                    {
                        compiledchunk.addTileEntity(tileentity);

                        if (tileentityspecialrenderer.func_181055_a())
                        {
                            var11.add(tileentity);
                        }
                    }
                }

                EnumWorldBlockLayer[] aenumworldblocklayer;

                if (flag2)
                {
                    aenumworldblocklayer = ENUM_WORLD_BLOCK_LAYERS;
                }
                else
                {
                    aenumworldblocklayer = this.blockLayersSingle;
                    aenumworldblocklayer[0] = block.getBlockLayer();
                }

                for (int i = 0; i < aenumworldblocklayer.length; ++i)
                {
                    EnumWorldBlockLayer enumworldblocklayer = aenumworldblocklayer[i];

                    if (flag2)
                    {
                        boolean flag5 = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, new Object[] {enumworldblocklayer});

                        if (!flag5)
                        {
                            continue;
                        }
                    }

                    enumworldblocklayer = this.fixBlockLayer(block, enumworldblocklayer);

                    if (flag3)
                    {
                        Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, new Object[] {enumworldblocklayer});
                    }

                    int j = enumworldblocklayer.ordinal();

                    if (block.getRenderType() != -1)
                    {
                        WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);
                        worldrenderer.setBlockLayer(enumworldblocklayer);

                        if (!compiledchunk.isLayerStarted(enumworldblocklayer))
                        {
                            compiledchunk.setLayerStarted(enumworldblocklayer);
                            this.preRenderBlocks(worldrenderer, blockpos);
                        }

                        aboolean[j] |= blockrendererdispatcher.renderBlock(iblockstate, blockposm, regionrendercache, worldrenderer);
                    }
                }
            }

            for (EnumWorldBlockLayer enumworldblocklayer1 : EnumWorldBlockLayer.values())
            {
                if (aboolean[enumworldblocklayer1.ordinal()])
                {
                    compiledchunk.setLayerUsed(enumworldblocklayer1);
                }

                if (compiledchunk.isLayerStarted(enumworldblocklayer1))
                {
                    this.postRenderBlocks(enumworldblocklayer1, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer1), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(var10.computeVisibility());
        this.lockCompileTask.lock();

        try
        {
            HashSet hashset1 = Sets.newHashSet(var11);
            HashSet hashset2 = Sets.newHashSet(this.field_181056_j);
            hashset1.removeAll(this.field_181056_j);
            hashset2.removeAll(var11);
            this.field_181056_j.clear();
            this.field_181056_j.addAll(var11);
            this.renderGlobal.func_181023_a(hashset2, hashset1);
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    protected void finishCompileTask()
    {
        this.lockCompileTask.lock();

        try
        {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    public ReentrantLock getLockCompileTask()
    {
        return this.lockCompileTask;
    }

    public ChunkCompileTaskGenerator makeCompileTaskChunk()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;

        try
        {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
            chunkcompiletaskgenerator = this.compileTask;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator;
    }

    public ChunkCompileTaskGenerator makeCompileTaskTransparency()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator1;

        try
        {
            if (this.compileTask != null && this.compileTask.getStatus() == ChunkCompileTaskGenerator.Status.PENDING)
            {
                ChunkCompileTaskGenerator chunkcompiletaskgenerator2 = null;
                return chunkcompiletaskgenerator2;
            }

            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.compileTask.finish();
                this.compileTask = null;
            }

            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
            this.compileTask.setCompiledChunk(this.compiledChunk);
            ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.compileTask;
            chunkcompiletaskgenerator1 = chunkcompiletaskgenerator;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator1;
    }

    private void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos)
    {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
        worldRendererIn.setTranslation((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
    }

    private void postRenderBlocks(EnumWorldBlockLayer layer, float x, float y, float z, WorldRenderer worldRendererIn, CompiledChunk compiledChunkIn)
    {
        if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer))
        {
            worldRendererIn.func_181674_a(x, y, z);
            compiledChunkIn.setState(worldRendererIn.func_181672_a());
        }

        worldRendererIn.finishDrawing();
    }

    private void initModelviewMatrix()
    {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        float f = 1.000001F;
        GlStateManager.translate(-8.0F, -8.0F, -8.0F);
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }

    public void multModelviewMatrix()
    {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.lockCompiledChunk.lock();

        try
        {
            this.compiledChunk = compiledChunkIn;
        }
        finally
        {
            this.lockCompiledChunk.unlock();
        }
    }

    public void stopCompileTask()
    {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }

    public void deleteGlResources()
    {
        this.stopCompileTask();
        this.world = null;

        for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i)
        {
            if (this.vertexBuffers[i] != null)
            {
                this.vertexBuffers[i].deleteGlBuffers();
            }
        }
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public void setNeedsUpdate(boolean needsUpdateIn)
    {
        this.needsUpdate = needsUpdateIn;
    }

    public boolean isNeedsUpdate()
    {
        return this.needsUpdate;
    }

    public BlockPos func_181701_a(EnumFacing p_181701_1_)
    {
        return this.getPositionOffset16(p_181701_1_);
    }

    public BlockPos getPositionOffset16(EnumFacing p_getPositionOffset16_1_)
    {
        int i = p_getPositionOffset16_1_.getIndex();
        BlockPos blockpos = this.positionOffsets16[i];

        if (blockpos == null)
        {
            blockpos = this.getPosition().offset(p_getPositionOffset16_1_, 16);
            this.positionOffsets16[i] = blockpos;
        }

        return blockpos;
    }

    private EnumWorldBlockLayer fixBlockLayer(Block p_fixBlockLayer_1_, EnumWorldBlockLayer p_fixBlockLayer_2_)
    {
        return p_fixBlockLayer_2_ == EnumWorldBlockLayer.CUTOUT ? (p_fixBlockLayer_1_ instanceof BlockRedstoneWire ? p_fixBlockLayer_2_ : (p_fixBlockLayer_1_ instanceof BlockCactus ? p_fixBlockLayer_2_ : EnumWorldBlockLayer.CUTOUT_MIPPED)) : p_fixBlockLayer_2_;
    }
}
