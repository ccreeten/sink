package io.sink.push.wrap;

import java.util.function.Function;

public final class ByRef<T> {

    private T value;

    private ByRef(final T value) {
        this.value = value;
    }

    public static <T> ByRef<T> none() {
        return new ByRef<>(null);
    }

    public static <T> ByRef<T> of(final T value) {
        return new ByRef<>(value);
    }

    public T get() {
        return value;
    }

    public T take() {
        final T value = get();
        set(null);
        return value;
    }

    public void set(final T value) {
        this.value = value;
    }

    public void update(final Function<? super T, ? extends T> updateFunction) {
        set(updateFunction.apply(get()));
    }

    public boolean exists() {
        return get() != null;
    }
}