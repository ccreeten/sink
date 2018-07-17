package io.sink.push;

import java.util.stream.Stream;

public interface Pushable<I> {

    void push(I element);

    default void push(I... elements) {
        for (final I element : elements) {
            push(element);
        }
    }

    default void push(Iterable<? extends I> elements) {
        elements.forEach(this::push);
    }

    default void push(Stream<? extends I> elements) {
        elements.forEach(this::push);
    }
}