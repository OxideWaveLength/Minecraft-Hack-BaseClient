package net.minecraft.util;

public class ChatComponentTranslationFormatException extends IllegalArgumentException
{
    public ChatComponentTranslationFormatException(ChatComponentTranslation component, String message)
    {
        super(String.format("Error parsing: %s: %s", new Object[] {component, message}));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation component, int index)
    {
        super(String.format("Invalid index %d requested for %s", new Object[] {Integer.valueOf(index), component}));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation component, Throwable cause)
    {
        super(String.format("Error while parsing: %s", new Object[] {component}), cause);
    }
}
