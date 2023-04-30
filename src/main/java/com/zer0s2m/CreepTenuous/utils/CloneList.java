package com.zer0s2m.CreepTenuous.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface CloneList {
    static <E> List<E> cloneOneLevel(List<E> list) {
        return new ArrayList<>(list);
    }

    static <E> List<E> cloneOneLevel(List<E> list1, List<E> list2) {
        List<E> newList1 = new ArrayList<>(list1);
        List<E> newList2 = new ArrayList<>(list2);

        return Stream.concat(
                newList1.stream(),
                newList2.stream()
        )
                .toList();
    }
}
