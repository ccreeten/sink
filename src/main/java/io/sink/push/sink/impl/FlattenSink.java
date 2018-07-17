package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

final class FlattenSink<I, O> extends BaseSink<I, O> {

    FlattenSink(final Sink<I, ? extends Iterable<O>> upSink) {
        super(upSink.hole());
        upSink.attachDrain(element -> element.forEach(this::pushDownDrain));
    }
}