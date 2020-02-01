package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand
{
    private static IAdminCommand theAdmin;

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public List<String> getCommandAliases()
    {
        return Collections.<String>emptyList();
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }

    public static int parseInt(String input) throws NumberInvalidException
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static int parseInt(String input, int min) throws NumberInvalidException
    {
        return parseInt(input, min, Integer.MAX_VALUE);
    }

    public static int parseInt(String input, int min, int max) throws NumberInvalidException
    {
        int i = parseInt(input);

        if (i < min)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(i), Integer.valueOf(min)});
        }
        else if (i > max)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(i), Integer.valueOf(max)});
        }
        else
        {
            return i;
        }
    }

    public static long parseLong(String input) throws NumberInvalidException
    {
        try
        {
            return Long.parseLong(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static long parseLong(String input, long min, long max) throws NumberInvalidException
    {
        long i = parseLong(input);

        if (i < min)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Long.valueOf(i), Long.valueOf(min)});
        }
        else if (i > max)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Long.valueOf(i), Long.valueOf(max)});
        }
        else
        {
            return i;
        }
    }

    public static BlockPos parseBlockPos(ICommandSender sender, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException
    {
        BlockPos blockpos = sender.getPosition();
        return new BlockPos(parseDouble((double)blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), parseDouble((double)blockpos.getY(), args[startIndex + 1], 0, 256, false), parseDouble((double)blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
    }

    public static double parseDouble(String input) throws NumberInvalidException
    {
        try
        {
            double d0 = Double.parseDouble(input);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException var3)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static double parseDouble(String input, double min) throws NumberInvalidException
    {
        return parseDouble(input, min, Double.MAX_VALUE);
    }

    public static double parseDouble(String input, double min, double max) throws NumberInvalidException
    {
        double d0 = parseDouble(input);

        if (d0 < min)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d0), Double.valueOf(min)});
        }
        else if (d0 > max)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d0), Double.valueOf(max)});
        }
        else
        {
            return d0;
        }
    }

    public static boolean parseBoolean(String input) throws CommandException
    {
        if (!input.equals("true") && !input.equals("1"))
        {
            if (!input.equals("false") && !input.equals("0"))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {input});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns the given ICommandSender as a EntityPlayer or throw an exception.
     */
    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) throws PlayerNotFoundException
    {
        if (sender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)sender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(ICommandSender sender, String username) throws PlayerNotFoundException
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(sender, username);

        if (entityplayermp == null)
        {
            try
            {
                entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(UUID.fromString(username));
            }
            catch (IllegalArgumentException var4)
            {
                ;
            }
        }

        if (entityplayermp == null)
        {
            entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
        }

        if (entityplayermp == null)
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return entityplayermp;
        }
    }

    public static Entity func_175768_b(ICommandSender p_175768_0_, String p_175768_1_) throws EntityNotFoundException
    {
        return getEntity(p_175768_0_, p_175768_1_, Entity.class);
    }

    public static <T extends Entity> T getEntity(ICommandSender commandSender, String p_175759_1_, Class <? extends T > p_175759_2_) throws EntityNotFoundException
    {
        Entity entity = PlayerSelector.matchOneEntity(commandSender, p_175759_1_, p_175759_2_);
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (entity == null)
        {
            entity = minecraftserver.getConfigurationManager().getPlayerByUsername(p_175759_1_);
        }

        if (entity == null)
        {
            try
            {
                UUID uuid = UUID.fromString(p_175759_1_);
                entity = minecraftserver.getEntityFromUuid(uuid);

                if (entity == null)
                {
                    entity = minecraftserver.getConfigurationManager().getPlayerByUUID(uuid);
                }
            }
            catch (IllegalArgumentException var6)
            {
                throw new EntityNotFoundException("commands.generic.entity.invalidUuid", new Object[0]);
            }
        }

        if (entity != null && p_175759_2_.isAssignableFrom(entity.getClass()))
        {
            return (T)entity;
        }
        else
        {
            throw new EntityNotFoundException();
        }
    }

    public static List<Entity> func_175763_c(ICommandSender p_175763_0_, String p_175763_1_) throws EntityNotFoundException
    {
        return (List<Entity>)(PlayerSelector.hasArguments(p_175763_1_) ? PlayerSelector.matchEntities(p_175763_0_, p_175763_1_, Entity.class) : Lists.newArrayList(new Entity[] {func_175768_b(p_175763_0_, p_175763_1_)}));
    }

    public static String getPlayerName(ICommandSender sender, String query) throws PlayerNotFoundException
    {
        try
        {
            return getPlayer(sender, query).getName();
        }
        catch (PlayerNotFoundException playernotfoundexception)
        {
            if (PlayerSelector.hasArguments(query))
            {
                throw playernotfoundexception;
            }
            else
            {
                return query;
            }
        }
    }

    /**
     * Attempts to retrieve an entity's name, first assuming that the entity is a player, and then exhausting all other
     * possibilities.
     */
    public static String getEntityName(ICommandSender p_175758_0_, String p_175758_1_) throws EntityNotFoundException
    {
        try
        {
            return getPlayer(p_175758_0_, p_175758_1_).getName();
        }
        catch (PlayerNotFoundException var5)
        {
            try
            {
                return func_175768_b(p_175758_0_, p_175758_1_).getUniqueID().toString();
            }
            catch (EntityNotFoundException entitynotfoundexception)
            {
                if (PlayerSelector.hasArguments(p_175758_1_))
                {
                    throw entitynotfoundexception;
                }
                else
                {
                    return p_175758_1_;
                }
            }
        }
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int p_147178_2_) throws CommandException, PlayerNotFoundException
    {
        return getChatComponentFromNthArg(sender, args, p_147178_2_, false);
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean p_147176_3_) throws PlayerNotFoundException
    {
        IChatComponent ichatcomponent = new ChatComponentText("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                ichatcomponent.appendText(" ");
            }

            IChatComponent ichatcomponent1 = new ChatComponentText(args[i]);

            if (p_147176_3_)
            {
                IChatComponent ichatcomponent2 = PlayerSelector.matchEntitiesToChatComponent(sender, args[i]);

                if (ichatcomponent2 == null)
                {
                    if (PlayerSelector.hasArguments(args[i]))
                    {
                        throw new PlayerNotFoundException();
                    }
                }
                else
                {
                    ichatcomponent1 = ichatcomponent2;
                }
            }

            ichatcomponent.appendSibling(ichatcomponent1);
        }

        return ichatcomponent;
    }

    /**
     * Builds a string starting at startPos
     */
    public static String buildString(String[] args, int startPos)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = startPos; i < args.length; ++i)
        {
            if (i > startPos)
            {
                stringbuilder.append(" ");
            }

            String s = args[i];
            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static CommandBase.CoordinateArg parseCoordinate(double base, String p_175770_2_, boolean centerBlock) throws NumberInvalidException
    {
        return parseCoordinate(base, p_175770_2_, -30000000, 30000000, centerBlock);
    }

    public static CommandBase.CoordinateArg parseCoordinate(double p_175767_0_, String p_175767_2_, int min, int max, boolean centerBlock) throws NumberInvalidException
    {
        boolean flag = p_175767_2_.startsWith("~");

        if (flag && Double.isNaN(p_175767_0_))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(p_175767_0_)});
        }
        else
        {
            double d0 = 0.0D;

            if (!flag || p_175767_2_.length() > 1)
            {
                boolean flag1 = p_175767_2_.contains(".");

                if (flag)
                {
                    p_175767_2_ = p_175767_2_.substring(1);
                }

                d0 += parseDouble(p_175767_2_);

                if (!flag1 && !flag && centerBlock)
                {
                    d0 += 0.5D;
                }
            }

            if (min != 0 || max != 0)
            {
                if (d0 < (double)min)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d0), Integer.valueOf(min)});
                }

                if (d0 > (double)max)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d0), Integer.valueOf(max)});
                }
            }

            return new CommandBase.CoordinateArg(d0 + (flag ? p_175767_0_ : 0.0D), d0, flag);
        }
    }

    public static double parseDouble(double base, String input, boolean centerBlock) throws NumberInvalidException
    {
        return parseDouble(base, input, -30000000, 30000000, centerBlock);
    }

    public static double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws NumberInvalidException
    {
        boolean flag = input.startsWith("~");

        if (flag && Double.isNaN(base))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(base)});
        }
        else
        {
            double d0 = flag ? base : 0.0D;

            if (!flag || input.length() > 1)
            {
                boolean flag1 = input.contains(".");

                if (flag)
                {
                    input = input.substring(1);
                }

                d0 += parseDouble(input);

                if (!flag1 && !flag && centerBlock)
                {
                    d0 += 0.5D;
                }
            }

            if (min != 0 || max != 0)
            {
                if (d0 < (double)min)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d0), Integer.valueOf(min)});
                }

                if (d0 > (double)max)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d0), Integer.valueOf(max)});
                }
            }

            return d0;
        }
    }

    /**
     * Gets the Item specified by the given text string.  First checks the item registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the item
     * wasn't found.  Returns the item if it was found.
     */
    public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException
    {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        Item item = (Item)Item.itemRegistry.getObject(resourcelocation);

        if (item == null)
        {
            throw new NumberInvalidException("commands.give.item.notFound", new Object[] {resourcelocation});
        }
        else
        {
            return item;
        }
    }

    /**
     * Gets the Block specified by the given text string.  First checks the block registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the block
     * wasn't found.  Returns the block if it was found.
     */
    public static Block getBlockByText(ICommandSender sender, String id) throws NumberInvalidException
    {
        ResourceLocation resourcelocation = new ResourceLocation(id);

        if (!Block.blockRegistry.containsKey(resourcelocation))
        {
            throw new NumberInvalidException("commands.give.block.notFound", new Object[] {resourcelocation});
        }
        else
        {
            Block block = (Block)Block.blockRegistry.getObject(resourcelocation);

            if (block == null)
            {
                throw new NumberInvalidException("commands.give.block.notFound", new Object[] {resourcelocation});
            }
            else
            {
                return block;
            }
        }
    }

    /**
     * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
     * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
     */
    public static String joinNiceString(Object[] elements)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < elements.length; ++i)
        {
            String s = elements[i].toString();

            if (i > 0)
            {
                if (i == elements.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static IChatComponent join(List<IChatComponent> components)
    {
        IChatComponent ichatcomponent = new ChatComponentText("");

        for (int i = 0; i < components.size(); ++i)
        {
            if (i > 0)
            {
                if (i == components.size() - 1)
                {
                    ichatcomponent.appendText(" and ");
                }
                else if (i > 0)
                {
                    ichatcomponent.appendText(", ");
                }
            }

            ichatcomponent.appendSibling((IChatComponent)components.get(i));
        }

        return ichatcomponent;
    }

    /**
     * Creates a linguistic series joining together the elements of the given collection.  Examples: 1) {} --> "",  2)
     * {"Steve"} --> "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil
     * and Mark"
     */
    public static String joinNiceStringFromCollection(Collection<String> strings)
    {
        return joinNiceString(strings.toArray(new String[strings.size()]));
    }

    public static List<String> func_175771_a(String[] p_175771_0_, int p_175771_1_, BlockPos p_175771_2_)
    {
        if (p_175771_2_ == null)
        {
            return null;
        }
        else
        {
            int i = p_175771_0_.length - 1;
            String s;

            if (i == p_175771_1_)
            {
                s = Integer.toString(p_175771_2_.getX());
            }
            else if (i == p_175771_1_ + 1)
            {
                s = Integer.toString(p_175771_2_.getY());
            }
            else
            {
                if (i != p_175771_1_ + 2)
                {
                    return null;
                }

                s = Integer.toString(p_175771_2_.getZ());
            }

            return Lists.newArrayList(new String[] {s});
        }
    }

    public static List<String> func_181043_b(String[] p_181043_0_, int p_181043_1_, BlockPos p_181043_2_)
    {
        if (p_181043_2_ == null)
        {
            return null;
        }
        else
        {
            int i = p_181043_0_.length - 1;
            String s;

            if (i == p_181043_1_)
            {
                s = Integer.toString(p_181043_2_.getX());
            }
            else
            {
                if (i != p_181043_1_ + 1)
                {
                    return null;
                }

                s = Integer.toString(p_181043_2_.getZ());
            }

            return Lists.newArrayList(new String[] {s});
        }
    }

    /**
     * Returns true if the given substring is exactly equal to the start of the given string (case insensitive).
     */
    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] p_175762_0_, Collection<?> p_175762_1_)
    {
        String s = p_175762_0_[p_175762_0_.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!p_175762_1_.isEmpty())
        {
            for (String s1 : Iterables.transform(p_175762_1_, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }

            if (list.isEmpty())
            {
                for (Object object : p_175762_1_)
                {
                    if (object instanceof ResourceLocation && doesStringStartWith(s, ((ResourceLocation)object).getResourcePath()))
                    {
                        list.add(String.valueOf(object));
                    }
                }
            }
        }

        return list;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object... msgParams)
    {
        notifyOperators(sender, command, 0, msgFormat, msgParams);
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, int p_152374_2_, String msgFormat, Object... msgParams)
    {
        if (theAdmin != null)
        {
            theAdmin.notifyOperators(sender, command, p_152374_2_, msgFormat, msgParams);
        }
    }

    /**
     * Sets the static IAdminCommander.
     */
    public static void setAdminCommander(IAdminCommand command)
    {
        theAdmin = command;
    }

    public int compareTo(ICommand p_compareTo_1_)
    {
        return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
    }

    public static class CoordinateArg
    {
        private final double field_179633_a;
        private final double field_179631_b;
        private final boolean field_179632_c;

        protected CoordinateArg(double p_i46051_1_, double p_i46051_3_, boolean p_i46051_5_)
        {
            this.field_179633_a = p_i46051_1_;
            this.field_179631_b = p_i46051_3_;
            this.field_179632_c = p_i46051_5_;
        }

        public double func_179628_a()
        {
            return this.field_179633_a;
        }

        public double func_179629_b()
        {
            return this.field_179631_b;
        }

        public boolean func_179630_c()
        {
            return this.field_179632_c;
        }
    }
}
