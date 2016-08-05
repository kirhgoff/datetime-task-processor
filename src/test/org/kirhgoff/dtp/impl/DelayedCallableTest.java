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

  @Test
  public void testPlusDelay() {
    LocalDateTime now = LocalDateTime.now();

    check(now, 100, now.plus(100, ChronoUnit.MILLIS));
    check(now, -50, now.minus(50, ChronoUnit.MILLIS));
    check(now, -1000, now.minus(1, ChronoUnit.SECONDS));
  }

  private void check(LocalDateTime now, int expectedDelay, LocalDateTime start) {
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
