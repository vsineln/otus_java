package ru.otus.homework.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private Map<K, V> cache = new WeakHashMap<>();
    private List<WeakReference<HwListener>> listeners = new ArrayList<>();
    private ReferenceQueue<HwListener> refQueue = new ReferenceQueue<>();

    @Override
    public void put(K key, V value) {
        notifyListener(key, value, "add");
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        notifyListener(key, cache.get(key), "remove");
        cache.remove(key);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener listener) {
        WeakReference weakReference = new WeakReference(listener, refQueue);
        listeners.add(weakReference);
    }

    @Override
    public void removeListener(HwListener listener) {
        Optional<WeakReference<HwListener>> reference = listeners.stream().filter(ref -> listener.equals(ref.get())).findAny();
        reference.ifPresent(hwListenerWeakReference -> listeners.remove(hwListenerWeakReference));
    }

    private void notifyListener(K key, V value, String action) {
        checkListeners();
        listeners.forEach(listener -> {
            if (listener != null) {
                listener.get().notify(key, value, action);
            }
        });
    }

    private void checkListeners() {
        Reference<? extends HwListener> reference = refQueue.poll();
        if (reference != null) {
            listeners.remove(reference);
        }
    }
}
