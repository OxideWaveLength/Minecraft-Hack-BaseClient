package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem extends CommandBase
{
    private static final Map<String, Integer> SHORTCUTS = Maps.<String, Integer>newHashMap();

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "replaceitem";
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
        return "commands.replaceitem.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
        }
        else
        {
            boolean flag;

            if (args[0].equals("entity"))
            {
                flag = false;
            }
            else
            {
                if (!args[0].equals("block"))
                {
                    throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
                }

                flag = true;
            }

            int i;

            if (flag)
            {
                if (args.length < 6)
                {
                    throw new WrongUsageException("commands.replaceitem.block.usage", new Object[0]);
                }

                i = 4;
            }
            else
            {
                if (args.length < 4)
                {
                    throw new WrongUsageException("commands.replaceitem.entity.usage", new Object[0]);
                }

                i = 2;
            }

            int j = this.getSlotForShortcut(args[i++]);
            Item item;

            try
            {
                item = getItemByText(sender, args[i]);
            }
            catch (NumberInvalidException numberinvalidexception)
            {
                if (Block.getBlockFromName(args[i]) != Blocks.air)
                {
                    throw numberinvalidexception;
                }

                item = null;
            }

            ++i;
            int k = args.length > i ? parseInt(args[i++], 1, 64) : 1;
            int l = args.length > i ? parseInt(args[i++]) : 0;
            ItemStack itemstack = new ItemStack(item, k, l);

            if (args.length > i)
            {
                String s = getChatComponentFromNthArg(sender, args, i).getUnformattedText();

                try
                {
                    itemstack.setTagCompound(JsonToNBT.getTagFromJson(s));
                }
                catch (NBTException nbtexception)
                {
                    throw new CommandException("commands.replaceitem.tagError", new Object[] {nbtexception.getMessage()});
                }
            }

            if (itemstack.getItem() == null)
            {
                itemstack = null;
            }

            if (flag)
            {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                World world = sender.getEntityWorld();
                TileEntity tileentity = world.getTileEntity(blockpos);

                if (tileentity == null || !(tileentity instanceof IInventory))
                {
                    throw new CommandException("commands.replaceitem.noContainer", new Object[] {Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ())});
                }

                IInventory iinventory = (IInventory)tileentity;

                if (j >= 0 && j < iinventory.getSizeInventory())
                {
                    iinventory.setInventorySlotContents(j, itemstack);
                }
            }
            else
            {
                Entity entity = func_175768_b(sender, args[1]);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

                if (entity instanceof EntityPlayer)
                {
                    ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
                }

                if (!entity.replaceItemInInventory(j, itemstack))
                {
                    throw new CommandException("commands.replaceitem.failed", new Object[] {Integer.valueOf(j), Integer.valueOf(k), itemstack == null ? "Air" : itemstack.getChatComponent()});
                }

                if (entity instanceof EntityPlayer)
                {
                    ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
                }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k);
            notifyOperators(sender, this, "commands.replaceitem.success", new Object[] {Integer.valueOf(j), Integer.valueOf(k), itemstack == null ? "Air" : itemstack.getChatComponent()});
        }
    }

    private int getSlotForShortcut(String shortcut) throws CommandException
    {
        if (!SHORTCUTS.containsKey(shortcut))
        {
            throw new CommandException("commands.generic.parameter.invalid", new Object[] {shortcut});
        }
        else
        {
            return ((Integer)SHORTCUTS.get(shortcut)).intValue();
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"entity", "block"}): (args.length == 2 && args[0].equals("entity") ? getListOfStringsMatchingLastWord(args, this.getUsernames()) : (args.length >= 2 && args.length <= 4 && args[0].equals("block") ? func_175771_a(args, 1, pos) : ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block")) ? ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block")) ? null : getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys())) : getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet()))));
    }

    protected String[] getUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && args[0].equals("entity") && index == 1;
    }

    static
    {
        for (int i = 0; i < 54; ++i)
        {
            SHORTCUTS.put("slot.container." + i, Integer.valueOf(i));
        }

        for (int j = 0; j < 9; ++j)
        {
            SHORTCUTS.put("slot.hotbar." + j, Integer.valueOf(j));
        }

        for (int k = 0; k < 27; ++k)
        {
            SHORTCUTS.put("slot.inventory." + k, Integer.valueOf(9 + k));
        }

        for (int l = 0; l < 27; ++l)
        {
            SHORTCUTS.put("slot.enderchest." + l, Integer.valueOf(200 + l));
        }

        for (int i1 = 0; i1 < 8; ++i1)
        {
            SHORTCUTS.put("slot.villager." + i1, Integer.valueOf(300 + i1));
        }

        for (int j1 = 0; j1 < 15; ++j1)
        {
            SHORTCUTS.put("slot.horse." + j1, Integer.valueOf(500 + j1));
        }

        SHORTCUTS.put("slot.weapon", Integer.valueOf(99));
        SHORTCUTS.put("slot.armor.head", Integer.valueOf(103));
        SHORTCUTS.put("slot.armor.chest", Integer.valueOf(102));
        SHORTCUTS.put("slot.armor.legs", Integer.valueOf(101));
        SHORTCUTS.put("slot.armor.feet", Integer.valueOf(100));
        SHORTCUTS.put("slot.horse.saddle", Integer.valueOf(400));
        SHORTCUTS.put("slot.horse.armor", Integer.valueOf(401));
        SHORTCUTS.put("slot.horse.chest", Integer.valueOf(499));
    }
}
