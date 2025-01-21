package es.uva.hilos;

public class Result {
    private final String word;
    private final int vowelCount;

    public Result(String word, int vowelCount) {
        this.word = word;
        this.vowelCount = vowelCount;
    }

    public String getWord() {
        return word;
    }

    public int getVowelCount() {
        return vowelCount;
    }
}
