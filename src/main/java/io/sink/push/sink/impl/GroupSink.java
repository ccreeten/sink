package io.sink.push.sink.impl;

import io.sink.push.sink.Sink;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class GroupSink<I, O> extends BaseSink<I, List<O>> {

    GroupSink(final Sink<I, O> upSink) {
        super(upSink.hole());

        final Deque<O> group = new ArrayDeque<>();
        upSink.attachDrain(element -> {
            if (!group.isEmpty() && !element.equals(group.peekLast())) {
                pushDownDrain(new ArrayList<>(group));
                group.clear();
            }
            group.add(element);
        });
    }
}