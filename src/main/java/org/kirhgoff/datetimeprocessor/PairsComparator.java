package org.kirhgoff.datetimeprocessor;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.Comparator;

public class PairsComparator<T> implements Comparator<Pair<LocalDateTime, T>> {

  @Override
  public int compare(Pair<LocalDateTime, T> o1, Pair<LocalDateTime, T> o2) {
    //TODO implement me with null checks
    return 0;
  }
}
