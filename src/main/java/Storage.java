import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    private ToDo parseTodo(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 2) {
            throw new CooperException("Improper ToDo format!");
        }

        return new ToDo(params[1].trim());
    }

    private Deadline parseDeadline(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 3) {
            throw new CooperException("Improper ToDo format!");
        }

        return new Deadline(params[1].trim(), params[2].trim());
    }

    private Event parseEvent(String line) {
        String[] params = line.split("\\|", -1);

        if (params.length != 4) {
            throw new CooperException("Improper ToDo format!");
        }

        return new Event(params[1].trim(), params[2].trim(), params[3].trim());
    }

    public List<Task> loadTasks() {
        List<Task> taskList = new ArrayList<>();

        try (Scanner fileReader = new Scanner(Path.of(filePath))) {
            while (fileReader.hasNextLine()) {
                String entry = fileReader.nextLine();
                taskList.add(decodeTask(entry));
            }
        } catch (IOException e) {
            throw new CooperException("Error occurred in opening file!");
        }

        return taskList;
    }

    public void saveTasks(List<Task> tasks) {
        try {
            Files.createDirectories(Path.of("data"));
        } catch (IOException e) {
            throw new CooperException("Error in creating new file directory!");
        }

        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (Task task : tasks) {
                fileWriter.write(task.toDataString());
            }
        } catch (IOException e) {
            throw new CooperException("Error in writing to the file!");
        }
    }

    public Task decodeTask(String line) {
        String[] params = line.split("\\|", -1);

        if (params[0].trim().length() != 1) {
            throw new CooperException("Improper entry format!");
        }

        char c = params[0].charAt(0);

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
