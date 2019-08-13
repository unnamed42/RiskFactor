package com.tjh.riskfactor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtils {

    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> ret = new ArrayList<>();
        iterable.forEach(ret::add);
        return ret;
    }

    public static <T> Stream<T> asStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

}
