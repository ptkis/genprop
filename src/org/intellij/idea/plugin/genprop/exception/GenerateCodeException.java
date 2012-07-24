package org.intellij.idea.plugin.genprop.exception;

/**
 * Error generating the javacode for the <code>toString</code> method.
 * <p/>
 * This exception is usually caused by a Velocity parsing exception.
 *
 * @author Claus Ibsen
 */
public class GenerateCodeException extends PluginException {

    /**
     * Error generating the java code.
     *
     * @param cause the caused exception.
     */
    public GenerateCodeException(Throwable cause) {
        super(cause);
    }

    /**
     * Error generating the java code.
     *
     * @param msg   message description.
     * @param cause the caused exception.
     */
    public GenerateCodeException(String msg, Throwable cause) {
        super(msg, cause);
    }

}