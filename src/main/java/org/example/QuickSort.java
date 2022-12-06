package org.example;

public class QuickSort {
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
    public void quickSort(int[] array, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(array, begin, end);

            quickSort(array, begin, partitionIndex);
            quickSort(array, partitionIndex + 1, end);
        }
    }
}
