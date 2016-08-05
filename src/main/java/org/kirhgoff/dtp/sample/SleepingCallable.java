package org.kirhgoff.dtp.sample;

import java.util.concurrent.Callable;

public class SleepingCallable<T> implements Callable<T>{
  private final long sleepTime;
  private final T string;

  public SleepingCallable(long sleepTime, T string) {
    this.sleepTime = sleepTime;
    this.string = string;
  }

  @Override
  public T call() throws Exception {
    System.out.println(">>>> Called: " + toString());
    long start = System.currentTimeMillis();
    Thread.sleep(sleepTime > 0 ? sleepTime : 0);
    System.out.println(
      ">>>> Finished, overslept for "
        + (System.currentTimeMillis() - start - sleepTime)
        + "ms " + toString()
    );
    return string;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[ sleepTime=" + sleepTime + ", string=" + string + "]";
  }
}
