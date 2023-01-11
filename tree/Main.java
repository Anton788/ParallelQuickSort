package org.example.multi.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final ReentrantLock operations_lock = new ReentrantLock();
    private static final Random random = new Random();
    public static final AtomicInteger ops = new AtomicInteger();
    private static void operate(Integer x, TreeSeq bstSeq, TreeParal bstPar, List<Integer> keys, long deadline) {
        while (System.currentTimeMillis() <= deadline) {
            Integer key = keys.get(random.nextInt(keys.size()));
            int p = random.nextInt(101);
            if (p < x) {
                operations_lock.lock();
                bstSeq.insert(key);
                operations_lock.unlock();
                bstPar.insert(key);
            } else if (p < 2 * x) {
                operations_lock.lock();
                bstSeq.delete(key);
                operations_lock.unlock();
                bstPar.delete(key);
            } else if (p >= 2 * x) {
                operations_lock.lock();
                bstSeq.contains(key);
                operations_lock.unlock();
                bstPar.contains(key);
            }
            ops.getAndIncrement();
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int num = 1; num <= 4; num++) {
            System.out.println("Threads: " + num);
            for (Integer x : List.of(0, 10, 50)) {
                TreeSeq treeSeq = new TreeSeq(0);
                TreeParal treeParal = new TreeParal(0);

                List<Integer> keys = IntStream.range(1, 100_000).boxed().collect(Collectors.toList());
                Collections.shuffle(keys);
                keys.forEach(k -> {
                    if (random.nextInt(2) == 0) {
                        treeSeq.insert(k);
                        treeParal.insert(k);
                    }
                });

                List<CompletableFuture<Void>> futures = new ArrayList<>();
                long deadline = System.currentTimeMillis() + 5000;
                for (int i = 0; i < num; i++) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                            () -> operate(x, treeSeq, treeParal, keys, deadline),
                            executor
                    );
                    futures.add(completableFuture);
                }
                for (CompletableFuture<Void> future : futures) {
                    future.get();
                }
                int ops = Main.ops.get() / 5;
                List<Integer> nodesFromSeq = treeSeq.inorderTraversal();
                List<Integer> nodesFromPar = treeParal.inorderTraversal();
                Collections.sort(nodesFromSeq);
                Collections.sort(nodesFromPar);
                if (nodesFromSeq.equals(nodesFromPar)){
                    System.out.println("Correct! " + "x: " + x);
                    System.out.println("Ops: " + ops);
                }
                //System.out.println("x: " + x + " ops: " + ops);
                Main.ops.set(0);
            }
            System.out.println();
        }
        executor.shutdown();
    }
}
