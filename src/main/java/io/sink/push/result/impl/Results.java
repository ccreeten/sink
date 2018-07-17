package io.sink.push.result.impl;

import io.sink.push.result.Result;
import io.sink.push.wrap.ByRef;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;

final class Results {

    static <I> Result<I, Optional<I>> findAny(final Predicate<? super I> predicate, final Creator<I, I, Optional<I>> creator) {
        return creator.create(
                ByRef::none,
                (acc, element) -> { if (!acc.exists() && predicate.test(element)) acc.set(element); },
                Optional::ofNullable
        );
    }

    static <I> Result<I, Long> count(final Creator<I, Long, Long> creator) {
        return creator.create(
                () -> ByRef.of(0L),
                (acc, element) -> acc.update(count -> count + 1),
                identity()
        );
    }

    static <I, A, O> Result<I, O> collect(final Collector<? super I, A, ? extends O> collector, final Creator<I, A, O> creator) {
        return creator.create(
                () -> ByRef.of(collector.supplier().get()),
                (acc, element) -> collector.accumulator().accept(acc.get(), element),
                collector.finisher()
        );
    }

    interface Creator<I, A, O> {

        Result<I, O> create(Supplier<ByRef<A>> identity, BiConsumer<ByRef<A>, ? super I> accumulator, Function<A, ? extends O> finisher);
    }
}
