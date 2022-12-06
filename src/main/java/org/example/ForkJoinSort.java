package org.example;

import java.util.concurrent.ThreadLocalRandom;

public class ForkJoinSort {
    public static void main(String[] args) {
        int[] arr = new int[100_000_000];
        for (int i = 0; i < 100_000_000; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(0, 100_000);
        }
        ParallelQuickSort quickSort = new ParallelQuickSort();
        long start = System.nanoTime();
        quickSort.sort(arr);
        long finish = System.nanoTime();
        System.out.println(finish - start);
    }
}
