package com.tjh.riskfactor.util;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;

}
