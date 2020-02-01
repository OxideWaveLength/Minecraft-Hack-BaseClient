package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT
{
    private static final Logger logger = LogManager.getLogger();
    private static final Pattern field_179273_b = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

    public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException
    {
        jsonString = jsonString.trim();

        if (!jsonString.startsWith("{"))
        {
            throw new NBTException("Invalid tag encountered, expected \'{\' as first char.");
        }
        else if (func_150310_b(jsonString) != 1)
        {
            throw new NBTException("Encountered multiple top tags, only one expected");
        }
        else
        {
            return (NBTTagCompound)func_150316_a("tag", jsonString).parse();
        }
    }

    static int func_150310_b(String p_150310_0_) throws NBTException
    {
        int i = 0;
        boolean flag = false;
        Stack<Character> stack = new Stack();

        for (int j = 0; j < p_150310_0_.length(); ++j)
        {
            char c0 = p_150310_0_.charAt(j);

            if (c0 == 34)
            {
                if (func_179271_b(p_150310_0_, j))
                {
                    if (!flag)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_150310_0_);
                    }
                }
                else
                {
                    flag = !flag;
                }
            }
            else if (!flag)
            {
                if (c0 != 123 && c0 != 91)
                {
                    if (c0 == 125 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_150310_0_);
                    }

                    if (c0 == 93 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_150310_0_);
                    }
                }
                else
                {
                    if (stack.isEmpty())
                    {
                        ++i;
                    }

                    stack.push(Character.valueOf(c0));
                }
            }
        }

        if (flag)
        {
            throw new NBTException("Unbalanced quotation: " + p_150310_0_);
        }
        else if (!stack.isEmpty())
        {
            throw new NBTException("Unbalanced brackets: " + p_150310_0_);
        }
        else
        {
            if (i == 0 && !p_150310_0_.isEmpty())
            {
                i = 1;
            }

            return i;
        }
    }

    static JsonToNBT.Any func_179272_a(String... p_179272_0_) throws NBTException
    {
        return func_150316_a(p_179272_0_[0], p_179272_0_[1]);
    }

    static JsonToNBT.Any func_150316_a(String p_150316_0_, String p_150316_1_) throws NBTException
    {
        p_150316_1_ = p_150316_1_.trim();

        if (p_150316_1_.startsWith("{"))
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.Compound jsontonbt$compound;
            String s1;

            for (jsontonbt$compound = new JsonToNBT.Compound(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(s1.length() + 1))
            {
                s1 = func_150314_a(p_150316_1_, true);

                if (s1.length() > 0)
                {
                    boolean flag1 = false;
                    jsontonbt$compound.field_150491_b.add(func_179270_a(s1, flag1));
                }

                if (p_150316_1_.length() < s1.length() + 1)
                {
                    break;
                }

                char c1 = p_150316_1_.charAt(s1.length());

                if (c1 != 44 && c1 != 123 && c1 != 125 && c1 != 91 && c1 != 93)
                {
                    throw new NBTException("Unexpected token \'" + c1 + "\' at: " + p_150316_1_.substring(s1.length()));
                }
            }

            return jsontonbt$compound;
        }
        else if (p_150316_1_.startsWith("[") && !field_179273_b.matcher(p_150316_1_).matches())
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.List jsontonbt$list;
            String s;

            for (jsontonbt$list = new JsonToNBT.List(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(s.length() + 1))
            {
                s = func_150314_a(p_150316_1_, false);

                if (s.length() > 0)
                {
                    boolean flag = true;
                    jsontonbt$list.field_150492_b.add(func_179270_a(s, flag));
                }

                if (p_150316_1_.length() < s.length() + 1)
                {
                    break;
                }

                char c0 = p_150316_1_.charAt(s.length());

                if (c0 != 44 && c0 != 123 && c0 != 125 && c0 != 91 && c0 != 93)
                {
                    throw new NBTException("Unexpected token \'" + c0 + "\' at: " + p_150316_1_.substring(s.length()));
                }
            }

            return jsontonbt$list;
        }
        else
        {
            return new JsonToNBT.Primitive(p_150316_0_, p_150316_1_);
        }
    }

    private static JsonToNBT.Any func_179270_a(String p_179270_0_, boolean p_179270_1_) throws NBTException
    {
        String s = func_150313_b(p_179270_0_, p_179270_1_);
        String s1 = func_150311_c(p_179270_0_, p_179270_1_);
        return func_179272_a(new String[] {s, s1});
    }

    private static String func_150314_a(String p_150314_0_, boolean p_150314_1_) throws NBTException
    {
        int i = func_150312_a(p_150314_0_, ':');
        int j = func_150312_a(p_150314_0_, ',');

        if (p_150314_1_)
        {
            if (i == -1)
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150314_0_);
            }

            if (j != -1 && j < i)
            {
                throw new NBTException("Name error at: " + p_150314_0_);
            }
        }
        else if (i == -1 || i > j)
        {
            i = -1;
        }

        return func_179269_a(p_150314_0_, i);
    }

    private static String func_179269_a(String p_179269_0_, int p_179269_1_) throws NBTException
    {
        Stack<Character> stack = new Stack();
        int i = p_179269_1_ + 1;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;

        for (int j = 0; i < p_179269_0_.length(); ++i)
        {
            char c0 = p_179269_0_.charAt(i);

            if (c0 == 34)
            {
                if (func_179271_b(p_179269_0_, i))
                {
                    if (!flag)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_179269_0_);
                    }
                }
                else
                {
                    flag = !flag;

                    if (flag && !flag2)
                    {
                        flag1 = true;
                    }

                    if (!flag)
                    {
                        j = i;
                    }
                }
            }
            else if (!flag)
            {
                if (c0 != 123 && c0 != 91)
                {
                    if (c0 == 125 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_179269_0_);
                    }

                    if (c0 == 93 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_179269_0_);
                    }

                    if (c0 == 44 && stack.isEmpty())
                    {
                        return p_179269_0_.substring(0, i);
                    }
                }
                else
                {
                    stack.push(Character.valueOf(c0));
                }
            }

            if (!Character.isWhitespace(c0))
            {
                if (!flag && flag1 && j != i)
                {
                    return p_179269_0_.substring(0, j + 1);
                }

                flag2 = true;
            }
        }

        return p_179269_0_.substring(0, i);
    }

    private static String func_150313_b(String p_150313_0_, boolean p_150313_1_) throws NBTException
    {
        if (p_150313_1_)
        {
            p_150313_0_ = p_150313_0_.trim();

            if (p_150313_0_.startsWith("{") || p_150313_0_.startsWith("["))
            {
                return "";
            }
        }

        int i = func_150312_a(p_150313_0_, ':');

        if (i == -1)
        {
            if (p_150313_1_)
            {
                return "";
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150313_0_);
            }
        }
        else
        {
            return p_150313_0_.substring(0, i).trim();
        }
    }

    private static String func_150311_c(String p_150311_0_, boolean p_150311_1_) throws NBTException
    {
        if (p_150311_1_)
        {
            p_150311_0_ = p_150311_0_.trim();

            if (p_150311_0_.startsWith("{") || p_150311_0_.startsWith("["))
            {
                return p_150311_0_;
            }
        }

        int i = func_150312_a(p_150311_0_, ':');

        if (i == -1)
        {
            if (p_150311_1_)
            {
                return p_150311_0_;
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150311_0_);
            }
        }
        else
        {
            return p_150311_0_.substring(i + 1).trim();
        }
    }

    private static int func_150312_a(String p_150312_0_, char p_150312_1_)
    {
        int i = 0;

        for (boolean flag = true; i < p_150312_0_.length(); ++i)
        {
            char c0 = p_150312_0_.charAt(i);

            if (c0 == 34)
            {
                if (!func_179271_b(p_150312_0_, i))
                {
                    flag = !flag;
                }
            }
            else if (flag)
            {
                if (c0 == p_150312_1_)
                {
                    return i;
                }

                if (c0 == 123 || c0 == 91)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    private static boolean func_179271_b(String p_179271_0_, int p_179271_1_)
    {
        return p_179271_1_ > 0 && p_179271_0_.charAt(p_179271_1_ - 1) == 92 && !func_179271_b(p_179271_0_, p_179271_1_ - 1);
    }

    abstract static class Any
    {
        protected String json;

        public abstract NBTBase parse() throws NBTException;
    }

    static class Compound extends JsonToNBT.Any
    {
        protected java.util.List<JsonToNBT.Any> field_150491_b = Lists.<JsonToNBT.Any>newArrayList();

        public Compound(String p_i45137_1_)
        {
            this.json = p_i45137_1_;
        }

        public NBTBase parse() throws NBTException
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            for (JsonToNBT.Any jsontonbt$any : this.field_150491_b)
            {
                nbttagcompound.setTag(jsontonbt$any.json, jsontonbt$any.parse());
            }

            return nbttagcompound;
        }
    }

    static class List extends JsonToNBT.Any
    {
        protected java.util.List<JsonToNBT.Any> field_150492_b = Lists.<JsonToNBT.Any>newArrayList();

        public List(String json)
        {
            this.json = json;
        }

        public NBTBase parse() throws NBTException
        {
            NBTTagList nbttaglist = new NBTTagList();

            for (JsonToNBT.Any jsontonbt$any : this.field_150492_b)
            {
                nbttaglist.appendTag(jsontonbt$any.parse());
            }

            return nbttaglist;
        }
    }

    static class Primitive extends JsonToNBT.Any
    {
        private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
        private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
        private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
        private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
        private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
        private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
        private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
        protected String jsonValue;

        public Primitive(String p_i45139_1_, String p_i45139_2_)
        {
            this.json = p_i45139_1_;
            this.jsonValue = p_i45139_2_;
        }

        public NBTBase parse() throws NBTException
        {
            try
            {
                if (DOUBLE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (FLOAT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (BYTE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (LONG.matcher(this.jsonValue).matches())
                {
                    return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (SHORT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (INTEGER.matcher(this.jsonValue).matches())
                {
                    return new NBTTagInt(Integer.parseInt(this.jsonValue));
                }

                if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue));
                }

                if (this.jsonValue.equalsIgnoreCase("true") || this.jsonValue.equalsIgnoreCase("false"))
                {
                    return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
                }
            }
            catch (NumberFormatException var6)
            {
                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                return new NBTTagString(this.jsonValue);
            }

            if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]"))
            {
                String s = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                String[] astring = (String[])Iterables.toArray(SPLITTER.split(s), String.class);

                try
                {
                    int[] aint = new int[astring.length];

                    for (int j = 0; j < astring.length; ++j)
                    {
                        aint[j] = Integer.parseInt(astring[j].trim());
                    }

                    return new NBTTagIntArray(aint);
                }
                catch (NumberFormatException var5)
                {
                    return new NBTTagString(this.jsonValue);
                }
            }
            else
            {
                if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\""))
                {
                    this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                }

                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                StringBuilder stringbuilder = new StringBuilder();

                for (int i = 0; i < this.jsonValue.length(); ++i)
                {
                    if (i < this.jsonValue.length() - 1 && this.jsonValue.charAt(i) == 92 && this.jsonValue.charAt(i + 1) == 92)
                    {
                        stringbuilder.append('\\');
                        ++i;
                    }
                    else
                    {
                        stringbuilder.append(this.jsonValue.charAt(i));
                    }
                }

                return new NBTTagString(stringbuilder.toString());
            }
        }
    }
}
