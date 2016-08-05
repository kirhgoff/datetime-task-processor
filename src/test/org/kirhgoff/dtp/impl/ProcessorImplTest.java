package org.kirhgoff.dtp.impl;

import org.kirhgoff.dtp.api.Processor;
import org.kirhgoff.dtp.sample.SleepingCallable;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorImplTest {
  @Test
  public void testOneTask() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Processor<String> processor = new ProcessorImpl<>(1);
    processor.start();
    processor.add(LocalDateTime.now().plus(100, ChronoUnit.MILLIS),
        () -> {
          latch.countDown();
          return "OK";
        }
    );
    latch.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testTwoTasks() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
    Processor<String> processor = new ProcessorImpl<>(1);
    processor.start();

    LocalDateTime now = LocalDateTime.now();
    Callable<String> task = () -> {
      latch.countDown();
      return "OK";
    };

    processor.add(now.plus(100, ChronoUnit.MILLIS), task);
    processor.add(now.plus(100, ChronoUnit.MILLIS), task);
    latch.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testTwoTasksOrder() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
    Processor<Boolean> processor = new ProcessorImpl<>(1);
    processor.start();

    ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
    LocalDateTime now = LocalDateTime.now();

    processor.add(now.plus(400, ChronoUnit.MILLIS), () -> {
      latch.countDown();
      return queue.add(2);
    });

    processor.add(now.minus(200, ChronoUnit.MILLIS), () -> {
      latch.countDown();
      return queue.add(1);
    });

    latch.await(600, TimeUnit.MILLISECONDS);
    assertThat(queue.size()).isEqualTo(2);
    assertThat(queue.poll()).isEqualTo(1);
    assertThat(queue.poll()).isEqualTo(2);
  }


}
