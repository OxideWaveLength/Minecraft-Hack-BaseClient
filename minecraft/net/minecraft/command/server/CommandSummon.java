package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "summon";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.summon.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.summon.usage", new Object[0]);
        }
        else
        {
            String s = args[0];
            BlockPos blockpos = sender.getPosition();
            Vec3 vec3 = sender.getPositionVector();
            double d0 = vec3.xCoord;
            double d1 = vec3.yCoord;
            double d2 = vec3.zCoord;

            if (args.length >= 4)
            {
                d0 = parseDouble(d0, args[1], true);
                d1 = parseDouble(d1, args[2], false);
                d2 = parseDouble(d2, args[3], true);
                blockpos = new BlockPos(d0, d1, d2);
            }

            World world = sender.getEntityWorld();

            if (!world.isBlockLoaded(blockpos))
            {
                throw new CommandException("commands.summon.outOfWorld", new Object[0]);
            }
            else if ("LightningBolt".equals(s))
            {
                world.addWeatherEffect(new EntityLightningBolt(world, d0, d1, d2));
                notifyOperators(sender, this, "commands.summon.success", new Object[0]);
            }
            else
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (args.length >= 5)
                {
                    IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 4);

                    try
                    {
                        nbttagcompound = JsonToNBT.getTagFromJson(ichatcomponent.getUnformattedText());
                        flag = true;
                    }
                    catch (NBTException nbtexception)
                    {
                        throw new CommandException("commands.summon.tagError", new Object[] {nbtexception.getMessage()});
                    }
                }

                nbttagcompound.setString("id", s);
                Entity entity2;

                try
                {
                    entity2 = EntityList.createEntityFromNBT(nbttagcompound, world);
                }
                catch (RuntimeException var19)
                {
                    throw new CommandException("commands.summon.failed", new Object[0]);
                }

                if (entity2 == null)
                {
                    throw new CommandException("commands.summon.failed", new Object[0]);
                }
                else
                {
                    entity2.setLocationAndAngles(d0, d1, d2, entity2.rotationYaw, entity2.rotationPitch);

                    if (!flag && entity2 instanceof EntityLiving)
                    {
                        ((EntityLiving)entity2).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity2)), (IEntityLivingData)null);
                    }

                    world.spawnEntityInWorld(entity2);
                    Entity entity = entity2;

                    for (NBTTagCompound nbttagcompound1 = nbttagcompound; entity != null && nbttagcompound1.hasKey("Riding", 10); nbttagcompound1 = nbttagcompound1.getCompoundTag("Riding"))
                    {
                        Entity entity1 = EntityList.createEntityFromNBT(nbttagcompound1.getCompoundTag("Riding"), world);

                        if (entity1 != null)
                        {
                            entity1.setLocationAndAngles(d0, d1, d2, entity1.rotationYaw, entity1.rotationPitch);
                            world.spawnEntityInWorld(entity1);
                            entity.mountEntity(entity1);
                        }

                        entity = entity1;
                    }

                    notifyOperators(sender, this, "commands.summon.success", new Object[0]);
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : null);
    }
}
