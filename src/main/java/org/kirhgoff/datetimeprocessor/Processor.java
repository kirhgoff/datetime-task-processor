package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public interface Processor<T> {
  void start();
  void process(Pair<LocalDateTime, Callable<T>> task);
  boolean isBusy();
  void stop();
}
