package org.kirhgoff.dtp.sample;

import org.apache.commons.lang3.tuple.Pair;
import org.kirhgoff.dtp.api.Processor;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Uses generated tasks
 */
class Feeder {
  private long delay;

  Feeder(long delay) {
    this.delay = delay;
  }

  void feed(Processor<String> processor,
            Queue<Pair<LocalDateTime, Callable<String>>> tasks)
      throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);
    final Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        //TODO do we need checks for interrupted here?
        if (!tasks.isEmpty()) {
          Pair<LocalDateTime, Callable<String>> nextOne = tasks.poll();

          LocalDateTime time = nextOne.getLeft();
          Callable<String> callable = nextOne.getRight();
          processor.add(time, callable);
        } else {
          timer.cancel();
          latch.countDown();
        }
      }
    }, delay, delay);
    latch.await();
  }

}
