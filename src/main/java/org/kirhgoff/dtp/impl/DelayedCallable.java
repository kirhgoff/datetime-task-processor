package org.kirhgoff.dtp.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

//TODO write test

/**
 * Delayed implementation
 * @param <T> - return value of Callable
 */
class DelayedCallable<T> implements Delayed, Callable<T> {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
  private final LocalDateTime dateTime;
  private final Callable<T> delegate;

  public DelayedCallable(LocalDateTime dateTime, Callable<T> delegate) {
    this.dateTime = dateTime;
    this.delegate = delegate;
  }

  /**
   * Delay till Callable start from current moment
   * @param unit - type of time unit
   * @return millis till expected start,
   * could return value below zero
   * if happened in the past
   */
  @Override
  public long getDelay(TimeUnit unit) {
    long millisLeft = ChronoUnit.MILLIS.between(LocalDateTime.now(), dateTime);
    long convert = unit.convert(millisLeft, MILLIS);
    //System.out.println("Job: millis left=" + millisLeft + ", converted=" + convert + ", unit=" + unit);
    return convert;
  }

  @Override
  public int compareTo(Delayed that) {
    long thisDelay = this.getDelay(MILLIS);
    long thatDelay = that.getDelay(MILLIS);
    //TODO how to keep initial order of addition?
    return thisDelay > thatDelay ? -1 : 1;
  }

  @Override
  public T call() throws Exception {
    return delegate.call();
  }

  @Override
  public String toString() {
    return "Job: to run in: " + getDelay(MILLIS) + ", " + delegate;
  }
}
