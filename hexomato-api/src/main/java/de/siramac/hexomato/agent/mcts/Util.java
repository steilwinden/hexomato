package de.siramac.hexomato.agent.mcts;

import java.util.stream.IntStream;

public class Util {

    public static int getArgMax(double[] array) {
        return IntStream.range(0, array.length)
                .reduce((i, j) -> array[i] >= array[j] ? i : j)
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
    }
}
