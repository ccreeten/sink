package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.Optional;

final class UnpackSink<I, O> extends BaseSink<I, O> {

    UnpackSink(final Sink<I, Optional<O>> upSink) {
        super(upSink.hole());
        upSink.attachDrain(element -> element.ifPresent(this::pushDownDrain));
    }
}