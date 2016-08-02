package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedWrapper<T> implements Delayed {
  private static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;
  private final LocalDateTime dateTime;
  private final Callable<T> callable;

  public DelayedWrapper(Pair<LocalDateTime, Callable<T>> pair) {
    this(pair.getLeft(), pair.getRight());
  }

  public DelayedWrapper(LocalDateTime dateTime, Callable<T> callable) {
    this.dateTime = dateTime;
    this.callable = callable;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    long millisLeft = ChronoUnit.MILLIS.between(dateTime, LocalDateTime.now());
    return unit.convert(millisLeft, MILLIS);
  }

  @Override
  public int compareTo(Delayed that) {
    long thisDelay = this.getDelay(MILLIS);
    long thatDelay = that.getDelay(MILLIS);
    //We dont care if it is equal
    return thisDelay > thatDelay ? -1 : 1;
  }

  public Callable<T> getCallable() {
    return callable;
  }

  @Override
  public String toString() {
    return dateTime + ": " + callable;
  }
}
