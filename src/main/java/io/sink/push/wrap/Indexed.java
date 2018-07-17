package io.sink.push.wrap;

public final class Indexed<T> {

    private final long index;
    private final T value;

    private Indexed(final long index, final T value) {
        this.index = index;
        this.value = value;
    }

    public static <T> Indexed<T> of(final long index, final T value) {
        return new Indexed<>(index, value);
    }

    public long index() {
        return index;
    }

    public T value() {
        return value;
    }
}
