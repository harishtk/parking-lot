package com.parkinglot.app.domain;

public interface IdGenerator<T, I> {
    T next(I input);
}
