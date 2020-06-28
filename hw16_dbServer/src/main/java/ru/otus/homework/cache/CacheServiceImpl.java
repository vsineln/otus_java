package ru.otus.homework.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;

@Service
public class CacheServiceImpl<K, V> implements CacheService<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(CacheServiceImpl.class);
    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<WeakReference<CacheListener<K, V>>> listeners = new ArrayList<>();
    private final ReferenceQueue<CacheListener<K, V>> refQueue = new ReferenceQueue<>();

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
    public void addListener(CacheListener<K, V> listener) {
        WeakReference<CacheListener<K, V>> weakReference = new WeakReference<>(listener, refQueue);
        listeners.add(weakReference);
    }

    @Override
    public void removeListener(CacheListener<K, V> listener) {
        Optional<WeakReference<CacheListener<K, V>>> reference = listeners.stream().
                filter(ref -> listener.equals(ref.get())).findAny();
        reference.ifPresent(listeners::remove);
    }

    private void notifyListener(K key, V value, String action) {
        checkListeners();
        listeners.forEach((WeakReference<CacheListener<K, V>> listener) -> {
            if (listener != null) {
                try {
                    Objects.requireNonNull(listener.get()).notify(key, value, action);
                } catch (Exception e) {
                   LOG.error("Error in listener: {}", e.getMessage());
                }
            }
        });
    }

    private void checkListeners() {
        Reference<? extends CacheListener<K, V>> reference = refQueue.poll();
        if (reference != null) {
            listeners.remove(reference);
        }
    }
}
