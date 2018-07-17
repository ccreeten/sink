package io.sink.push.sink;

import io.sink.push.Pushable;
import io.sink.push.result.Result;
import io.sink.push.result.impl.ContinuousResult;
import io.sink.push.result.impl.DiscreteResult;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import static io.sink.push.sink.impl.SinkExtensions.flatten;
import static io.sink.push.sink.impl.SinkExtensions.group;
import static io.sink.push.sink.impl.SinkExtensions.unpack;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

public class SinkTest {

    @Test
    public void attachDrain() {
        final Sink<String, Integer> parseSink = Sink.<String>create()
                .map(Integer::parseInt);
        final Pushable<Number> numberPrinting = Sink.<Number>create()
                .forEach(System.out::println);
        parseSink.attachDrain(numberPrinting);

        parseSink.push("1", "10");

//      1
//      10
    }

    @Test
    public void simple() {
        final Pushable<Integer> echoSink = Sink.<Integer>create().forEach(System.out::println);
        echoSink.push(1, 2, 3);
        echoSink.push(4, 5, 6);

//      1
//      2
//      3
//      4
//      5
//      6
    }

    @Test
    public void printTwice() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .forEach(System.out::println);

        sink.push(1, 2, 3);

//      1
//      1
//      2
//      2
//      3
//      3
    }

    @Test
    public void filtering() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .filter(element -> element % 2 == 1)
                .forEach(System.out::println);

        sink.push(1, 2, 3, 4);

//      1
//      3
    }

    @Test
    public void distinct() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .distinct()
                .forEach(System.out::println);

        sink.push(1, 2, 1, 3, 1, 2);

//      1
//      2
//      3
    }

    @Test
    public void mapping() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .map(element -> element + 1)
                .forEach(System.out::println);

        sink.push(1, 2, 3);
        sink.push(4, 5, 6);

//      2
//      3
//      4
//      5
//      6
//      7
    }

    @Test
    public void collectingDiscreteSimple() {
        final Result<Integer, Number> sum = DiscreteResult.collect(summingInt(unboxed()));
        final Pushable<Integer> sink = Sink.<Integer>create().collect(sum);

        sink.push(1, 2, 3);
        System.out.println(sum.current());

        sink.push(4, 5, 6);
        System.out.println(sum.current());

//      6
//      15
    }

    @Test
    public void collectingDiscreteComplex() {
        final Result<Integer, Map<Integer, Long>> tally = DiscreteResult.collect(groupingBy(identity(), counting()));
        final Pushable<Integer> sink = Sink.<Integer>create().collect(tally);

        sink.push(1, 2, 3);
        System.out.println(tally.current());

        sink.push(4, 3, 2);
        System.out.println(tally.current());

//      {1=1, 2=1, 3=1}
//      {2=1, 3=1, 4=1}
    }

    @Test
    public void collectingContinuous() {
        final Result<String, List<String>> characters = ContinuousResult.collect(toList());
        final Pushable<String> sink = Sink.<String>create().collect(characters);

        sink.push("a", "b", "c");
        System.out.println(characters.current());

        sink.push("d", "e", "f");
        System.out.println(characters.current());

//      [a, b, c]
//      [a, b, c, d, e, f]
    }

    @Test
    public void collectingContinuousComplex() {
        final Result<String, StringBuilder> concat = ContinuousResult.collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append));
        final Pushable<String> sink = Sink.<String>create().collect(concat);

        sink.push("a", "b", "c");
        System.out.println(concat.current());

        sink.push("d", "e", "f");
        System.out.println(concat.current());

//      abc
//      abcdef
    }

    @Test
    public void crazyCollecting() {
        final Result<Integer, Integer> sum = DiscreteResult.collect(summingInt(unboxed()));
        final Result<Integer, Map<Integer, Long>> tally = DiscreteResult.collect(groupingBy(identity(), counting()));
        final Result<String, List<String>> characters = ContinuousResult.collect(toList());
        final Result<String, StringBuilder> concat = ContinuousResult.collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append));

        final Pushable<Integer> sink = Sink.<Integer>create()
                .collect(sum)
                .collect(tally)
                .map(String::valueOf)
                .collect(characters)
                .collect(concat);

        sink.push(1, 2, 3);
        System.out.println(sum.current());
        System.out.println(tally.current());
        System.out.println(characters.current());
        System.out.println(concat.current());

        sink.push(1, 2, 3, 4, 5, 6);
        System.out.println(sum.current());
        System.out.println(tally.current());
        System.out.println(characters.current());
        System.out.println(concat.current());

//      6
//      {1=1, 2=1, 3=1}
//      [1, 2, 3]
//      123
//      21
//      {1=1, 2=1, 3=1, 4=1, 5=1, 6=1}
//      [1, 2, 3, 1, 2, 3, 4, 5, 6]
//      123123456
    }

    @Test
    public void findAnyDiscrete() {
        final Result<Integer, Optional<Integer>> any = DiscreteResult.findAny(element -> (element % 3) == 0);

        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .collect(any)
                .forEach(System.out::println);

        sink.push(1, 2, 4, 8, 6, 9);
        System.out.println(any.current());

        sink.push(555);
        System.out.println(any.current());

//      1
//      1
//      2
//      2
//      4
//      4
//      8
//      8
//      6
//      6
//      9
//      9
//      Optional[6]
//      555
//      555
//      Optional[555]
    }

    @Test
    public void findAnyContinuous() {
        final Result<Integer, Optional<Integer>> any = ContinuousResult.findAny(element -> (element % 3) == 0);

        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .collect(any)
                .forEach(System.out::println);

        sink.push(1, 2, 4, 8, 6, 9);
        System.out.println(any.current());

        sink.push(555);
        System.out.println(any.current());

//      1
//      1
//      2
//      2
//      4
//      4
//      8
//      8
//      6
//      6
//      9
//      9
//      Optional[6]
//      555
//      555
//      Optional[6]
    }

    @Test
    public void limit() {
        final Result<Integer, Optional<Integer>> any = DiscreteResult.findAny(element -> (element % 3) == 0);

        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .limit(4)
                .forEach(System.out::println)
                .collect(any);

        sink.push(1, 2, 4, 8, 6, 9);
        System.out.println(any.current());

//      1
//      1
//      2
//      2
//      4
//      4
//      8
//      8
//      6
//      9
//      Optional.empty
    }

    @Test
    public void skip() {
        final Result<Integer, Optional<Integer>> any = DiscreteResult.findAny(element -> (element % 3) == 0);

        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .skip(4)
                .forEach(System.out::println)
                .collect(any);

        sink.push(1, 2, 4, 8, 6, 9);
        System.out.println(any.current());

//      1
//      2
//      4
//      8
//      6
//      6
//      9
//      9
//      Optional[6]
    }

    @Test
    public void pushWhile() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .pushWhile(element -> element < 4)
                .forEach(System.out::println);

        sink.push(1, 2, 4, 8, 6, 9);

//      1
//      1
//      2
//      2
//      4
//      8
//      6
//      9
    }

    @Test
    public void skipWhile() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .forEach(System.out::println)
                .skipWhile(element -> element < 4)
                .forEach(System.out::println);

        sink.push(1, 2, 4, 8, 6, 9);

//      1
//      2
//      4
//      4
//      8
//      8
//      6
//      6
//      9
//      9
    }

    @Test
    public void pairMap() {
        final Result<Integer, Long> seenPerActivation = DiscreteResult.count();
        final Result<Integer, Long> totalSeen = ContinuousResult.count();

        final Pushable<Integer> sink = Sink.<Integer>create()
                .pairMap((left, right) -> left + right)
                .forEach(System.out::println)
                .collect(seenPerActivation)
                .collect(totalSeen);

        sink.push(1, 2, 3, 4, 5);
        System.out.println(seenPerActivation.current());
        System.out.println(totalSeen.current());

        sink.push(6);
        System.out.println(seenPerActivation.current());
        System.out.println(totalSeen.current());

//      3
//      7
//      2
//      2
//      11
//      1
//      3
    }

    @Test
    public void zipWith() {
        final Sink<Integer, Integer> numbers = Sink.create();
        final Sink<String, String> numberedStrings = Sink.<String>create()
                .zipWith(numbers, (string, number) -> number + ": " + string)
                .forEach(System.out::println);

        numbers.push(1, 2, 3);
        numberedStrings.push("a", "b", "c");

//      1: a
//      2: b
//      3: c
    }

    @Test
    public void zipWithComplex() {
        final Result<String, List<String>> strings = ContinuousResult.collect(toList());

        final Sink<Integer, Integer> numbers = Sink.create();
        final Sink<String, String> numberedStrings = Sink.<String>create()
                .zipWith(numbers, (string, number) -> number + ": " + string)
                .collect(strings)
                .forEach(System.out::println);

        numberedStrings.push("x", "y", "z");
        numbers.push(3);
        System.out.println(strings.current());

        numbers.push(2);
        System.out.println(strings.current());

        numberedStrings.push("w");
        System.out.println(strings.current());

        numbers.push(1, 0);
        System.out.println(strings.current());

//      3: x
//      [3: x]
//      2: y
//      [3: x, 2: y]
//      [3: x, 2: y]
//      1: z
//      0: w
//      [3: x, 2: y, 1: z, 0: w]
    }

    @Test
    public void bigCounts() {
        final Result<Integer, Map<Integer, Long>> tally = DiscreteResult.collect(groupingBy(identity(), counting()));
        final Pushable<Integer> sink = Sink.<Integer>create().collect(tally);

        final Random random = new Random(0);
        sink.push(random.ints().mapToObj(number -> number & 0xF).limit(10000000));

        System.out.println(tally.current());

//      {0=625067, 1=624816, 2=625106, 3=625173, 4=624940, 5=624749, 6=625024, 7=624842, 8=624987, 9=625158, 10=624966, 11=624896, 12=624944, 13=625180, 14=624952, 15=625200}
    }

    @Test
    public void sliding() {
        final Pushable<Integer> sink = Sink.<Integer>create()
                .sliding(3)
                .forEach(System.out::println);

        final Random random = new Random(0);
        sink.push(random.ints().boxed().limit(10));
        sink.push(1, 2);

//      [-1155484576, -723955400, 1033096058]
//      [-1690734402, -1557280266, 1327362106]
//      [-1930858313, 502539523, -1728529858]
//      [-938301587, 1, 2]
    }

    @Test
    public void indexed() {
        final Result<String, List<String>> combinations = ContinuousResult.collect(toList());
        final Sink<String, String> colours = Sink.create();
        final Sink<String, String> animals = Sink.create();

        colours
                .zipWith(animals, (colour, animal) -> String.format("a %s %s", colour, animal))
                .indexed()
                .map(element -> String.format("%d: %s", element.index(), element.value()))
                .collect(combinations);

        colours.push("brown", "white", "black");
        animals.push("horse", "shark", "bear");

        System.out.println(combinations.current());

//      [0: a brown horse, 1: a white shark, 2: a black bear]
    }

    @Test
    public void chainToAnything() {
        final Sink<Integer, Integer> sink = Sink.<Integer>create()
                .filter(value -> value < 3)
                .forEach(System.out::println);

        sink.push(1, 2, 3);

        final Object type = sink.chain(Sink::getClass);
        System.out.println(type);

//      1
//      2
//      class io.sink.push.sink.impl.ForEachSink
    }

    @Test
    public void grouping() {
        final Result<String, List<String>> letterParts = ContinuousResult.collect(toList());
        final Pushable<String> sink = Sink.<String>create()
                .chain(group())
                .map(letters -> letters.stream().collect(joining()))
                .collect(letterParts);

        sink.push("Mississippi".split(""));
        System.out.println(letterParts.current());

        sink.push("Over 9000!".split(""));
        System.out.println(letterParts.current());

//      [M, i, ss, i, ss, i, pp]
//      [M, i, ss, i, ss, i, pp, i, O, v, e, r,  , 9, 000]
    }

    @Test
    public void unpacking() {
        final Pushable<String> sink = Sink.<String>create()
                .map(Optional::ofNullable)
                .chain(unpack())
                .forEach(System.out::println);

        sink.push(null, "a", null, null, "b", "c");

//      a
//      b
//      c
    }

    @Test
    public void flattening() {
        final Pushable<String> sink = Sink.<String>create()
                .map(Collections::singleton)
                .chain(flatten())
                .map(Collections::singletonList)
                .chain(flatten())
                .forEach(System.out::println);

        sink.push("a", "b", "c");

//      a
//      b
//      c
    }

    private static ToIntFunction<Integer> unboxed() {
        return integer -> integer;
    }
}
