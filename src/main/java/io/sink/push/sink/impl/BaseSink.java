package io.sink.push.sink.impl;

import io.sink.push.Pushable;
import io.sink.push.wrap.Indexed;
import io.sink.push.result.Result;
import io.sink.push.sink.Sink;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

public abstract class BaseSink<I, O> implements Sink<I, O> {

    private Pushable<I> hole;
    private Pushable<? super O> drain;

    BaseSink(final Pushable<I> hole) {
        this.hole = hole;
        this.drain = __ -> { };
    }

    void attachHole(Pushable<I> hole) {
        this.hole = hole;
    }

    void pushDownDrain(final O element) {
        drain.push(element);
    }

    @Override
    public Pushable<I> hole() {
        return hole;
    }

    @Override
    public void attachDrain(final Pushable<? super O> drain) {
        this.drain = drain;
    }

    @Override
    public void push(final I element) {
        hole.push(element);
    }

    @Override
    public Sink<I, O> filter(final Predicate<? super O> predicate) {
        return new FilterSink<>(this, predicate);
    }

    @Override
    public Sink<I, O> distinct() {
        final Set<O> seen = newKeySet();
        return filter(seen::add);
    }

    @Override
    public Sink<I, O> limit(final long limit) {
        final AtomicLong passed = new AtomicLong();
        return filter(__ -> passed.getAndIncrement() < limit);
    }

    @Override
    public Sink<I, O> skip(final long skip) {
        final AtomicLong passed = new AtomicLong();
        return filter(__ -> passed.getAndIncrement() >= skip);
    }

    @Override
    public Sink<I, O> pushWhile(final Predicate<? super O> predicate) {
        final AtomicBoolean skip = new AtomicBoolean();
        return filter(element -> {
            if (skip.get()) {
                return false;
            }
            final boolean matched = predicate.test(element);
            skip.set(!matched);
            return matched;
        });
    }

    @Override
    public Sink<I, O> skipWhile(final Predicate<? super O> predicate) {
        final AtomicBoolean take = new AtomicBoolean();
        return filter(element -> {
            if (take.get()) {
                return true;
            }
            final boolean matched = predicate.test(element);
            take.set(!matched);
            return !matched;
        });
    }

    @Override
    public Sink<I, O> forEach(final Consumer<? super O> consumer) {
        return new ForEachSink<>(this, consumer);
    }

    @Override
    public Sink<I, List<O>> sliding(final int windowSize) {
        return new SlidingSink<>(this, windowSize);
    }

    @Override
    public Sink<I, Indexed<O>> indexed() {
        return new IndexedSink<>(this);
    }

    @Override
    public <T> Sink<I, T> map(final Function<? super O, ? extends T> mappingFunction) {
        return new MapSink<>(this, mappingFunction);
    }

    @Override
    public <T> Sink<I, T> pairMap(final BiFunction<? super O, ? super O, ? extends T> mappingFunction) {
        return new PairMapSink<>(this, mappingFunction);
    }

    @Override
    public Sink<I, O> collect(final Result<? super O, ?> result) {
        return new CollectSink<>(this, result);
    }

    @Override
    public <T, V> Sink<I, T> zipWith(final Sink<?, V> other, final BiFunction<? super O, ? super V, ? extends T> mappingFunction) {
        return new ZipWithSink<>(this, other, mappingFunction);
    }

    @Override
    public <T> T chain(final Function<Sink<I, O>, ? extends T> chainingStep) {
        return chainingStep.apply(this);
    }
}
