package org.kirhgoff.dtp.impl;

import org.kirhgoff.dtp.api.Processor;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorImplTest {
  private Processor<Boolean> processor;

  @BeforeTest
  void setUp() {
    processor = new ProcessorImpl<>(1);
    processor.start();
  }

  @AfterTest
  void tearDown() {
    processor.stop();
  }

  @Test
  public void testOneTask() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    processor.add(LocalDateTime.now().plus(100, ChronoUnit.MILLIS),
        () -> {
          latch.countDown();
          return true;
        }
    );
    latch.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testTwoTasks() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
    LocalDateTime now = LocalDateTime.now();
    Callable<Boolean> task = () -> {
      latch.countDown();
      return true;
    };

    processor.add(now.plus(100, ChronoUnit.MILLIS), task);
    processor.add(now.plus(100, ChronoUnit.MILLIS), task);
    latch.await(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testTwoTasksOrder() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
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
