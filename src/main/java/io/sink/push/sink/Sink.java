package io.sink.push.sink;

import io.sink.push.Pushable;
import io.sink.push.result.Result;
import io.sink.push.sink.impl.IdentitySink;
import io.sink.push.wrap.Indexed;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Sink<I, O> extends Pushable<I> {

    static <T> Sink<T, T> create() {
        return new IdentitySink<>();
    }

    Pushable<I> hole();

    void attachDrain(Pushable<? super O> drain);

    Sink<I, O> filter(Predicate<? super O> predicate);

    Sink<I, O> distinct();

    Sink<I, O> limit(long limit);

    Sink<I, O> skip(long skip);

    Sink<I, O> pushWhile(Predicate<? super O> predicate);

    Sink<I, O> skipWhile(Predicate<? super O> predicate);

    Sink<I, O> forEach(Consumer<? super O> consumer);

    Sink<I, List<O>> sliding(int windowSize);

    Sink<I, Indexed<O>> indexed();

    Sink<I, O> collect(Result<? super O, ?> result);

    <T> Sink<I, T> map(Function<? super O, ? extends T> mappingFunction);

    <T> Sink<I, T> pairMap(BiFunction<? super O, ? super O, ? extends T> mappingFunction);

    <T, V> Sink<I, T> zipWith(Sink<?, V> other, BiFunction<? super O, ? super V, ? extends T> mappingFunction);

    <T> T chain(Function<Sink<I, O>, ? extends T> chainingFunction);
}
