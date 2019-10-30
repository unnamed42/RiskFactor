package com.tjh.riskfactor.util;

public interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;

}
