package es.uva.hilos;

import java.util.concurrent.BlockingQueue;

public class WordVowelCounter implements Runnable {
    private BlockingQueue<String> wordQueue;
    private BlockingQueue<Result> resultQueue;
    private volatile boolean ejecutar = true;

    // Constructor
    public WordVowelCounter(BlockingQueue<String> wordQueue, BlockingQueue<Result> resultQueue) {
        this.wordQueue = wordQueue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        while(ejecutar){
            try {
                String word = wordQueue.take();

                if(word.equals("#")){
                    break;
                }

                int countVowels = countVowels(word);

                Result result = new Result(word, countVowels);
                resultQueue.put(result);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    private int countVowels(String word) {
        String [] letters = word.toLowerCase().split("");
        int vowelCount = 0;

        for(String letter:letters){
            switch (letter) {
                case "a":
                case "e":
                case "i":
                case "o":
                case "u":
                    vowelCount++;
                    break;
                default:
                    break;
            }
        }

        return vowelCount;
    }
}

