package ru.otus.homework;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadDemo {
    private final Object monitor = new Object();
    private AtomicBoolean sharedFlag = new AtomicBoolean(true);
    private Printer printer1 = new Printer("print1", true);
    private Printer printer2 = new Printer("print2", false);

    public static void main(String[] args) {
        new ThreadDemo().start();
    }

    private void start() {
        new Thread(printer1).start();
        new Thread(printer2).start();
    }

    class Printer implements Runnable {
        private String name;
        private boolean flag;

        Printer(String name, boolean flag) {
            this.name = name;
            this.flag = flag;
        }

        @Override
        public void run() {
            for (int i = 1, j = 0; ; i += j) {
                synchronized (monitor) {
                    System.out.println(name + ":" + i);
                    monitor.notify();
                    while (sharedFlag.get() != flag) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                sharedFlag.getAndSet(!flag);
                if (i == 1) j = 1;
                if (i == 10) j = -1;
            }
        }
    }
}
