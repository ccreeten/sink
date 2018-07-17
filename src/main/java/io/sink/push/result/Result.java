package io.sink.push.result;

public interface Result<I, O> {

    void accept(I element);

    O current();
}
