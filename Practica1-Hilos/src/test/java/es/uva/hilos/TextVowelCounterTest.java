package es.uva.hilos;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class TextVowelCounterTest {

    @Test
    public void emptyTestCase() throws InterruptedException{
        assertTrue(TextVowelCounter.getVowels("", 1) == 0);
    }

    @Test
    public void oneWordCase() throws InterruptedException{
        assertTrue(TextVowelCounter.getVowels("Asterisk", 1) == 3);
    }

    @Test
    public void manyCase() throws InterruptedException{
        assertTrue(TextVowelCounter.getVowels("Asterisk Test 123 Cow", 3) == 5);
    }

    @Test
    public void tooManyWorkers() throws InterruptedException{
        assertTrue(TextVowelCounter.getVowels("Asterisk", 10) == 3);
    }
}
