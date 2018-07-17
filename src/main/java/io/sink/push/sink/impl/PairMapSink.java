package io.sink.push.sink.impl;

import io.sink.push.wrap.ByRef;
import io.sink.push.sink.Sink;

import java.util.function.BiFunction;

final class PairMapSink<I, O> extends BaseSink<I, O> {

    <T> PairMapSink(final Sink<I, T> upSink, final BiFunction<? super T, ? super T, ? extends O> mappingFunction) {
        super(upSink.hole());

        final ByRef<T> first = ByRef.none();
        upSink.attachDrain(element -> {
            if (!first.exists()) {
                first.set(element);
            } else {
                pushDownDrain(mappingFunction.apply(first.take(), element));
            }
        });
    }
}