package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.concurrent.*;

import static org.kirhgoff.datetimeprocessor.DelayedWrapper.MILLIS;

public class ProcessorImpl<T> implements Processor<T> {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;

  private ScheduledExecutorService executor;
  private DelayQueue<DelayedWrapper<T>> jobs = new DelayQueue<>();
  private Semaphore semaphore;
  /**
   * Not thread safe
   */
  public void start(int poolSize) {
    executor = Executors.newScheduledThreadPool(poolSize);
    semaphore = new Semaphore(poolSize);
    while (!Thread.currentThread().isInterrupted()) {
      DelayedWrapper<T> job = jobs.poll();
      Callable<T> realCallable = () -> {
        T result = job.getCallable().call();
        semaphore.release();
        return result;
      };

      try {
        semaphore.acquire();
        executor.schedule(
          realCallable, job.getDelay(MILLIS), MILLIS
        );
      } catch (InterruptedException e) {
        System.out.println("Processor was interrupted.");
        return;
      }
    }
  }

  @Override
  public void process(Pair<LocalDateTime, Callable<T>> task) {
    jobs.add(new DelayedWrapper<>(task));
    System.out.println("Added task: " + task);
  }

  @Override
  public boolean isBusy() throws InterruptedException {
    //TODO how to check semaphore is busy
    //TODO will it return true if some future call is in?
    return !jobs.isEmpty();
  }

  public void stop() {
    executor.shutdownNow();
    //TODO wait till it ends
    //executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
  }
}
