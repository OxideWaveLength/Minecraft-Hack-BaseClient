package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T>
{
    private static final Set < Class<? >> field_181158_a = Sets. < Class<? >> newHashSet();
    private final Map < Class<?>, List<T >> map = Maps. < Class<?>, List<T >> newHashMap();
    private final Set < Class<? >> knownKeys = Sets. < Class<? >> newIdentityHashSet();
    private final Class<T> baseClass;
    private final List<T> field_181745_e = Lists.<T>newArrayList();

    public ClassInheritanceMultiMap(Class<T> baseClassIn)
    {
        this.baseClass = baseClassIn;
        this.knownKeys.add(baseClassIn);
        this.map.put(baseClassIn, this.field_181745_e);

        for (Class<?> oclass : field_181158_a)
        {
            this.createLookup(oclass);
        }
    }

    protected void createLookup(Class<?> clazz)
    {
        field_181158_a.add(clazz);

        for (T t : this.field_181745_e)
        {
            if (clazz.isAssignableFrom(t.getClass()))
            {
                this.func_181743_a(t, clazz);
            }
        }

        this.knownKeys.add(clazz);
    }

    protected Class<?> func_181157_b(Class<?> p_181157_1_)
    {
        if (this.baseClass.isAssignableFrom(p_181157_1_))
        {
            if (!this.knownKeys.contains(p_181157_1_))
            {
                this.createLookup(p_181157_1_);
            }

            return p_181157_1_;
        }
        else
        {
            throw new IllegalArgumentException("Don\'t know how to search for " + p_181157_1_);
        }
    }

    public boolean add(T p_add_1_)
    {
        for (Class<?> oclass : this.knownKeys)
        {
            if (oclass.isAssignableFrom(p_add_1_.getClass()))
            {
                this.func_181743_a(p_add_1_, oclass);
            }
        }

        return true;
    }

    private void func_181743_a(T p_181743_1_, Class<?> p_181743_2_)
    {
        List<T> list = (List)this.map.get(p_181743_2_);

        if (list == null)
        {
            this.map.put(p_181743_2_, Lists.newArrayList(p_181743_1_));
        }
        else
        {
            list.add(p_181743_1_);
        }
    }

    public boolean remove(Object p_remove_1_)
    {
        T t = (T)p_remove_1_;
        boolean flag = false;

        for (Class<?> oclass : this.knownKeys)
        {
            if (oclass.isAssignableFrom(t.getClass()))
            {
                List<T> list = (List)this.map.get(oclass);

                if (list != null && list.remove(t))
                {
                    flag = true;
                }
            }
        }

        return flag;
    }

    public boolean contains(Object p_contains_1_)
    {
        return Iterators.contains(this.getByClass(p_contains_1_.getClass()).iterator(), p_contains_1_);
    }

    public <S> Iterable<S> getByClass(final Class<S> clazz)
    {
        return new Iterable<S>()
        {
            public Iterator<S> iterator()
            {
                List<T> list = (List)ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.func_181157_b(clazz));

                if (list == null)
                {
                    return Iterators.<S>emptyIterator();
                }
                else
                {
                    Iterator<T> iterator = list.iterator();
                    return Iterators.filter(iterator, clazz);
                }
            }
        };
    }

    public Iterator<T> iterator()
    {
        return this.field_181745_e.isEmpty() ? Iterators.<T>emptyIterator() : Iterators.unmodifiableIterator(this.field_181745_e.iterator());
    }

    public int size()
    {
        return this.field_181745_e.size();
    }
}
