package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle
{
    /**
     * The parent of this ChatStyle.  Used for looking up values that this instance does not override.
     */
    private ChatStyle parentStyle;
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent chatClickEvent;
    private HoverEvent chatHoverEvent;
    private String insertion;

    /**
     * The base of the ChatStyle hierarchy.  All ChatStyle instances are implicitly children of this.
     */
    private static final ChatStyle rootStyle = new ChatStyle()
    {
        public EnumChatFormatting getColor()
        {
            return null;
        }
        public boolean getBold()
        {
            return false;
        }
        public boolean getItalic()
        {
            return false;
        }
        public boolean getStrikethrough()
        {
            return false;
        }
        public boolean getUnderlined()
        {
            return false;
        }
        public boolean getObfuscated()
        {
            return false;
        }
        public ClickEvent getChatClickEvent()
        {
            return null;
        }
        public HoverEvent getChatHoverEvent()
        {
            return null;
        }
        public String getInsertion()
        {
            return null;
        }
        public ChatStyle setColor(EnumChatFormatting color)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setBold(Boolean boldIn)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setItalic(Boolean italic)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setStrikethrough(Boolean strikethrough)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setUnderlined(Boolean underlined)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setObfuscated(Boolean obfuscated)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setChatClickEvent(ClickEvent event)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setChatHoverEvent(HoverEvent event)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setParentStyle(ChatStyle parent)
        {
            throw new UnsupportedOperationException();
        }
        public String toString()
        {
            return "Style.ROOT";
        }
        public ChatStyle createShallowCopy()
        {
            return this;
        }
        public ChatStyle createDeepCopy()
        {
            return this;
        }
        public String getFormattingCode()
        {
            return "";
        }
    };

    /**
     * Gets the effective color of this ChatStyle.
     */
    public EnumChatFormatting getColor()
    {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.chatClickEvent == null && this.chatHoverEvent == null;
    }

    /**
     * The effective chat click event.
     */
    public ClickEvent getChatClickEvent()
    {
        return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
    }

    /**
     * The effective chat hover event.
     */
    public HoverEvent getChatHoverEvent()
    {
        return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
    }

    /**
     * Get the text to be inserted into Chat when the component is shift-clicked
     */
    public String getInsertion()
    {
        return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
    }

    /**
     * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
     * specific methods.
     */
    public ChatStyle setColor(EnumChatFormatting color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
     * and you want text of this style to be unbolded.
     */
    public ChatStyle setBold(Boolean boldIn)
    {
        this.bold = boldIn;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
     * italicized and you want to override that for this style.
     */
    public ChatStyle setItalic(Boolean italic)
    {
        this.italic = italic;
        return this;
    }

    /**
     * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
     * style uses strikethrough and you want to override that for this style.
     */
    public ChatStyle setStrikethrough(Boolean strikethrough)
    {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
     * underlined and you want to override that for this style.
     */
    public ChatStyle setUnderlined(Boolean underlined)
    {
        this.underlined = underlined;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
     * obfuscated and you want to override that for this style.
     */
    public ChatStyle setObfuscated(Boolean obfuscated)
    {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is clicked on.
     */
    public ChatStyle setChatClickEvent(ClickEvent event)
    {
        this.chatClickEvent = event;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is hovered over.
     */
    public ChatStyle setChatHoverEvent(HoverEvent event)
    {
        this.chatHoverEvent = event;
        return this;
    }

    /**
     * Set a text to be inserted into Chat when the component is shift-clicked
     */
    public ChatStyle setInsertion(String insertion)
    {
        this.insertion = insertion;
        return this;
    }

    /**
     * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
     * defaults are used (bold: false, underlined: false, etc).
     */
    public ChatStyle setParentStyle(ChatStyle parent)
    {
        this.parentStyle = parent;
        return this;
    }

    /**
     * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
     */
    public String getFormattingCode()
    {
        if (this.isEmpty())
        {
            return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.getColor() != null)
            {
                stringbuilder.append((Object)this.getColor());
            }

            if (this.getBold())
            {
                stringbuilder.append((Object)EnumChatFormatting.BOLD);
            }

            if (this.getItalic())
            {
                stringbuilder.append((Object)EnumChatFormatting.ITALIC);
            }

            if (this.getUnderlined())
            {
                stringbuilder.append((Object)EnumChatFormatting.UNDERLINE);
            }

            if (this.getObfuscated())
            {
                stringbuilder.append((Object)EnumChatFormatting.OBFUSCATED);
            }

            if (this.getStrikethrough())
            {
                stringbuilder.append((Object)EnumChatFormatting.STRIKETHROUGH);
            }

            return stringbuilder.toString();
        }
    }

    /**
     * Gets the immediate parent of this ChatStyle.
     */
    private ChatStyle getParent()
    {
        return this.parentStyle == null ? rootStyle : this.parentStyle;
    }

    public String toString()
    {
        return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getChatClickEvent() + ", hoverEvent=" + this.getChatHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatStyle))
        {
            return false;
        }
        else
        {
            boolean flag;
            label0:
            {
                ChatStyle chatstyle = (ChatStyle)p_equals_1_;

                if (this.getBold() == chatstyle.getBold() && this.getColor() == chatstyle.getColor() && this.getItalic() == chatstyle.getItalic() && this.getObfuscated() == chatstyle.getObfuscated() && this.getStrikethrough() == chatstyle.getStrikethrough() && this.getUnderlined() == chatstyle.getUnderlined())
                {
                    label85:
                    {
                        if (this.getChatClickEvent() != null)
                        {
                            if (!this.getChatClickEvent().equals(chatstyle.getChatClickEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (chatstyle.getChatClickEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getChatHoverEvent() != null)
                        {
                            if (!this.getChatHoverEvent().equals(chatstyle.getChatHoverEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (chatstyle.getChatHoverEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getInsertion() != null)
                        {
                            if (this.getInsertion().equals(chatstyle.getInsertion()))
                            {
                                break label0;
                            }
                        }
                        else if (chatstyle.getInsertion() == null)
                        {
                            break label0;
                        }
                    }
                }

                flag = false;
                return flag;
            }
            flag = true;
            return flag;
        }
    }

    public int hashCode()
    {
        int i = this.color.hashCode();
        i = 31 * i + this.bold.hashCode();
        i = 31 * i + this.italic.hashCode();
        i = 31 * i + this.underlined.hashCode();
        i = 31 * i + this.strikethrough.hashCode();
        i = 31 * i + this.obfuscated.hashCode();
        i = 31 * i + this.chatClickEvent.hashCode();
        i = 31 * i + this.chatHoverEvent.hashCode();
        i = 31 * i + this.insertion.hashCode();
        return i;
    }

    /**
     * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy, but
     * changes to the parent style's values WILL be reflected in both this instance and the copy, wherever either does
     * not override a value.
     */
    public ChatStyle createShallowCopy()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.bold = this.bold;
        chatstyle.italic = this.italic;
        chatstyle.strikethrough = this.strikethrough;
        chatstyle.underlined = this.underlined;
        chatstyle.obfuscated = this.obfuscated;
        chatstyle.color = this.color;
        chatstyle.chatClickEvent = this.chatClickEvent;
        chatstyle.chatHoverEvent = this.chatHoverEvent;
        chatstyle.parentStyle = this.parentStyle;
        chatstyle.insertion = this.insertion;
        return chatstyle;
    }

    /**
     * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
     * copy.
     */
    public ChatStyle createDeepCopy()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.setBold(Boolean.valueOf(this.getBold()));
        chatstyle.setItalic(Boolean.valueOf(this.getItalic()));
        chatstyle.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
        chatstyle.setUnderlined(Boolean.valueOf(this.getUnderlined()));
        chatstyle.setObfuscated(Boolean.valueOf(this.getObfuscated()));
        chatstyle.setColor(this.getColor());
        chatstyle.setChatClickEvent(this.getChatClickEvent());
        chatstyle.setChatHoverEvent(this.getChatHoverEvent());
        chatstyle.setInsertion(this.getInsertion());
        return chatstyle;
    }

    public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle>
    {
        public ChatStyle deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                ChatStyle chatstyle = new ChatStyle();
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();

                if (jsonobject == null)
                {
                    return null;
                }
                else
                {
                    if (jsonobject.has("bold"))
                    {
                        chatstyle.bold = Boolean.valueOf(jsonobject.get("bold").getAsBoolean());
                    }

                    if (jsonobject.has("italic"))
                    {
                        chatstyle.italic = Boolean.valueOf(jsonobject.get("italic").getAsBoolean());
                    }

                    if (jsonobject.has("underlined"))
                    {
                        chatstyle.underlined = Boolean.valueOf(jsonobject.get("underlined").getAsBoolean());
                    }

                    if (jsonobject.has("strikethrough"))
                    {
                        chatstyle.strikethrough = Boolean.valueOf(jsonobject.get("strikethrough").getAsBoolean());
                    }

                    if (jsonobject.has("obfuscated"))
                    {
                        chatstyle.obfuscated = Boolean.valueOf(jsonobject.get("obfuscated").getAsBoolean());
                    }

                    if (jsonobject.has("color"))
                    {
                        chatstyle.color = (EnumChatFormatting)p_deserialize_3_.deserialize(jsonobject.get("color"), EnumChatFormatting.class);
                    }

                    if (jsonobject.has("insertion"))
                    {
                        chatstyle.insertion = jsonobject.get("insertion").getAsString();
                    }

                    if (jsonobject.has("clickEvent"))
                    {
                        JsonObject jsonobject1 = jsonobject.getAsJsonObject("clickEvent");

                        if (jsonobject1 != null)
                        {
                            JsonPrimitive jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                            ClickEvent.Action clickevent$action = jsonprimitive == null ? null : ClickEvent.Action.getValueByCanonicalName(jsonprimitive.getAsString());
                            JsonPrimitive jsonprimitive1 = jsonobject1.getAsJsonPrimitive("value");
                            String s = jsonprimitive1 == null ? null : jsonprimitive1.getAsString();

                            if (clickevent$action != null && s != null && clickevent$action.shouldAllowInChat())
                            {
                                chatstyle.chatClickEvent = new ClickEvent(clickevent$action, s);
                            }
                        }
                    }

                    if (jsonobject.has("hoverEvent"))
                    {
                        JsonObject jsonobject2 = jsonobject.getAsJsonObject("hoverEvent");

                        if (jsonobject2 != null)
                        {
                            JsonPrimitive jsonprimitive2 = jsonobject2.getAsJsonPrimitive("action");
                            HoverEvent.Action hoverevent$action = jsonprimitive2 == null ? null : HoverEvent.Action.getValueByCanonicalName(jsonprimitive2.getAsString());
                            IChatComponent ichatcomponent = (IChatComponent)p_deserialize_3_.deserialize(jsonobject2.get("value"), IChatComponent.class);

                            if (hoverevent$action != null && ichatcomponent != null && hoverevent$action.shouldAllowInChat())
                            {
                                chatstyle.chatHoverEvent = new HoverEvent(hoverevent$action, ichatcomponent);
                            }
                        }
                    }

                    return chatstyle;
                }
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(ChatStyle p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            if (p_serialize_1_.isEmpty())
            {
                return null;
            }
            else
            {
                JsonObject jsonobject = new JsonObject();

                if (p_serialize_1_.bold != null)
                {
                    jsonobject.addProperty("bold", p_serialize_1_.bold);
                }

                if (p_serialize_1_.italic != null)
                {
                    jsonobject.addProperty("italic", p_serialize_1_.italic);
                }

                if (p_serialize_1_.underlined != null)
                {
                    jsonobject.addProperty("underlined", p_serialize_1_.underlined);
                }

                if (p_serialize_1_.strikethrough != null)
                {
                    jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
                }

                if (p_serialize_1_.obfuscated != null)
                {
                    jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
                }

                if (p_serialize_1_.color != null)
                {
                    jsonobject.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
                }

                if (p_serialize_1_.insertion != null)
                {
                    jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
                }

                if (p_serialize_1_.chatClickEvent != null)
                {
                    JsonObject jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("action", p_serialize_1_.chatClickEvent.getAction().getCanonicalName());
                    jsonobject1.addProperty("value", p_serialize_1_.chatClickEvent.getValue());
                    jsonobject.add("clickEvent", jsonobject1);
                }

                if (p_serialize_1_.chatHoverEvent != null)
                {
                    JsonObject jsonobject2 = new JsonObject();
                    jsonobject2.addProperty("action", p_serialize_1_.chatHoverEvent.getAction().getCanonicalName());
                    jsonobject2.add("value", p_serialize_3_.serialize(p_serialize_1_.chatHoverEvent.getValue()));
                    jsonobject.add("hoverEvent", jsonobject2);
                }

                return jsonobject;
            }
        }
    }
}
