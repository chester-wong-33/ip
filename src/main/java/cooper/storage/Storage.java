package cooper.storage;

import cooper.exception.CooperException;
import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.Task;
import cooper.task.ToDo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/** Loads tasks from and saves tasks to a data file. */
public class Storage {
    private final String filePath;

    /** Creates a storage manager that reads from and writes to the specified file. */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Decodes the fields of a todo storage entry. */
    private ToDo parseTodo(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 3) {
            throw new CooperException("Improper ToDo format!");
        }

        boolean isDone = params[1].trim().equals("1");

        return new ToDo(params[2].trim(), isDone);
    }

    /** Decodes the fields of a deadline storage entry. */
    private Deadline parseDeadline(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 4) {
            throw new CooperException("Improper Deadline format!");
        }

        boolean isDone = params[1].trim().equals("1");
        LocalDateTime dueDate = LocalDateTime.parse(params[3].trim());

        return new Deadline(params[2].trim(), isDone, dueDate);
    }

    /** Decodes the fields of an event storage entry. */
    private Event parseEvent(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 5) {
            throw new CooperException("Improper Event format!");
        }

        boolean isDone = params[1].trim().equals("1");
        LocalDateTime startDate = LocalDateTime.parse(params[3].trim());
        LocalDateTime endDate = LocalDateTime.parse(params[4].trim());

        return new Event(params[2].trim(), isDone, startDate, endDate);
    }

    /**
     * Loads all tasks from the data file, creating the file and its parent directories if absent.
     *
     * @return tasks decoded from the data file
     * @throws CooperException if the file cannot be read or created
     */
    public List<Task> loadTasks() {
        List<Task> taskList = new ArrayList<>();
        Path path = Path.of(filePath);

        try {
            Path parentDirectory = path.getParent();

            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            if (Files.notExists(path)) {
                Files.createFile(path);
                return taskList;
            }

            try (Scanner fileReader = new Scanner(path)) {
                while (fileReader.hasNextLine()) {
                    String entry = fileReader.nextLine();
                    taskList.add(decodeTask(entry));
                }
            }
        } catch (IOException e) {
            throw new CooperException("Unable to load task data.");
        }

        return taskList;
    }

    /**
     * Replaces the contents of the data file with the supplied tasks.
     *
     * @param tasks tasks to persist in their current order
     * @throws CooperException if the directory or file cannot be written
     */
    public void saveTasks(List<Task> tasks) {

        Path path = Path.of(filePath);
        Path parent = path.getParent();

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new CooperException("Error in creating new file directory!");
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (Task task : tasks) {
                fileWriter.write(task.toDataString());
                fileWriter.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new CooperException("Error in writing to the file!");
        }
    }

    /**
     * Decodes one storage entry into its corresponding task subtype.
     *
     * @param line pipe-delimited storage entry
     * @return decoded todo, deadline, or event
     * @throws CooperException if the entry structure, status, or task type is invalid
     */
    public Task decodeTask(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length < 3) {
            throw new CooperException("Improper entry format");
        }

        String type = params[0].trim();
        String isDone = params[1].trim();

        if (type.length() != 1 || (!isDone.equals("0") && !isDone.equals("1"))) {
            throw new CooperException("Improper entry format!");
        }

        char c = type.charAt(0);

        switch (c) {
            case 'T': {
                return parseTodo(line);
            }
            case 'D': {
                return parseDeadline(line);
            }
            case 'E': {
                return parseEvent(line);
            }
            default: {
                throw new CooperException("Task type not recognized!");
            }
        }
    }
}
