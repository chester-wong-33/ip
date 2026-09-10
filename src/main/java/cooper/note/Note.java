package cooper.note;

import cooper.exception.CooperException;

/** An immutable, single-line snippet independent of task completion state. */
public record Note(String text) {
    public static final String EMPTY_TEXT = "Cooper needs some text for your note!";
    public static final String LINE_BREAK = "Cooper's note commands must fit on one line!";

    /** Rejects blank or multiline text and removes outer whitespace. */
    public Note {
        if (text.matches("(?s).*\\R.*")) {
            throw new CooperException(LINE_BREAK);
        }
        text = text.strip();
        if (text.isEmpty()) {
            throw new CooperException(EMPTY_TEXT);
        }
    }
}
