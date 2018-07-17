package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.function.Consumer;

final class ForEachSink<I, O> extends BaseSink<I, O> {

    ForEachSink(final Sink<I, O> upSink, final Consumer<? super O> consumer) {
        super(upSink.hole());

        upSink.attachDrain(element -> {
            consumer.accept(element);
            pushDownDrain(element);
        });
    }
}