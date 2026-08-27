package cooper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises complete command flows through Cooper's text interface. */
public class CooperSmokeTest {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void run_findMatchingTasks_displaysAllMatches() {
        String output = runCooper("todo read book\n"
                + "deadline return book /by 2026-08-30\n"
                + "todo buy groceries\n"
                + "find book\n"
                + "bye\n");

        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
    }

    @Test
    public void run_findWithoutMatches_displaysNoMatchesMessage() {
        String output = runCooper("todo read book\nfind movie\nbye\n");

        assertTrue(output.contains("No matching tasks found!"));
    }

    private String runCooper(String commands) {
        ByteArrayInputStream input = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(input);
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Path dataFile = temporaryDirectory.resolve("data").resolve("cooper.txt");
        new Cooper(dataFile.toString()).run();
        return output.toString(StandardCharsets.UTF_8);
    }
}
