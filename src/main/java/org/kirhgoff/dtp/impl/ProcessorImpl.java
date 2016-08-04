package org.kirhgoff.dtp.impl;

import org.kirhgoff.dtp.api.Processor;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public class ProcessorImpl<T> implements Processor<T> {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;

  private final int poolSize;
  private final ScheduledExecutorService executor;
  private final DelayQueue<DelayedCallable<T>> jobs = new DelayQueue<>();
  private final Semaphore semaphore;

  public ProcessorImpl(int poolSize) {
    this.poolSize = poolSize;
    this.executor = Executors.newScheduledThreadPool(poolSize);
    this.semaphore = new Semaphore(poolSize);
  }
  /**
   * Not thread safe
   */
  public void start() {
    while (!Thread.currentThread().isInterrupted()) {
      DelayedCallable<T> job = jobs.poll();

      try {
        semaphore.acquire();
        executor.schedule(
            new SemaphoreAwareCallable<>(job, semaphore),
            job.getDelay(MILLIS), MILLIS
        );
      } catch (InterruptedException e) {
        //TODO check its ok to just return, looks good
        System.out.println("Processor was interrupted.");
        return;
      }
    }
  }

  @Override
  public void add(LocalDateTime time, Callable<T> task) {
    jobs.add(new DelayedCallable<>(time, task));
    System.out.println("Added: time=" + time + ", task=" + task);
  }

  @Override
  public boolean isBusy() throws InterruptedException {
    //TODO is it be safe?
    //TODO get executor pool size
    //TODO will it return true if some future call is in?
    return !jobs.isEmpty() || semaphore.availablePermits() < poolSize;
  }

  public void stop() {
    executor.shutdownNow();
    //TODO wait till it ends
    //executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
  }

  private static class SemaphoreAwareCallable<R> implements Callable<R> {
    private final DelayedCallable<R> callable;
    private final Semaphore semaphore;

    SemaphoreAwareCallable (DelayedCallable<R> callable, Semaphore semaphore) {
      this.callable = callable;
      this.semaphore = semaphore;
    }

    @Override
    public R call() throws Exception {
      R result = callable.call();
      //TODO possible unsafeness
      semaphore.release();
      return result;
    }
  }
}
