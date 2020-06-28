package ru.otus.homework;

import com.sun.management.GarbageCollectionNotificationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GcStatistic {
    private static final Logger LOG = LoggerFactory.getLogger(GcStatistic.class);
    private final Map<String, Map<Long, Long>> map;

    public GcStatistic(Map<String, Map<Long, Long>> map) {
        this.map = map;
    }

    public void switchOnMonitoring() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        for (GarbageCollectorMXBean gcbean : gcBeans) {
            LOG.info("GC name: {}", gcbean.getName());
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = (Notification notification, Object handback) -> {
                if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                    handleNotification(notification,
                            memoryMXBean.getHeapMemoryUsage().getUsed());
                }
            };
            emitter.addNotificationListener(listener, null, null);
        }
    }

    private void handleNotification(Notification notification, long memoryUsed) {
        GarbageCollectionNotificationInfo info =
                GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
        String gcName = info.getGcName();
        long duration = info.getGcInfo().getDuration();

        String heapMemory = String.format("%.2f Mb", (double)memoryUsed / 1_048_576);
        LOG.info("Name: {}, action: {}, cause: {} ({} ms). Used heap memory {}",
                info.getGcName(), info.getGcAction(), info.getGcCause(), duration,
                heapMemory);

        if (map.containsKey(gcName)) {
            Map<Long, Long> actionStatistic = map.get(gcName);
            long eventCount = actionStatistic.entrySet().iterator().next().getKey();
            long sumDuration = actionStatistic.get(eventCount) + duration;
            map.put(gcName, Collections.singletonMap(eventCount + 1, sumDuration));
        } else {
            map.put(gcName, Collections.singletonMap(1L, duration));
        }
    }
}
