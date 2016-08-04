package org.kirhgoff.dtp.api;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * DTP - date time processor
 * @param <T> - result type for provided Callables
 */
public interface Processor<T> {
  /**
   * Starts work of processor
   */
  void start();

  /**
   * Momentarily adds task to be executed in required
   * moment of time. Executed as soon as possible, if
   * resources are short and requested time is already
   * in the past
   * @param task - callable to execute, returns object
   * of type T
   */
  void add(LocalDateTime time, Callable<T> task);

  /**
   * Checks the amount of queue and remaining working processes
   * @return true, if busy
   * @throws InterruptedException
   */
  boolean isBusy() throws InterruptedException;

  /**
   * Interrupts running processes and tries to shutdown
   * resource pool
   */
  void stop();
}
