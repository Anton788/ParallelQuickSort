package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class ParallelQuickSort {
    public void sort(int[] arr) {
        ForkJoinPool pool = new ForkJoinPool(4);
        pool.invoke(new Sorting(arr, 0, arr.length - 1));
    }

    private class Sorting extends RecursiveAction {
        private final int singleBlock = 32;

        int[] array;
        int left_border;
        int right_border;

        Sorting(int[] a, int left, int right) {
            this.array = a;
            this.left_border = left;
            this.right_border = right;
        }

        private int partition(int[] array, int begin, int end) {
            int pivot = array[(begin + end) / 2];
            int left = begin;
            int right = end;
            while (left < right){
                if (array[left] < pivot){
                    ++left;
                } else if (array[right] > pivot) {
                    --right;
                } else {
                    int tmp = array[right];
                    array[right] = array[left];
                    array[left] = tmp;
                    ++left;
                    --right;
                }
            }
            return right;
        }

        @Override
        protected void compute() {
            if(right_border <= left_border) return;
            if ((right_border - left_border) > singleBlock) {
                int j = partition(array, left_border, right_border);
                invokeAll(new Sorting(array, left_border, j - 1), new Sorting(array, j + 1, right_border));
            }else{
                quickSort(array, left_border, right_border);
            }
        }

        public void quickSort(int[] array, int begin, int end) {
            if (begin < end) {
                int partitionIndex = partition(array, begin, end);

                quickSort(array, begin, partitionIndex);
                quickSort(array, partitionIndex + 1, end);
            }
        }
    }
}