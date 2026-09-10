# Notes test plan

Run builds and tests with Java 25. Automated tests use temporary directories and must not touch personal data.

## Automated verification

Run `./gradlew test checkstyleMain checkstyleTest` (Windows: `.\gradlew.bat test checkstyleMain checkstyleTest`).

| Area | Coverage |
| --- | --- |
| `NoteParserTest` | All subcommands, case and tabs, preserved internal whitespace, literal Unicode/escapes, missing arguments, usage errors, signs/overflow/non-ASCII numbers, line breaks, validation precedence. |
| `NoteListTest` | Immutable candidate edits, append and duplicates, renumbering on deletion, bounds, original search numbers, literal phrase matching, locale-independent case conversion. |
| `NoteStorageTest` | Exact UTF-8 escaping and LF output, CRLF input, missing file, empty saved collection, malformed headers/records/escapes/text/UTF-8, atomic-move failure preservation and temporary-file cleanup. |
| `CooperNotesTest` | Exact response strings, full lifecycle, identical edit, restart, zero/singular/plural counts, invalid-command nonmutation, task isolation and legacy records, corrupted-file lockout/recovery, failed add/edit/delete rollback. |
| `CooperSmokeTest` | CLI add/list/find/edit/delete/restart flow and existing task find flows. |
| Existing parser, task-list and task-storage tests | Regression coverage for unchanged task behavior. |

All note responses must leave `shouldExit` false. Existing `bye` must still set it true.
Compare response strings exactly, including headings, punctuation, original search numbers, and no trailing newline.

## Manual GUI acceptance

Run the GUI with Java 25 from a disposable working directory with empty `data/`.
Use the exact example responses in [the Notes guide](../docs/README.md#notes).

1. Enter `note list`: expect `No notes yet!` and an echoed input bubble.
2. Add `Waist size: 32` and `Watch Arrival`: expect numbers 1 and 2 and correct singular/plural counts.
3. Enter `note find ARRIVAL`: expect only `2.[N] Watch Arrival`.
4. Enter `note edit 2 Watch Spirited Away`, then `note delete 1`: expect the documented acknowledgements.
5. Enter `note list`: expect `1.[N] Watch Spirited Away`, without a checkbox.
6. Add a long single-line note containing Unicode/emoji, quotes, a pipe, a backslash, and repeated spaces.
   Verify complete readable wrapping without truncation, and scroll to its end.
7. Try `note`, `note list extra`, `note add`, `note find`, `note delete 0`, and an out-of-range number.
   Verify exact documented errors and that `note list` is unchanged.
8. Add, mark, find, unmark, and delete a task. Verify it never becomes a note or changes note data.
9. Enter `bye`: verify the reply, disabled input, and delayed exit. Restart and verify all notes survived.

## Manual recovery checks

Use only disposable data; exit Cooper before changing files.

1. Put malformed data into `data/notes.txt`, then start Cooper. Verify the exact startup warning.
2. Verify valid note operations return the unavailable message, while invalid syntax still returns syntax errors.
3. Add a task and exit. Verify the malformed notes file is unchanged.
4. Restore a valid versioned notes file and restart. Verify Notes works again.
5. On a filesystem/location where note replacement is denied, attempt a mutation. Expect the exact save-failure
   response, unchanged active notes, and unchanged previous file. Automated injected-failure tests cover this
   deterministically without relying on local permission settings.

Manual GUI and platform permission checks require a human run; passing JUnit tests does not certify them.
