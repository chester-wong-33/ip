package cooper.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;

/** Tests the conversion of supported date strings into {@link LocalDateTime} values. */
public class ParserTest {
    private static final String INVALID_DATE_MESSAGE =
            "Invalid date. Use yyyy-MM-dd or dd-MM-yyyy, and HH:mm optionally.";

    @Test
    public void parseDate_yearMonthDayDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0),
                Parser.parseDate("2026-08-28"));
    }

    @Test
    public void parseDate_dayMonthYearDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0),
                Parser.parseDate("28-08-2026"));
    }

    @Test
    public void parseDate_supportedDateTimeFormats_returnsSpecifiedTime() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 9, 5),
                Parser.parseDate("2026-08-28 09:05"));
        assertEquals(LocalDateTime.of(2026, 8, 28, 23, 59),
                Parser.parseDate("28-08-2026 23:59"));
    }

    @Test
    public void parseDate_slashSeparatorsAndSurroundingWhitespace_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30),
                Parser.parseDate("  28/08/2026 14:30  "));
    }

    @Test
    public void parseDate_validLeapDay_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0),
                Parser.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_invalidCalendarDate_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class,
                () -> Parser.parseDate("2025-02-29"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDate_invalidTime_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class,
                () -> Parser.parseDate("2026-08-28 24:00"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDate_unsupportedFormat_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class,
                () -> Parser.parseDate("August 28, 2026"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }
}
