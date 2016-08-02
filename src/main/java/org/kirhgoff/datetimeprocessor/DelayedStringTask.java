package org.kirhgoff.datetimeprocessor;

import java.util.concurrent.Callable;

public class DelayedStringTask implements Callable<String>{
  private final long delay;
  private final String string;

  public DelayedStringTask(long delay, String string) {
    this.delay = delay;
    this.string = string;
  }

  @Override
  public String call() throws Exception {
    Thread.sleep(delay);
    return string;
  }
}
