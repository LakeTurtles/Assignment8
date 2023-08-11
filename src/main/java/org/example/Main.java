package org.example;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import static org.example.ThreadColor.*;


public class Main {
public static ReentrantLock bufferLock = new ReentrantLock( true);

    public synchronized static void main(String[] args) {

        MultiThreading22 multiThread = new MultiThreading22();
        List<Integer> numbersCollection = Collections.synchronizedList(new ArrayList<>(1000));
        ExecutorService service1 = Executors.newCachedThreadPool();

        List<CompletableFuture<Void>> tasks = new ArrayList<>(1000);

            for (int i = 0; i < 1000; i++) {
                bufferLock.lock();
                try {
                    CompletableFuture<Void> task = CompletableFuture.supplyAsync(()-> multiThread.getNumbers(), service1)
                        .thenAccept(numbers -> numbersCollection.addAll(numbers));

                    tasks.add(task);

                    } finally {
                        bufferLock.unlock();
                    }

            }

//            while (tasks.stream().filter(CompletableFuture::isDone).count() < 1000) // same as lambda below
            while (tasks.stream().filter(task -> task.isDone()).count() < 1000)
            {
                try {
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                }
            }

        System.out.println("------------------------------------------------------------");
        System.out.println(ANSI_BLUE + "Done getting all the numbers");
        System.out.println("There are : "+ ANSI_RED+ numbersCollection.size() + ANSI_CYAN + " numbers in the list.");

        System.out.println("------------------------------------------------------------");

        HashMap<Integer, Integer> integerCountMap = new HashMap<>();

        for (Integer num : numbersCollection) {
            integerCountMap.put(num, integerCountMap.getOrDefault(num, 0) + 1);
        }

        for (Integer num : integerCountMap.keySet()) {
            int count = integerCountMap.get(num);
            System.out.println(ANSI_RED+num +ANSI_BLUE + " occurs "+ANSI_RED + count + ANSI_BLUE+" times.");
        }

        System.out.println("--------------------------------------------");

        Map<Integer, Long> occurenceMap = numbersCollection.stream()
                .collect(Collectors.groupingBy(
                        integer -> integer,
                        Collectors.counting()
                ));

        occurenceMap.forEach((integer, count) -> {
            System.out.println(ANSI_PURPLE+ "Integer: "+ ANSI_YELLOW + integer + ANSI_BLUE+ " Occurrences: "+ ANSI_RED + count);
        });


        service1.shutdown();
    }
}