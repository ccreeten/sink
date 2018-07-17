package io.sink.push.result.impl;

import io.sink.push.result.Result;
import io.sink.push.wrap.ByRef;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class DiscreteResult {

    private DiscreteResult() {
    }

    public static <I> Result<I, Optional<I>> findAny(final Predicate<? super I> predicate) {
        return Results.findAny(predicate, DiscreteResult::of);
    }

    public static <I> Result<I, Long> count() {
        return Results.count(DiscreteResult::of);
    }

    public static <I, O> Result<I, O> collect(final Collector<? super I, ?, ? extends O> collector) {
        return Results.collect(collector, DiscreteResult::of);
    }

    private static <I, A, O> Result<I, O> of(final Supplier<ByRef<A>> identity, final BiConsumer<ByRef<A>, ? super I> accumulator, final Function<A, ? extends O> finisher) {
        return new Result<I, O>() {

            final ByRef<A> acc = identity.get();

            @Override
            public void accept(final I element) {
                accumulator.accept(acc, element);
            }

            @Override
            public O current() {
                O current = finisher.apply(acc.get());
                acc.set(identity.get().get());
                return current;
            }
        };
    }
}
