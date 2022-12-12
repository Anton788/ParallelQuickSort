package org.example;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        int[] arr = new int[100_000_000];
        for (int i = 0; i < 100_000_000; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(0, 100_000);
        }
        int[] check_arr = arr.clone();
        QuickSort quickSort = new QuickSort();
        long start = System.nanoTime();
        quickSort.quickSort(arr, 0, arr.length - 1);
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
        // 8416892800
        // Correct!
        //
        // Attempt 2
        // 8293289800
        // Correct!
        //
        // Attempt 3
        // 9302296500
        // Correct!
        //
        // Attempt 4
        // 8253262600
        // Correct!
        //
        // Attempt 5
        // 9267214400
        // Correct!
        //
    }
}