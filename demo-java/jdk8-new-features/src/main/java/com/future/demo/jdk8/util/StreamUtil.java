package com.future.demo.jdk8.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamUtil {
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> function) {
        Set<Object> seen = new HashSet<>();
        return e -> seen.add(function.apply(e));
    }
}
