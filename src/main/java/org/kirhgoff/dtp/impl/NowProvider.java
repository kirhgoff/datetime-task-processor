package org.kirhgoff.dtp.impl;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public interface NowProvider {
  Temporal get();

  static NowProvider system() {
    return () -> LocalDateTime.now();
  }
}
