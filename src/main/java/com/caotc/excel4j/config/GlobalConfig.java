package com.caotc.excel4j.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeToken;

public class GlobalConfig {
  private static final Map<Class<?>, Supplier<?>> CLASS_TO_SUPPLIERS = Maps.newHashMap();

  static {
    setSupplier(List.class, Lists::newArrayList);
    setSupplier(Set.class, Sets::newHashSet);
    setSupplier(Collection.class, Lists::newArrayList);
    setSupplier(Multimap.class, ArrayListMultimap::create);
    setSupplier(Table.class, HashBasedTable::create);
  }

  public static <T> void setSupplier(Class<T> type, Supplier<T> supplier) {
    CLASS_TO_SUPPLIERS.put(type, supplier);
  }

  @SuppressWarnings("unchecked")
  public static <T> Optional<Supplier<T>> getSupplier(Class<T> type) {
    // TODO
    return TypeToken.of(type).getTypes().rawTypes().stream().filter(CLASS_TO_SUPPLIERS::containsKey)
        .findFirst().map(CLASS_TO_SUPPLIERS::get).map(supplier -> (Supplier<T>) supplier);
  }
}