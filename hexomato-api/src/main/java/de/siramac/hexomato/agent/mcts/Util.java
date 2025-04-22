package de.siramac.hexomato.agent.mcts;

import java.util.Comparator;
import java.util.stream.IntStream;

public class Util {

    public static int getArgMax(double[] array) {
        return IntStream.range(0, array.length)  // Create an IntStream of indices
                .boxed()  // Box the stream to stream of Integer objects
                .max(Comparator.comparingDouble(i -> array[i]))  // Find index with the max value
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));  // Handle empty array case
    }
}
