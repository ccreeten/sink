package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class SinkExtensions {

    private SinkExtensions() {
    }

    public static <I, O> Function<Sink<I, O>, Sink<I, List<O>>> group() {
        return GroupSink::new;
    }

    public static <I, O> Function<Sink<I, Optional<O>>, Sink<I, O>> unpack() {
        return UnpackSink::new;
    }

    // TODO: compiler bug? using U extends Iterable<O> instead of bounded wildcard works (should be no difference?)
    public static <I, O, U extends Iterable<O>> Function<Sink<I, U>, Sink<I, O>> flatten() {
        return FlattenSink::new;
    }
}
