package io.sink.push.result.impl;

import io.sink.push.result.Result;
import io.sink.push.wrap.ByRef;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public final class ContinuousResult {

    private ContinuousResult() {
    }

    public static <I> Result<I, Optional<I>> findAny(final Predicate<? super I> predicate) {
        return Results.findAny(predicate, ContinuousResult::of);
    }

    public static <I> Result<I, Long> count() {
        return Results.count(ContinuousResult::of);
    }

    public static <I, O> Result<I, O> collect(final Collector<? super I, ?, ? extends O> collector) {
        if (!collector.characteristics().contains(IDENTITY_FINISH)) {
             // TODO: actually, it can be used as long as the accumulator itself does not get mutated
             throw new IllegalArgumentException("continuous result can only be used with identity finishers");
        }
        return Results.collect(collector, ContinuousResult::of);
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
                return finisher.apply(acc.get());
            }
        };
    }
}
