package org.kirhgoff.dtp.impl;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DelayedCallableTest {
  private static final Callable<String> OK = () -> "OK";
  public static final ChronoUnit MILLIS = ChronoUnit.MILLIS;

  @Test
  public void testDelay() {
    LocalDateTime now = LocalDateTime.now();

    checkDelay(now, 100, now.plus(100, MILLIS));
    checkDelay(now, -50, now.minus(50, MILLIS));
    checkDelay(now, -1000, now.minus(1, ChronoUnit.SECONDS));
  }

  @Test
  public void testOrder() {
    LocalDateTime now = LocalDateTime.now();
    Static provider = new Static(now);

    DelayedCallable<String> young = new DelayedCallable<>(
        now.minus(100, MILLIS), OK, provider);

    DelayedCallable<String> twin = new DelayedCallable<>(
        now.minus(100, MILLIS), OK, provider);

    DelayedCallable<String> old = new DelayedCallable<>(
        now.plus(100, MILLIS), OK, provider);

    assertThat(young.compareTo(old)).isEqualTo(-1);
    assertThat(old.compareTo(twin)).isEqualTo(1);
    assertThat(young.compareTo(twin)).isEqualTo(0);
  }

    private void checkDelay(LocalDateTime now, int expectedDelay, LocalDateTime start) {
    Static provider = new Static(now);
    DelayedCallable<String> delayed = new DelayedCallable<>(start, OK, provider);
    assertThat(delayed.getDelay(TimeUnit.MILLISECONDS)).isEqualTo(expectedDelay);
  }


  private static class Static implements NowProvider {
    private Temporal now;

    Static (Temporal now) {
      this.now = now;
    }

    @Override
    public Temporal get() {
      return now;
    }
  }
}
