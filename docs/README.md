# Cooper User Guide

Cooper manages tasks and standalone notes through commands in its chat window or command-line interface.

## Notes

Use notes for short snippets such as a waist size or a movie to remember. Notes are separate from tasks:
they have no completion checkbox, dates, title field, tags, attachments, pinning, or archive state.
Existing `list`, `find`, `delete`, `mark`, and `unmark` commands still affect only tasks.

### Commands

| Command | Result |
| --- | --- |
| `note add <text>` | Append a note. |
| `note list` | Show all notes. |
| `note find <keyword>` | Search for a literal phrase, ignoring case. |
| `note edit <number> <text>` | Replace the entire text, keeping its position. |
| `note delete <number>` | Delete immediately, without confirmation or undo. |

Command words ignore case, and spaces or tabs may separate command components.
Notes contain one line. Outer whitespace is removed; internal spacing and case are preserved.
Unicode, emoji, pipes, quotes, backslashes, and command-like text are accepted literally.
Quotes do not group arguments, and `\n` means a backslash followed by `n`, not a newline.
Blank text and actual line breaks are rejected. Duplicates are allowed; there is no explicit length or count cap.
The GUI displays full text with wrapping and no truncation.

Notes are numbered from 1 in creation order. Deleting a note renumbers later notes.
Editing does not change order. Search results show the original collection numbers, not result positions.
Use the number shown to edit or delete a matching note. Numbers must contain only ASCII digits and fit a positive
Java integer (at most 2147483647); leading zeros are accepted. The number must also exist in the current list.

### Example session and exact responses

Starting with no notes, `note list` returns:

```text
No notes yet!
```

Enter `note add Waist size: 32`:

```text
Got it. I've added this note:
1.[N] Waist size: 32
Now you have 1 note in the list.
```

Enter `note add Watch Arrival`:

```text
Got it. I've added this note:
2.[N] Watch Arrival
Now you have 2 notes in the list.
```

Enter `note list`:

```text
Here are your notes:
1.[N] Waist size: 32
2.[N] Watch Arrival
```

Enter `note find ARRIVAL`:

```text
Here are the matching notes:
2.[N] Watch Arrival
```

Search matches a complete literal substring of the text, using locale-independent case conversion.
For example, `note find waist size` matches the first note. Multiple words form one phrase;
there are no wildcards or regular expressions. No matches returns:

```text
No matching notes found!
```

Enter `note edit 2 Watch Spirited Away`:

```text
Got it. I've updated this note:
2.[N] Watch Spirited Away
```

Editing to the same text is also successful. Enter `note delete 1`:

```text
Noted. I've removed this note:
1.[N] Waist size: 32
Now you have 1 note in the list.
```

The remaining note is now number 1. Deletion displays the removed note's former number.
The count uses `note` only for 1, and `notes` for all other counts, including zero.
Response text has no trailing newline; the CLI adds its usual output line terminator.
The GUI also echoes your input in a separate bubble. Note commands never exit Cooper; use `bye` as usual.

Other valid inputs:

```text
  NoTe   AdD   Watch Dune
note add Size: 32 | colour: blue
note add Remember "Arrival"
note add Folder C:\films
note add todo buy milk /by tomorrow
note edit 01 Watch Dune
```

The text after `add` is stored literally, including quotes and task-like words.

### Invalid inputs

Bare `note`, unknown subcommands, and incorrect structure return:

```text
Cooper needs a valid note command:
note add <text>
note list
note find <keyword>
note edit <number> <text>
note delete <number>
```

Examples: `note`, `note archive 1`, `note list extra`, `note delete`, `note delete 1 extra`, and `note edit`.

| Input or condition | Exact response |
| --- | --- |
| `note add`, `note edit 1`, or blank replacement text | `Cooper needs some text for your note!` |
| `note find` or blank keyword | `Cooper needs a keyword to find matching notes!` |
| Actual line break inside a note command | `Cooper's note commands must fit on one line!` |
| `note delete abc`, `note delete -1`, `note delete 0`, `note delete +1`, or integer overflow | `Cooper needs a valid positive note number!` |
| Valid positive number outside the list | `Cooper couldn't find a note with that number :(` |

Validation checks line breaks, structure, required text/keyword and number syntax, Notes availability, then whether
the number exists. Errors do not modify notes. In the CLI, pressing Enter submits a command; it cannot enter a
multiline note. The GUI likewise uses a single-line input field.

### Saving and compatibility

Notes are automatically saved after every successful add, edit, or delete. They survive restarts.
The file is `data/notes.txt` by default, or `notes.txt` beside a custom task file.
A missing file means an empty collection; it is created on the first successful mutation.

Task data stays in its original format and location. Existing task files need no migration, and older Cooper versions
ignore the separate notes file. Importing other note formats and automatic migrations are not supported.
Do not run multiple Cooper instances against the same notes file or edit it externally while Cooper is running.

The notes file is UTF-8 with this version header and one record per note:

```text
COOPER_NOTES_V1
N | Waist size: 32
N | Watch Arrival
```

Writes use LF line endings and a final LF; reads accept LF or CRLF. An empty collection contains only the header.
Record order defines numbering. Each record starts with the exact prefix `N | `.
In the file (not in commands), backslashes are encoded as `\\`, and pipes as `\|`:

```text
COOPER_NOTES_V1
N | Folder C:\\films \| watch Arrival
```

Only those two escapes are valid. Empty records, unknown prefixes, blank text, outer whitespace, actual line breaks
in a note, unknown/incomplete escapes, unescaped pipes, invalid UTF-8, and unsupported/missing headers are rejected.
An existing zero-byte file is malformed, not an empty collection.

### Storage errors and recovery

If saving fails, Cooper returns:

```text
Cooper couldn't save your notes. Your change has not been applied.
```

The prior collection remains active and the previous saved file is retained. Saving writes a sibling temporary file
and atomically replaces the saved file before publishing the change in memory. A filesystem that cannot support
atomic replacement causes a save error; Cooper does not fall back to a partial overwrite.
Temporary files use `notes-*.tmp` names and are cleaned up when possible; they are never loaded as notes.

If loading fails, the startup message includes this paragraph after a blank line:

```text
Cooper couldn't load your notes. Notes are unavailable until you fix the notes file and restart Cooper. Your tasks are still available.
```

Valid note commands then return:

```text
Cooper couldn't load your notes. Fix the notes file and restart Cooper. Your saved notes have not been changed.
```

Task commands remain available. Cooper does not partially load or overwrite the damaged file.
Exit Cooper, keep a backup of the damaged file, correct its format or restore a known-good copy, then restart.
If you intentionally want to start over, move the damaged file elsewhere before restarting.
Repairing the file while Cooper is running does not re-enable Notes until restart.
