package org.example;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        int[] arr = new int[100_000_000];
        for (int i = 0; i < 100_000_000; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(0, 100_000);
        }
        QuickSort quickSort = new QuickSort();
        long start = System.nanoTime();
        quickSort.quickSort(arr, 0, arr.length - 1);
        long finish = System.nanoTime();
        System.out.println(finish - start);
    }
}