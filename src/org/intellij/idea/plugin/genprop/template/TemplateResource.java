package org.intellij.idea.plugin.genprop.template;

import java.io.Serializable;

/**
 * A template.
 * <p/>
 * A template contains the methody body and the filename of the resource where
 * the text is stored.
 *
 * @author Claus Ibsen
 */
public class TemplateResource implements Serializable {

    private String fileName;
    private String template;

    /**
     * Constructor.
     *
     * @param fileName a template filename
     * @param template the template velocity body content
     */
    public TemplateResource(String fileName, String template) {
        this.fileName = fileName;
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Important to return filename only as it is the displayname in the UI.
     *
     * @return filename for UI.
     */
    public String toString() {
        return fileName != null ? fileName : template;
    }


}
