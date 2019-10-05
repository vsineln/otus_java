package ru.otus.homework;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * {@code List} interface implementation based on array
 *
 * @param <T> element's type which will be hold in array
 */
public class DIYarrayList<T> implements List<T> {
    private int INIT_CAPACITY = 10;
    private int size;
    private Object[] data;

    public DIYarrayList() {
        data = new Object[INIT_CAPACITY];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        if (isEmpty()) {
            return new Object[]{};
        }
        Object[] copy = new Object[size];
        System.arraycopy(data, 0, copy, 0, size);
        return copy;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) copyArray(a.length);
        }
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        if (size == data.length) {
            increaseArray(size + 1);
        }
        data[size] = t;
        size += 1;
        return true;
    }

    private void increaseArray(int size) {
        if (size < 0) {
            throw new OutOfMemoryError();
        }
        int newCapacity = size + (size >> 1);
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        }
        data = copyArray(newCapacity);
    }

    private Object[] copyArray(int newSize) {
        Object[] copy = new Object[newSize];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    @Override
    public T set(int index, T element) {
        checkIndex(index);
        Object previous = data[index];
        data[index] = element;
        return (T) previous;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DIYIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private class DIYIterator implements ListIterator<T> {
        private int index;
        private int lastIndex;

        public DIYIterator(int index) {
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index != size;
        }

        @Override
        public T next() {
            lastIndex = index;
            if (lastIndex >= size) {
                throw new NoSuchElementException();
            }
            index += 1;
            return (T) data[lastIndex];
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException();
        }

        @Override
        public T previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T o) {
            if (lastIndex < 0) {
                throw new IllegalStateException();
            }
            DIYarrayList.this.set(lastIndex, o);
        }

        @Override
        public void add(T o) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        List<Object> list = Arrays.asList(data);
        return list.stream()
                .filter(n -> n != null)
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(",", "[", "]"));
    }
}
