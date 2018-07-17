package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.ArrayList;
import java.util.List;

final class SlidingSink<I, O> extends BaseSink<I, List<O>> {

    SlidingSink(final Sink<I, O> upSink, final int windowSize) {
        super(upSink.hole());

        final List<O> window = new ArrayList<>(windowSize);
        upSink.attachDrain(element -> {
            window.add(element);
            if (window.size() == windowSize) {
                pushDownDrain(new ArrayList<>(window));
                window.clear();
            }
        });
    }
}