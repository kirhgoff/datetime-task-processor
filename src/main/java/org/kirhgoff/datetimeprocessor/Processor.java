package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public interface Processor<T> {
  void start(int poolSize);
  void process(Pair<LocalDateTime, Callable<T>> task);
  boolean isBusy() throws InterruptedException;
  void stop();
}
