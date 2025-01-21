package es.uva.hilos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TextVowelCounter {

    // Method that takes a String input and returns an ArrayList of words
    public static ArrayList<String> getWords(String input) {
        String cleanInput = input.replaceAll("[^\\w\\s]", "");
        String[] wordsSeparated = cleanInput.split("\\s*[.,¿?¡!]+\\s*");

        return Arrays.stream(wordsSeparated)
                .filter(word -> !word.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
    }

    // Method that counts vowels in a string using parallelism
    public static int getVowels(String input, int parallelism) throws InterruptedException {

        // Create queues
        BlockingQueue<String> wordQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();

        // Split the input into words and add them to the wordQueue
        ArrayList<String> words = getWords(input);
        for (String word : words) {
            wordQueue.put(word);
        }

        for (int i = 0; i < parallelism; i++) {
            wordQueue.put("#");  // Agregar un marcador para cada hilo
        }

        // Create and start the worker threads based on the parallelism parameter
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < parallelism; i++) {
            WordVowelCounter worker = new WordVowelCounter(wordQueue, resultQueue);
            Thread workerThread = new Thread(worker);
            workers.add(workerThread);
            workerThread.start();  // Start each worker thread
        }

        // Wait for all worker threads to finish
        for (Thread worker : workers) {
            worker.join();
        }

        // Gather results from resultQueue
        int totalVowels = 0;
        while (!resultQueue.isEmpty()) {
            Result result = resultQueue.poll();
            totalVowels += result.getVowelCount();
        }

        return totalVowels;
    }
}
