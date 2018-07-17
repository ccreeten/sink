package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.BiFunction;

final class ZipWithSink<I, O> extends BaseSink<I, O> {

    <T, V> ZipWithSink(final Sink<I, T> upSink, final Sink<?, V> other, final BiFunction<? super T, ? super V, ? extends O> mappingFunction) {
        super(upSink.hole());

        final Queue<T> left = new ArrayDeque<>();
        final Queue<V> right = new ArrayDeque<>();

        upSink.attachDrain(element -> {
            if (right.isEmpty()) {
                left.offer(element);
            } else {
                pushDownDrain(mappingFunction.apply(element, right.poll()));
            }
        });

        other.attachDrain(element -> {
            if (left.isEmpty()) {
                right.offer(element);
            } else {
                pushDownDrain(mappingFunction.apply(left.poll(), element));
            }
        });
    }
}