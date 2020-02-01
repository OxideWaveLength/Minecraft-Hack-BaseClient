package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent
{
    protected List<IChatComponent> siblings = Lists.<IChatComponent>newArrayList();
    private ChatStyle style;

    /**
     * Appends the given component to the end of this one.
     */
    public IChatComponent appendSibling(IChatComponent component)
    {
        component.getChatStyle().setParentStyle(this.getChatStyle());
        this.siblings.add(component);
        return this;
    }

    public List<IChatComponent> getSiblings()
    {
        return this.siblings;
    }

    /**
     * Appends the given text to the end of this component.
     */
    public IChatComponent appendText(String text)
    {
        return this.appendSibling(new ChatComponentText(text));
    }

    public IChatComponent setChatStyle(ChatStyle style)
    {
        this.style = style;

        for (IChatComponent ichatcomponent : this.siblings)
        {
            ichatcomponent.getChatStyle().setParentStyle(this.getChatStyle());
        }

        return this;
    }

    public ChatStyle getChatStyle()
    {
        if (this.style == null)
        {
            this.style = new ChatStyle();

            for (IChatComponent ichatcomponent : this.siblings)
            {
                ichatcomponent.getChatStyle().setParentStyle(this.style);
            }
        }

        return this.style;
    }

    public Iterator<IChatComponent> iterator()
    {
        return Iterators.<IChatComponent>concat(Iterators.<IChatComponent>forArray(new ChatComponentStyle[] {this}), createDeepCopyIterator(this.siblings));
    }

    /**
     * Get the text of this component, <em>and all child components</em>, with all special formatting codes removed.
     */
    public final String getUnformattedText()
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent ichatcomponent : this)
        {
            stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
        }

        return stringbuilder.toString();
    }

    /**
     * Gets the text of this component, with formatting codes added for rendering.
     */
    public final String getFormattedText()
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent ichatcomponent : this)
        {
            stringbuilder.append(ichatcomponent.getChatStyle().getFormattingCode());
            stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
            stringbuilder.append((Object)EnumChatFormatting.RESET);
        }

        return stringbuilder.toString();
    }

    public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components)
    {
        Iterator<IChatComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>()
        {
            public Iterator<IChatComponent> apply(IChatComponent p_apply_1_)
            {
                return p_apply_1_.iterator();
            }
        }));
        iterator = Iterators.transform(iterator, new Function<IChatComponent, IChatComponent>()
        {
            public IChatComponent apply(IChatComponent p_apply_1_)
            {
                IChatComponent ichatcomponent = p_apply_1_.createCopy();
                ichatcomponent.setChatStyle(ichatcomponent.getChatStyle().createDeepCopy());
                return ichatcomponent;
            }
        });
        return iterator;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentStyle))
        {
            return false;
        }
        else
        {
            ChatComponentStyle chatcomponentstyle = (ChatComponentStyle)p_equals_1_;
            return this.siblings.equals(chatcomponentstyle.siblings) && this.getChatStyle().equals(chatcomponentstyle.getChatStyle());
        }
    }

    public int hashCode()
    {
        return 31 * this.style.hashCode() + this.siblings.hashCode();
    }

    public String toString()
    {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
