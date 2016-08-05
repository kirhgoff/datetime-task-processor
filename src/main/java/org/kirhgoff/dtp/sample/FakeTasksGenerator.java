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
   * Generates simple callables with random start time in periodMillis
   * that will sleep for some time around periodMillis
   * @param count - amount of pairs to generate
   * @param periodMillis - constant to be used in randomization,
   * initial period of events
   * @return queue with generated pairs
   */
  Queue<Pair<LocalDateTime, Callable<String>>> newTasks(int count, long periodMillis) {
    Queue<Pair<LocalDateTime, Callable<String>>> tasks = new LinkedList<>();

    LocalDateTime now = LocalDateTime.now();
    for (int i = 0; i < count; i ++) {
      String string = UUID.randomUUID().toString();
      LocalDateTime scheduleTime = plus(now, periodMillis);
      long sleepTime = randomMillis(periodMillis);

      tasks.add(
        new ImmutablePair<>(scheduleTime,
          new SleepingCallable<>(sleepTime, string))
      );

      now = scheduleTime;
    }
    return tasks;
  }

  private LocalDateTime plus(LocalDateTime now, long periodMillis) {
    return now.plus(randomMillis(periodMillis), ChronoUnit.MILLIS);
  }

  private long randomMillis(long periodMillis) {
    return Math.round(periodMillis * (random.nextDouble() - half));
  }


}
