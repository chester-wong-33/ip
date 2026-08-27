import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.ToDo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/** Converts user input into actions, task numbers, and task objects. */
public class Parser {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT));
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT));

    /** Parses the command word at the start of the input. */
    public static Action parseAction(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new CooperException("Please enter a command!");
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];
        try {
            return Action.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CooperException("Cooper doesn't understand this command: " + commandWord);
        }
    }

    /** Parses a one-based task number from a command containing exactly two words. */
    public static int parseTaskNumber(String input, String syntaxErrorMessage) {
        String[] parameters = input.trim().split(" ");
        if (parameters.length != 2) {
            throw new CooperException(syntaxErrorMessage);
        }

        int taskNumber = wordToNum(parameters[1]);
        if (taskNumber <= 0) {
            throw new CooperException("The index isn't valid!");
        }
        return taskNumber;
    }

    /** Creates a todo task from a todo command. */
    public static ToDo parseTodo(String input) {
        String[] parameters = input.split(" ");
        if (parameters.length == 1) {
            throw new CooperException("Cooper notices that your todo is empty. That's impossible!");
        }
        return new ToDo(input.split(" ", 2)[1]);
    }

    /** Creates a deadline task from a deadline command. */
    public static Deadline parseDeadline(String input) {
        String[] parameters = input.split(" /by ");
        if (parameters.length != 2) {
            throw new CooperException("Cooper feels a task can only have exactly 1 deadline!");
        }
        if (parameters[0].trim().equals("deadline")) {
            throw new CooperException("Cooper can't keep track of tasks with no name!");
        }

        String taskName = parameters[0].split("deadline ")[1];
        return new Deadline(taskName, parseDate(parameters[1]));
    }

    /** Creates an event task from an event command. */
    public static Event parseEvent(String input) {
        String[] parameters = input.split(" /from ");
        if (parameters.length != 2) {
            throw new CooperException("Cooper feels an event must have a title and start date!");
        }
        if (parameters[0].trim().equals("event")) {
            throw new CooperException("Cooper thinks we need a title!");
        }

        String taskName = parameters[0].split("event ")[1];
        String startDate = parameters[1].split(" /to ")[0];
        String endDate = parameters[1].split(" /to ")[1];
        return new Event(taskName, parseDate(startDate), parseDate(endDate));
    }

    /** Parses a supported date or date-time supplied by the user. */
    public static LocalDateTime parseDate(String time) {
        String normalizedTime = time.trim().replace('/', '-');

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalizedTime, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalizedTime, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }

        throw new CooperException("Invalid date. Use yyyy-MM-dd or dd-MM-yyyy, and HH:mm optionally.");
    }

    private static int wordToNum(String numberString) {
        int number = 0;
        int length = numberString.length();
        for (int i = 0; i < length; i++) {
            char currentCharacter = numberString.charAt(i);
            if (currentCharacter - '0' < 0 || currentCharacter - '0' > 9) {
                return -1;
            }
            number += (currentCharacter - '0') * (int) Math.pow(10, length - 1 - i);
        }
        return number;
    }
}
