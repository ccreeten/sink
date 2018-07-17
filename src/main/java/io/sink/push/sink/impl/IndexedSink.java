package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;
import io.sink.push.wrap.Indexed;

import java.util.concurrent.atomic.AtomicLong;

final class IndexedSink<I, O> extends BaseSink<I, Indexed<O>> {

    IndexedSink(final Sink<I, O> upSink) {
        super(upSink.hole());

        final AtomicLong index = new AtomicLong();
        upSink.attachDrain(element -> pushDownDrain(Indexed.of(index.getAndIncrement(), element)));
    }
}