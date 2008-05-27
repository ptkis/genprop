package org.intellij.idea.plugin.genprop.exception;

/**
 * Base plugin exception.
 *
 * @author Claus Ibsen
 */
public class PluginException extends RuntimeException {

    private String message;

    /**
     * Create exception.
     *
     * @param msg    message description.
     * @param cause  the caused exception.
     */
    public PluginException(String msg, Throwable cause) {
        super(cause);
        this.message = (msg != null ? msg + "\nCaused by: " + cause.getMessage() : cause.getMessage());
    }

    /**
     * Create exception.
     *
     * @param cause  the caused exception.
     */
    public PluginException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    /**
     * Get's the caused message.
     * @return  the caused message.
     */
    public String getMessage() {
        return message;
    }

}
