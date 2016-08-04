package org.kirhgoff.dtp.sample;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

class FakeTasksGenerator {
  private static final double half = 0.5;
  private final Random random = new Random();

  FakeTasksGenerator() {}

  /**
   * Generates simple callables which wait for random time and need to start in some period of time from current moment
   * @param count - amount of pairs to generate
   * @param periodMillis - constant to be used in randomization,
   * initial period of events
   * @return queue with generated pairs
   */
  Queue<Pair<LocalDateTime, Callable<String>>> newTasks(int count, long periodMillis) {
    Queue<Pair<LocalDateTime, Callable<String>>> tasks = new LinkedList<>();

    LocalDateTime now = LocalDateTime.now();
    for (int i = 0; i < count; i ++) {
      long timeShift = Math.round(periodMillis * (random.nextDouble() - half));
      LocalDateTime scheduleTime = now.plus(timeShift, ChronoUnit.MILLIS);

      long sleepTime = Math.round(periodMillis * (random.nextDouble() - half));
      String string = UUID.randomUUID().toString();
      Callable<String> callable = new SleepingCallable<>(sleepTime, string);

      tasks.add(new ImmutablePair<>(scheduleTime, callable));
      now = scheduleTime;
    }
    return tasks;
  }


}
