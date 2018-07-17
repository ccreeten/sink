package io.sink.push.sink.impl;

public final class IdentitySink<I> extends BaseSink<I, I> {

    public IdentitySink() {
        super(__ -> { });
        attachHole(this::pushDownDrain);
    }
}