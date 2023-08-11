package org.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.ThreadColor.*;

public class MultiThreading22 {

    private List<Integer> numbers = null;
    private AtomicInteger i = new AtomicInteger(0);

    public MultiThreading22() {
        try {
            numbers = Files.readAllLines(Paths.get("output.txt"))
                    .stream()
                    .map(n -> Integer.parseInt(n))
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getNumbers() {
        int start, end;
        synchronized (i) {
            start = i.get();
            end = i.addAndGet(1000);

            System.out.println(ANSI_BLUE + "Starting to fetch records " + ANSI_YELLOW + start + " to " + (end));
        }
        // force thread to pause for half a second to simulate actual Http / API traffic
        // delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Integer> newList = new ArrayList<>();
        IntStream.range(start, end)
                .forEach(n -> {
                    newList.add(numbers.get(n));
                });
        System.out.println(ANSI_PURPLE + "Done Fetching records " + ANSI_BLINK + ANSI_CYAN+ start + " to " + (end));
        return newList;
    }

}
