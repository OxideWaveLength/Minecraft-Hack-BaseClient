package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle
{
    private final String key;
    private final Object[] formatArgs;
    private final Object syncLock = new Object();
    private long lastTranslationUpdateTimeInMilliseconds = -1L;
    List<IChatComponent> children = Lists.<IChatComponent>newArrayList();
    public static final Pattern stringVariablePattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public ChatComponentTranslation(String translationKey, Object... args)
    {
        this.key = translationKey;
        this.formatArgs = args;

        for (Object object : args)
        {
            if (object instanceof IChatComponent)
            {
                ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
            }
        }
    }

    /**
     * ensures that our children are initialized from the most recent string translation mapping.
     */
    synchronized void ensureInitialized()
    {
        synchronized (this.syncLock)
        {
            long i = StatCollector.getLastTranslationUpdateTimeInMilliseconds();

            if (i == this.lastTranslationUpdateTimeInMilliseconds)
            {
                return;
            }

            this.lastTranslationUpdateTimeInMilliseconds = i;
            this.children.clear();
        }

        try
        {
            this.initializeFromFormat(StatCollector.translateToLocal(this.key));
        }
        catch (ChatComponentTranslationFormatException chatcomponenttranslationformatexception)
        {
            this.children.clear();

            try
            {
                this.initializeFromFormat(StatCollector.translateToFallback(this.key));
            }
            catch (ChatComponentTranslationFormatException var5)
            {
                throw chatcomponenttranslationformatexception;
            }
        }
    }

    /**
     * initializes our children from a format string, using the format args to fill in the placeholder variables.
     */
    protected void initializeFromFormat(String format)
    {
        boolean flag = false;
        Matcher matcher = stringVariablePattern.matcher(format);
        int i = 0;
        int j = 0;

        try
        {
            int l;

            for (; matcher.find(j); j = l)
            {
                int k = matcher.start();
                l = matcher.end();

                if (k > j)
                {
                    ChatComponentText chatcomponenttext = new ChatComponentText(String.format(format.substring(j, k), new Object[0]));
                    chatcomponenttext.getChatStyle().setParentStyle(this.getChatStyle());
                    this.children.add(chatcomponenttext);
                }

                String s2 = matcher.group(2);
                String s = format.substring(k, l);

                if ("%".equals(s2) && "%%".equals(s))
                {
                    ChatComponentText chatcomponenttext2 = new ChatComponentText("%");
                    chatcomponenttext2.getChatStyle().setParentStyle(this.getChatStyle());
                    this.children.add(chatcomponenttext2);
                }
                else
                {
                    if (!"s".equals(s2))
                    {
                        throw new ChatComponentTranslationFormatException(this, "Unsupported format: \'" + s + "\'");
                    }

                    String s1 = matcher.group(1);
                    int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;

                    if (i1 < this.formatArgs.length)
                    {
                        this.children.add(this.getFormatArgumentAsComponent(i1));
                    }
                }
            }

            if (j < format.length())
            {
                ChatComponentText chatcomponenttext1 = new ChatComponentText(String.format(format.substring(j), new Object[0]));
                chatcomponenttext1.getChatStyle().setParentStyle(this.getChatStyle());
                this.children.add(chatcomponenttext1);
            }
        }
        catch (IllegalFormatException illegalformatexception)
        {
            throw new ChatComponentTranslationFormatException(this, illegalformatexception);
        }
    }

    private IChatComponent getFormatArgumentAsComponent(int index)
    {
        if (index >= this.formatArgs.length)
        {
            throw new ChatComponentTranslationFormatException(this, index);
        }
        else
        {
            Object object = this.formatArgs[index];
            IChatComponent ichatcomponent;

            if (object instanceof IChatComponent)
            {
                ichatcomponent = (IChatComponent)object;
            }
            else
            {
                ichatcomponent = new ChatComponentText(object == null ? "null" : object.toString());
                ichatcomponent.getChatStyle().setParentStyle(this.getChatStyle());
            }

            return ichatcomponent;
        }
    }

    public IChatComponent setChatStyle(ChatStyle style)
    {
        super.setChatStyle(style);

        for (Object object : this.formatArgs)
        {
            if (object instanceof IChatComponent)
            {
                ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
            }
        }

        if (this.lastTranslationUpdateTimeInMilliseconds > -1L)
        {
            for (IChatComponent ichatcomponent : this.children)
            {
                ichatcomponent.getChatStyle().setParentStyle(style);
            }
        }

        return this;
    }

    public Iterator<IChatComponent> iterator()
    {
        this.ensureInitialized();
        return Iterators.<IChatComponent>concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedTextForChat()
    {
        this.ensureInitialized();
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent ichatcomponent : this.children)
        {
            stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
        }

        return stringbuilder.toString();
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public ChatComponentTranslation createCopy()
    {
        Object[] aobject = new Object[this.formatArgs.length];

        for (int i = 0; i < this.formatArgs.length; ++i)
        {
            if (this.formatArgs[i] instanceof IChatComponent)
            {
                aobject[i] = ((IChatComponent)this.formatArgs[i]).createCopy();
            }
            else
            {
                aobject[i] = this.formatArgs[i];
            }
        }

        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(this.key, aobject);
        chatcomponenttranslation.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent ichatcomponent : this.getSiblings())
        {
            chatcomponenttranslation.appendSibling(ichatcomponent.createCopy());
        }

        return chatcomponenttranslation;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentTranslation))
        {
            return false;
        }
        else
        {
            ChatComponentTranslation chatcomponenttranslation = (ChatComponentTranslation)p_equals_1_;
            return Arrays.equals(this.formatArgs, chatcomponenttranslation.formatArgs) && this.key.equals(chatcomponenttranslation.key) && super.equals(p_equals_1_);
        }
    }

    public int hashCode()
    {
        int i = super.hashCode();
        i = 31 * i + this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.formatArgs);
        return i;
    }

    public String toString()
    {
        return "TranslatableComponent{key=\'" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }

    public String getKey()
    {
        return this.key;
    }

    public Object[] getFormatArgs()
    {
        return this.formatArgs;
    }
}
