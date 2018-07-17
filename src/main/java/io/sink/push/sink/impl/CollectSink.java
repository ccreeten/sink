package io.sink.push.sink.impl;

import io.sink.push.result.Result;
import io.sink.push.sink.Sink;

final class CollectSink<I, O> extends BaseSink<I, O> {

    CollectSink(final Sink<I, O> upSink, final Result<? super O, ?> result) {
        super(upSink.hole());

        upSink.attachDrain(element -> {
            result.accept(element);
            pushDownDrain(element);
        });
    }
}
