package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public class ProcessorImpl<T> implements Processor<T> {
  private final ThreadPoolExecutor executor
    = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

  private ConcurrentSkipListSet<Pair<LocalDateTime, Callable<T>>> jobs
    = new ConcurrentSkipListSet<>(new PairsComparator<Callable<T>>());

  public void start() {

  }

  @Override
  public void process(Pair<LocalDateTime, Callable<T>> task) {
    jobs.add(task);
    System.out.println("Added task: " + task);
  }

  @Override
  public boolean isBusy() {
    return !jobs.isEmpty() || executor.getActiveCount() != 0;
  }

  public void stop() {
    executor.shutdownNow();
  }
}
