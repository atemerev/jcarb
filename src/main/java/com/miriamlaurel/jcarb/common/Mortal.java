package com.miriamlaurel.jcarb.common;

public interface Mortal {
    void stop();
    default void onTermination(Throwable e) {
        e.printStackTrace();
    }
}
