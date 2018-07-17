package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.function.Function;

final class MapSink<I, O> extends BaseSink<I, O> {

    <T> MapSink(final Sink<I, T> upSink, final Function<? super T, ? extends O> mappingFunction) {
        super(upSink.hole());
        upSink.attachDrain(element -> pushDownDrain(mappingFunction.apply(element)));
    }
}