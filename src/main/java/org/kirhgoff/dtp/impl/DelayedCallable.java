package org.kirhgoff.dtp.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Delayed implementation
 * @param <T> - return value of Callable
 */
class DelayedCallable<T> implements Delayed, Callable<T> {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
  private final LocalDateTime dateTime;
  private final Callable<T> delegate;
  private final NowProvider nowProvider;

  public DelayedCallable(LocalDateTime dateTime, Callable<T> delegate, NowProvider nowProvider) {
    this.dateTime = dateTime;
    this.delegate = delegate;
    this.nowProvider = nowProvider;
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
    long millisLeft = ChronoUnit.MILLIS.between(nowProvider.get(), dateTime);
    //TODO give zero if overdue?
    return unit.convert(millisLeft, MILLIS);
  }

  @Override
  public int compareTo(Delayed that) {
    long thisDelay = this.getDelay(MILLIS);
    long thatDelay = that.getDelay(MILLIS);
    if (thisDelay == thatDelay) return 0;
    else return thisDelay < thatDelay ? -1 : 1;
  }

  @Override
  public T call() throws Exception {
    return delegate.call();
  }

  @Override
  public String toString() {
    return "DelayedCallable[delay: " + getDelay(MILLIS) + ", callable=" + delegate + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DelayedCallable)) return false;

    DelayedCallable<?> that = (DelayedCallable<?>) o;

    if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null)
      return false;
    if (delegate != null ? !delegate.equals(that.delegate) : that.delegate != null)
      return false;
    return nowProvider  == that.nowProvider;

  }

  @Override
  public int hashCode() {
    int result = dateTime != null ? dateTime.hashCode() : 0;
    result = 31 * result + (delegate != null ? delegate.hashCode() : 0);
    result = 31 * result + (nowProvider != null ? nowProvider.hashCode() : 0);
    return result;
  }
}
