package org.intellij.idea.plugin.genprop.exception;

/**
 * Template resource related exceptions.
 * <p/>
 * Usually error loading, saving template resources.
 *
 * @author Claus Ibsen
 */
public class TemplateResourceException extends PluginException {

    /**
     * Create template exception (error saving template, loading template etc.)
     *
     * @param msg   message description.
     * @param cause the caused exception.
     */
    public TemplateResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
