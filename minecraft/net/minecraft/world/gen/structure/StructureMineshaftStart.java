package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class StructureMineshaftStart extends StructureStart
{
    public StructureMineshaftStart()
    {
    }

    public StructureMineshaftStart(World worldIn, Random rand, int chunkX, int chunkZ)
    {
        super(chunkX, chunkZ);
        StructureMineshaftPieces.Room structuremineshaftpieces$room = new StructureMineshaftPieces.Room(0, rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
        this.components.add(structuremineshaftpieces$room);
        structuremineshaftpieces$room.buildComponent(structuremineshaftpieces$room, this.components, rand);
        this.updateBoundingBox();
        this.markAvailableHeight(worldIn, rand, 10);
    }
}
