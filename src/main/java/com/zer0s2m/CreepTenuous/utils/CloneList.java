package com.zer0s2m.CreepTenuous.utils;

import java.util.ArrayList;
import java.util.List;

public interface CloneList {
    static <E> List<E> cloneOneLevel(List<E> list) {
        return new ArrayList<>(list);
    }
}
