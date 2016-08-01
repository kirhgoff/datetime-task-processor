package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Feeder<T> {
  private final Processor<T> processor;
  private long delay;
  private Stack<Pair<LocalDateTime, Callable<T>>> tasks;

  public Feeder(Processor<T> processor, long delay) {
    this.processor = processor;
    this.delay = delay;
  }

  public void generateTasks(int count) {
    tasks = new Stack<>();
    //TODO generate tasks
  }

  public CountDownLatch start() {
    final CountDownLatch latch = new CountDownLatch(1);
    final Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (!tasks.isEmpty()) {
          processor.process(tasks.pop());
        } else {
          timer.cancel();
          latch.countDown();
        }
      }
    }, delay, delay);
    return latch;
  }

}
