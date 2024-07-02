package by.zhorau.rediscollections;

import redis.clients.jedis.commands.JedisCommands;

import java.util.*;

import static redis.clients.jedis.args.ListPosition.AFTER;
import static redis.clients.jedis.args.ListPosition.BEFORE;

public class RedisList implements List<String> {

    private final JedisCommands jedis;
    private final String namespace;

    public RedisList(JedisCommands jedis, String namespace) {
        this.jedis = jedis;
        this.namespace = namespace;
    }

    @Override
    public int size() {
        return (int) jedis.llen(namespace);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(cursor++);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return subList(0, size()).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<T> list = (List<T>) subList(0, size());
        return (T[]) list.toArray();
    }

    @Override
    public boolean add(String s) {
        jedis.rpush(namespace, s);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        long count = jedis.lrem(namespace, 1, o.toString());
        return count > 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        for (String s : c) {
            add(s);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        int i = index;
        for (String s : c) {
            add(i++, s);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            modified |= remove(o);
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : this) {
            if (!c.contains(o))
                modified |= remove(o);
        }
        return modified;
    }

    @Override
    public void clear() {
        jedis.del(namespace);
    }

    @Override
    public String get(int index) {
        return jedis.lindex(namespace, index);
    }

    @Override
    public String set(int index, String element) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        String oldValue = get(index);
        jedis.lset(namespace, index, element);
        return oldValue;
    }

    @Override
    public void add(int index, String element) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        jedis.linsert(namespace, index == 0 ? BEFORE : AFTER, jedis.lindex(namespace, index - 1), element);
    }

    @Override
    public String remove(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        String element = get(index);
        jedis.lrem(namespace, 1, element);
        return element;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(o, get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size() - 1; i >= 0; i--) {
            if (Objects.equals(o, get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<String> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return new ListIterator<>() {
            private int cursor = index;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public String previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return get(--cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                RedisList.this.remove(cursor);
            }

            @Override
            public void set(String e) {
                RedisList.this.set(cursor, e);
            }

            @Override
            public void add(String e) {
                RedisList.this.add(cursor++, e);
            }
        };
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        return jedis.lrange(namespace, fromIndex, toIndex - 1);
    }

    @Override
    public String toString() {
        Iterator<String> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            String e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }
}
