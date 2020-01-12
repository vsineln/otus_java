package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.cache.HwCache;
import ru.otus.homework.cache.HwListener;
import ru.otus.homework.cache.MyCache;

/**
 * @author sergey
 * created on 14.12.18.
 */
public class HWCacheDemo {
    private Logger logger = LoggerFactory.getLogger(HWCacheDemo.class);

    public static void main(String[] args) throws InterruptedException {
        new HWCacheDemo().demo();
    }

    private void demo() throws InterruptedException {
        HwCache<Integer, Integer> cache = new MyCache<>();
        HwListener<Integer, Integer> listener =
                (key, value, action) -> logger.info("key:{}, value:{}, action: {}", key, value, action);
        cache.addListener(listener);
        cache.put(1, 1);

        logger.info("getValue:{}", cache.get(1));
        cache.remove(1);
        cache.removeListener(listener);

        /*listener = null;
        System.gc();
        Thread.sleep(100);
        cache.put(1, 1);
        cache.remove(1);*/
    }
}
