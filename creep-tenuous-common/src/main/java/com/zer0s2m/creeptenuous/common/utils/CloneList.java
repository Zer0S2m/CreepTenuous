package com.zer0s2m.creeptenuous.common.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface CloneList {

    @Contract(value = "_ -> new", pure = true)
    static <E> @NotNull List<E> cloneOneLevel(List<E> list) {
        return new ArrayList<>(list);
    }

    static <E> List<E> cloneOneLevel(List<E> list1, List<E> list2) {
        List<E> newList1 = cloneOneLevel(list1);
        List<E> newList2 = cloneOneLevel(list2);

        if (newList1.isEmpty() && !newList2.isEmpty()) {
            return newList2;
        }
        if (newList2.isEmpty() && !newList1.isEmpty()) {
            return newList1;
        }
        if (newList2.isEmpty()) {
            return new ArrayList<>();
        }

        return Stream.concat(
                        newList1.stream(),
                        newList2.stream()
                )
                .toList();
    }

}
