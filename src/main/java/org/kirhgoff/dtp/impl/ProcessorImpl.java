package org.kirhgoff.dtp.impl;

import org.kirhgoff.dtp.api.Processor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;

public class ProcessorImpl<T> implements Processor<T> {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
  private static final NowProvider NOW = NowProvider.system();

  private final ScheduledExecutorService executor;
  private final DelayQueue<DelayedCallable<T>> jobs = new DelayQueue<>();
  private final Semaphore semaphore;
  private Thread pollingThread;

  private final int poolSize;
  private long precision;

  public ProcessorImpl(int poolSize, long precision) {
    this.poolSize = poolSize;
    this.executor = Executors.newScheduledThreadPool(poolSize);
    this.semaphore = new Semaphore(poolSize);
    this.precision = precision;
  }
  /**
   * Not thread safe
   */
  public void start() {
    pollingThread = new Thread(this::run);
    pollingThread.start();
  }

  private void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        System.out.println("Processor: looking for job");
        DelayedCallable<T> job = jobs.take();
        long delay = job.getDelay(MILLIS);
        System.out.println("Processor: scheduled job " + job);
        executor.schedule(
            new SemaphoreAwareCallable<>(job, semaphore),
            delay, MILLIS
        );
      }
    } catch (InterruptedException e) {
      System.out.println("Processor: was interrupted.");
    }
  }

  @Override
  public void add(LocalDateTime time, Callable<T> task) {
    jobs.add(new DelayedCallable<>(time, task, NOW));
    long timeMilli = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    System.out.println("Processor: added job, to start=" + (timeMilli - System.currentTimeMillis())
        + ", task=" + task);
  }

  @Override
  public boolean isBusy() throws InterruptedException {
    //TODO is it be safe?
    return !jobs.isEmpty() && semaphore.availablePermits() < poolSize;
  }

  public void stop() {
    executor.shutdownNow();
    pollingThread.interrupt();
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
      System.out.println("Processor: starting a job, acquiring monitor for "
          + callable);
      semaphore.acquire();
      R result = callable.call();
      //TODO possible unsafeness
      semaphore.release();
      System.out.println("Processor: releasing semaphore: " + semaphore.availablePermits());
      return result;
    }
  }
}
