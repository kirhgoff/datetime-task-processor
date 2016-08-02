package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Feeder {
  private final Processor<String> processor;
  private long delay;
  private Stack<Pair<LocalDateTime, Callable<String>>> tasks;

  public Feeder(Processor<String> processor, long delay) {
    this.processor = processor;
    this.delay = delay;
  }

  public void generateTasks(int count) {
    //TODO extract as generator
    Random random = new Random();
    tasks = new Stack<>();
    LocalDateTime now = LocalDateTime.now();
    for (int i = 0; i < count; i ++) {
      String string = UUID.randomUUID().toString();
      long taskDelay = Math.round(delay* (0.5 - random.nextDouble()));
      long timeShift = Math.round(delay* (0.5 - random.nextDouble()));
      LocalDateTime scheduleTime = now.plus(timeShift, ChronoUnit.MILLIS);
      tasks.add(new ImmutablePair<>(scheduleTime,
          new DelayedStringTask(taskDelay, string))
      );

      now = scheduleTime;

    }
  }

  public void run() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        //TODO do we need checks for interrupted here?
        if (!tasks.isEmpty()) {
          processor.process(tasks.pop());
        } else {
          timer.cancel();
          latch.countDown();
        }
      }
    }, delay, delay);
    latch.await();
  }

}
