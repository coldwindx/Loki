package com.coldwindx.loki.support;

import com.coldwindx.loki.config.ScheduleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
public abstract class AbstractScheduleTaskProcess<T> implements IScheduleTaskProcess {

    private final ScheduleConfig config;
    private final ExecutorService executor;

    public AbstractScheduleTaskProcess(ScheduleConfig config, ExecutorService executors) {
        this.config = config;
        ThreadFactory tf = (r)->new Thread(r, "custom-thread");
        BlockingDeque<Runnable> deque = new LinkedBlockingDeque<>();
        executor = new ThreadPoolExecutor(
                config.getExecuteCount(), 10, 60, TimeUnit.SECONDS, deque, tf, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void destroy() {
        try {
            executor.shutdown();
            executor.awaitTermination(1L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("AbstractScheduleTaskProcess destroy error！", e);
        }
    }
    @Override
    public int execute() {
        List<T> tasks = this.query();
        if(CollectionUtils.isEmpty(tasks))
            return 0;
        List<List<T>> batches = batch(tasks, config.getExecuteCount());
        final CountDownLatch latch = new CountDownLatch(tasks.size());
        for (List<T> batch : batches) {
            executor.submit(() -> {
                try {
                    this.execute(batch);
                }finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("interrupted when processing data access request in concurrency");
        }
        return tasks.size();
    }

    protected abstract List<T> query();
    protected abstract void execute(List<T> batch);

    private static <T> List<List<T>> batch(List<T> tasks, int batchSize) {
        if (batchSize <= 0)
            throw new RuntimeException("batchSize must be greater than 0");

        int batchCount = (tasks.size() + batchSize - 1) / batchSize;
        List<List<T>> result = new java.util.ArrayList<>(batchCount);
        for (int i = 0; i < batchCount; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, tasks.size());
            result.add(tasks.subList(start, end));
        }
        return result;
    }
}
