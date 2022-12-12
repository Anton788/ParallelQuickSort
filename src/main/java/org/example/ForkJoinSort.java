package org.example;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ForkJoinSort {
    public static void main(String[] args) {
        int[] arr = new int[100_000_000];
        for (int i = 0; i < 100_000_000; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(0, 100_000);
        }
        int[] check_arr = arr.clone();
        ParallelQuickSort quickSort = new ParallelQuickSort();
        long start = System.nanoTime();
        quickSort.sort(arr);
        long finish = System.nanoTime();
        System.out.println(finish - start);
        int[] stream = Arrays.stream(check_arr).sorted().toArray();
        boolean correct = true;
        for (int i = 0; i < arr.length; i++) {
            if (stream[i] != arr[i]){
                System.out.println(i);
                System.out.println(stream[i] + " " + arr[i]);
                correct = false;
                break;
            }
        }
        if (correct){
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect result of sorting!");
        }
        // Attempt 1
        // 3430789700
        // Correct!
        //
        // Attempt 2
        // 3264368100
        // Correct!
        //
        // Attempt 3
        // 2633887400
        // Correct!
        //
        // Attempt 4
        // 2793114900
        // Correct!
        //
        // Attempt 5
        // 3071168700
        // Correct!
        //
    }
}
