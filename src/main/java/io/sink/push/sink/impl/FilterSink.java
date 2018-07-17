package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.function.Predicate;

final class FilterSink<I, O> extends BaseSink<I, O> {

    FilterSink(final Sink<I, O> upSink, final Predicate<? super O> predicate) {
        super(upSink.hole());

        upSink.attachDrain(element -> {
            if (predicate.test(element)) {
                pushDownDrain(element);
            }
        });
    }
}