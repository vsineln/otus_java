package ru.otus.homework.messagesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms.Message;
import ru.otus.homework.ms.MsClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class MessageSystemImpl implements MessageSystem {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSystemImpl.class);
    private static final int MESSAGE_QUEUE_SIZE = 100_000;
    private static final int MSG_HANDLER_THREAD_LIMIT = 100;

    private final AtomicBoolean runFlag = new AtomicBoolean(true);

    private final Deque<MsClient> dbClientList = new ArrayDeque<>();
    private final Map<String, MsClient> feClientMap = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);

    private Runnable disposeCallback;

    private final ExecutorService msgProcessor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("msg-processor-thread");
        return thread;
    });

    private final ExecutorService msgHandler =
            Executors.newFixedThreadPool(MSG_HANDLER_THREAD_LIMIT, new ThreadFactory() {
                private final AtomicInteger threadNameSeq = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("msg-handler-thread-" + threadNameSeq.incrementAndGet());
                    return thread;
                }
            });

    public MessageSystemImpl() {
        start();
    }

    public MessageSystemImpl(boolean startProcessing) {
        if (startProcessing) {
            start();
        }
    }

    @Override
    public void start() {
        msgProcessor.submit(this::msgProcessor);
    }

    @Override
    public int currentQueueSize() {
        return messageQueue.size();
    }

    @Override
    public void addDbClient(MsClient msClient) {
        LOG.info("new database client: {}", msClient.getName());
        dbClientList.add(msClient);
    }

    @Override
    public void addFeClient(MsClient msClient) {
        LOG.info("new frontend client: {}", msClient.getName());
        if (feClientMap.containsKey(msClient.getName())) {
            throw new IllegalArgumentException("Error, client: " + msClient.getName() + " already exists");
        }
        feClientMap.put(msClient.getName(), msClient);
    }

    @Override
    public void removeClient(String clientId) {
        Optional<MsClient> dbClient = dbClientList.stream().
                filter(msClient -> clientId.equals(msClient.getName())).findAny();
        dbClient.ifPresent(dbClientList::remove);
        if (feClientMap.remove(clientId) != null || dbClient.isPresent()) {
            LOG.info("client {} removed", clientId);
        } else {
            LOG.warn("client not found: {}", clientId);
        }
    }

    @Override
    public boolean newMessage(Message msg) {
        LOG.info("new message: {}", msg.getId());
        if (runFlag.get()) {
            return messageQueue.offer(msg);
        } else {
            LOG.warn("MS is being shutting down... rejected:{}", msg);
            return false;
        }
    }

    @Override
    public void dispose() throws InterruptedException {
        LOG.info("now in the messageQueue {} messages", currentQueueSize());
        runFlag.set(false);
        insertStopMessage();
        msgProcessor.shutdown();
        msgHandler.awaitTermination(60, TimeUnit.SECONDS);
    }

    @Override
    public void dispose(Runnable callback) throws InterruptedException {
        disposeCallback = callback;
        dispose();
    }

    private void insertStopMessage() throws InterruptedException {
        boolean result = messageQueue.offer(Message.VOID_MESSAGE);
        while (!result) {
            Thread.sleep(100);
            result = messageQueue.offer(Message.VOID_MESSAGE);
        }
    }

    private void messageHandlerShutdown() {
        msgHandler.shutdown();
        LOG.info("msgHandler has been shut down");
    }

    private void msgProcessor() {
        LOG.info("msgProcessor started, {}", currentQueueSize());
        while (runFlag.get() || !messageQueue.isEmpty()) {
            try {
                Message msg = messageQueue.take();
                LOG.info("MSG_PROCESSOR");
                if (msg == Message.VOID_MESSAGE) {
                    LOG.info("received the stop message");
                } else {
                    handleMessage(msg);
                }
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        if (disposeCallback != null) {
            msgHandler.submit(disposeCallback);
        }
        msgHandler.submit(this::messageHandlerShutdown);
        LOG.info("msgProcessor finished");
    }

    private void handleMessage(Message msg) {
        MsClient clientTo = gerClientTo(msg.getTo());
        if (clientTo != null) {
            msgHandler.submit(() -> clientTo.sendMessage(msg));
        } else {
            LOG.warn("client not found");
        }
    }

    private MsClient gerClientTo(String msgClientTo) {
        MsClient clientTo;
        if (msgClientTo == null && !dbClientList.isEmpty()) {
            clientTo = dbClientList.removeFirst();
            dbClientList.addLast(clientTo);
        } else {
            clientTo = feClientMap.get(msgClientTo);
        }
        return clientTo;
    }
}
