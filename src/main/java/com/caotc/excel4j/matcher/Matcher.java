package com.caotc.excel4j.matcher;

import java.util.function.Predicate;

public interface Matcher<T> extends Predicate<T> {
  Matcher<T> add(Predicate<T> predicate);
  
  Matcher<T> not();

  Matcher<T> and();

  Matcher<T> or();

  Matcher<T> endNot();

  Matcher<T> endAnd();

  Matcher<T> endOr();
}