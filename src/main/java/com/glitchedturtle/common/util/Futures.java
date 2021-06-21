package com.glitchedturtle.common.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Futures {

    public static <I> CompletableFuture<I> wrap(Consumer<I> onSuccess) {

        CompletableFuture<I> future = new CompletableFuture<>();

        future.thenAccept(onSuccess);
        future.exceptionally((ex) -> {

            ex.printStackTrace();
            return null;

        });

        return future;

    }

}
