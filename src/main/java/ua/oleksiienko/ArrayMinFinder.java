package ua.oleksiienko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArrayMinFinder {
    private final int threadCount;
    private final List<Integer> array;
    private final MinHolder result = new MinHolder();
    public ArrayMinFinder(int threadCount, int arraySize) {
        this.threadCount = threadCount;
        this.array = generateArray(arraySize);
    }

    public int findMin() {
        Thread[] threads = startThreads();
        try {
            for (var thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.err.println("ArrayMinFinder was rudely interrupted while waiting for result!");
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to find minimum - interrupted");
        }
        return result.get();
    }

    private List<Integer> generateArray(int size) {
        ArrayList<Integer> result = new ArrayList<>(size);
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int part = findPart(size, i);
            result.add(random.nextInt(part, 1000));
        }
        int minPos = random.nextInt(size);
        result.set(minPos, -1);
        System.out.printf("Finished generation of random array of size %d, with minimum hidden at position: %d\n",
                size, minPos);
        return Collections.unmodifiableList(result);
    }

    private int findPart(int size, int i) {
        return (i < (size % threadCount) * size / threadCount)
                ? i / (size / threadCount + 1)
                : (i - (size % threadCount) * (size / threadCount + 1)) / (size / threadCount) + (size % threadCount);
    }

    private Thread[] startThreads() {
        Thread[] threads = new Thread[threadCount];
        int blockSz = array.size() / threadCount;
        int remainder = array.size() % threadCount;
        int start = 0;
        for (int i = 0; i < threadCount; i++) {
            int fin = start + blockSz + (remainder > 0 ? 1 : 0);
            var calc = new Calculator(start, fin);
            threads[i] = new Thread(calc);
            threads[i].setName("Calculator " + i);
            start = fin;
            remainder--;
        }
        System.out.printf("Starting %d threads with uniformly distributed segments:\n\n", threadCount);
        for (var thread : threads) {
            thread.start();
        }
        return threads;
    }

    private class Calculator implements Runnable {

        private final int from;
        private final int to;

        public Calculator(int from, int to) {
            this.from = from;
            this.to = to;
        }
        @Override
        public void run() {
            int min = array.get(from);
            for (int i = from; i < to; i++) {
                min = Math.min(min, array.get(i));
            }
                System.out.printf("%s finished on segment [%d - %d] with minimum: %d\n",
                    Thread.currentThread().getName(), from, to, min);
            result.update(min);
        }
    }

}
