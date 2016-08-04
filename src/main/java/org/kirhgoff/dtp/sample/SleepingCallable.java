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
    System.out.println(string + ": going to sleep for " + delay + " millis");
    long start = System.currentTimeMillis();
    Thread.sleep(delay);
    System.out.println(
      string + ": finished, overslept for "
        + (System.currentTimeMillis() - start - delay)
        + "millis"
    );
    return string;
  }
}
