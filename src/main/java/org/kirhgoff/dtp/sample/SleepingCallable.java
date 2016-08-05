package org.kirhgoff.dtp.sample;

import java.util.concurrent.Callable;

public class SleepingCallable<T> implements Callable<T>{
  private final long delay;
  private final T string;

  public SleepingCallable(long delay, T string) {
    this.delay = delay;
    this.string = string;
  }

  @Override
  public T call() throws Exception {
    System.out.println("Called: " + toString());
    long start = System.currentTimeMillis();
    Thread.sleep(delay);
    System.out.println(
      "Finished, overslept for "
        + (System.currentTimeMillis() - start - delay)
        + "ms " + toString()
    );
    return string;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[ delay=" + delay + ", string=" + string + "]";
  }
}
