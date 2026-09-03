package cooper;

/**
 * Contains Cooper's response to a command and whether the application should exit.
 *
 * @param message response to display
 * @param shouldExit whether the application should close after displaying it
 */
public record CommandResult(String message, boolean shouldExit) {
}
