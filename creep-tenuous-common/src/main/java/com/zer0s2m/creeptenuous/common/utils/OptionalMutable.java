package com.zer0s2m.creeptenuous.common.utils;

public final class OptionalMutable<T> {
    private T value;

    public OptionalMutable(T value) {
        this.value = value;
    }

    public OptionalMutable() { }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void cleanValue() {
        this.value = null;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public boolean isEmpty() {
        return this.value == null;
    }
}
