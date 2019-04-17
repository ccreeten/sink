package io.sink.push.sink.impl;

import io.sink.push.Pushable;
import io.sink.push.sink.Sink;

final class TeeSink<I, O> extends BaseSink<I, O> {

    @SafeVarargs
    TeeSink(final Sink<I, O> upSink, final Pushable<? super O>... others) {
        super(upSink);
        upSink.attachDrain(element -> {
            pushDownDrain(element);
            for (final Pushable<? super O> other : others) {
                other.push(element);
            }
        });
    }
}