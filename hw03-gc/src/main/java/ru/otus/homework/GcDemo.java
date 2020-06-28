package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -Xms512m
 * -Xmx512m
 * -Xlog:gc=debug:file=./logs/gc.log:tags,uptime,time,level:filecount=10,filesize=10m
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=./logs/dump
 * -XX:+UseG1GC
 * <p>
 * -XX:+UseSerialGC
 * -XX:+UseParallelGC
 * -XX:+UseConcMarkSweepGC
 * -XX:+UseG1GC
 */
public class GcDemo {
    private static final Logger LOG = LoggerFactory.getLogger(GcDemo.class);

    public static void main(String[] args) {
        new GcDemo().startDemo();
    }

    private void startDemo() {
        Map<String, Map<Long, Long>> map = new HashMap<>();
        GcStatistic gcStatistic = new GcStatistic(map);
        gcStatistic.switchOnMonitoring();

        long beginTime = System.currentTimeMillis();
        List<String> list = readFile();

        MemoryLeakApp appDemo = new MemoryLeakApp(list);
        try {
            appDemo.run();
        } catch (OutOfMemoryError e) {
            LOG.info("time: {}", (System.currentTimeMillis() - beginTime) / 1000);
            LOG.info("GC statistic: {}", map);
            LOG.info("Out of memory error", e);
        }
    }

    private List<String> readFile() {
        List<String> list = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream in = classLoader.getResourceAsStream("example.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] ar = line.split(" ");
                    list.addAll(Arrays.asList(ar));
                }
            }
        } catch (IOException e) {
            LOG.error("Can not read file ", e);
        }
        return list;
    }
}
