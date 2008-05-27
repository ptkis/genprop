package org.intellij.idea.plugin.genprop.template;

import com.intellij.openapi.application.PathManager;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.Version;
import org.intellij.idea.plugin.genprop.exception.TemplateResourceException;
import org.intellij.idea.plugin.genprop.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource locator for default method body templates and user specific.
 * <p/>
 * Will scan the 'genprop-plugin' folder of the IDEA plugins folder for additional templates.
 *
 * @author Claus Ibsen
 */
public class TemplateResourceLocator {

    private static Logger log = Logger.getLogger(TemplateResourceLocator.class);

    /** Foldername for additional velocity body templates. The folder should be a subfolder in the IDEA/<i>plugins</i> folder. */
    public static final String FOLDER_NAME = "genprop-plugin";

    /** Filename for autosaving active template. */
    public static final String AUTOSAVE_ACTIVE_TEMPLATE_FILE_NAME = "__autosave_active";

    private static final String DEFAULT_CONCAT = "/org/intellij/idea/plugin/genprop/template/DefaultConcat.vm";
    private static final String DEFAULT_BUFFER = "/org/intellij/idea/plugin/genprop/template/DefaultBuffer.vm";
    private static final String DEFAULT_SUPER_CONCAT = "/org/intellij/idea/plugin/genprop/template/DefaultSuperConcat.vm";
    private static final String DEFAULT_SUPER_BUFFER = "/org/intellij/idea/plugin/genprop/template/DefaultSuperBuffer.vm";
    private static final String DEFAULT_REFLECTION = "/org/intellij/idea/plugin/genprop/template/DefaultReflection.vm";
    private static final String DEFAULT_SUPER_REFLECTION = "/org/intellij/idea/plugin/genprop/template/DefaultSuperReflection.vm";
    private static final String ADVANCED_TEMPLATE = "/org/intellij/idea/plugin/genprop/template/AdvancedTemplate.vm";
    private static final String JAVADOC_EXAMPLE = "/org/intellij/idea/plugin/genprop/template/JavaDocExample.vm";
    private static final String DEFAULT_CONCAT_MEMBER = "/org/intellij/idea/plugin/genprop/template/DefaultConcatMember.vm";

    private static boolean templateFolderExists = false;

    /**
     * Only static methods.
     */
    private TemplateResourceLocator() {
    }


    /**
     * Get's the default template if none exists. Likely when this plugin has been installed for the first time.
     */
    public static String getDefaultTemplateBody() {
        return getDefaultTemplates()[0].getTemplate();
    }

    /**
     * Get's the default template name if none exists. Likely when this plugin has been installed for the first time.
     */ 
    public static String getDefaultTemplateName() {
        return "User template";
    }

    /**
     * Get the default templates.
     */
    public static TemplateResource[] getDefaultTemplates() {
        try {
            TemplateResource tr1 = new TemplateResource("Default Concat", FileUtil.readFile(DEFAULT_CONCAT));
            TemplateResource tr2 = new TemplateResource("Default Concat incl. super.toString()", FileUtil.readFile(DEFAULT_SUPER_CONCAT));
            TemplateResource tr3 = new TemplateResource("Default Concat incl. getters", FileUtil.readFile(DEFAULT_CONCAT_MEMBER));
            TemplateResource tr4 = new TemplateResource("Default StringBuffer", FileUtil.readFile(DEFAULT_BUFFER));
            TemplateResource tr5 = new TemplateResource("Default StringBuffer incl. super.toString()", FileUtil.readFile(DEFAULT_SUPER_BUFFER));
            TemplateResource tr6 = new TemplateResource("Default Reflection", FileUtil.readFile(DEFAULT_REFLECTION));
            TemplateResource tr7 = new TemplateResource("Default Reflection incl. super.toString()", FileUtil.readFile(DEFAULT_SUPER_REFLECTION));
            TemplateResource tr8 = new TemplateResource("Advanced Template (for demonstration)", FileUtil.readFile(ADVANCED_TEMPLATE));
            TemplateResource tr9 = new TemplateResource("JavaDoc Template (for demonstration)", FileUtil.readFile(JAVADOC_EXAMPLE));

            return new TemplateResource[]{tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8, tr9};

        } catch (IOException e) {
            throw new TemplateResourceException("Error loading default templates", e);
        }
    }

    /**
     * Get the additional user specific templates from the 'genprop-plugin' subfolder.
     *
     * @return additional templates, null or empty array if none exists.
     */
    public static TemplateResource[] getAdditionalTemplates() {
        String path = getTemplateFolder();

        // check for sub folder exists
        File dir = new File(path);
        if (! dir.exists()) {
            return null;
        }

        // add each file in the folder
        List resources = new ArrayList();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            
            // add file if it is not the autosaved template
            if (! file.getName().startsWith(AUTOSAVE_ACTIVE_TEMPLATE_FILE_NAME)) {
                String body = null;
                try {
                    body = FileUtil.readFile(file);
                } catch (IOException e) {
                    throw new TemplateResourceException("Error loading additional templates", e);
                }
                TemplateResource tr = new TemplateResource(file.getName(), body);
                resources.add(tr);
            }
        }

        return (TemplateResource[]) resources.toArray(new TemplateResource[resources.size()]);
    }

    /**
     * Get all the templates (defaults and additional)
     * @return  all the templates.
     */
    public static TemplateResource[] getAllTemplates() {
        List resources = new ArrayList();

        TemplateResource[] tr1 = getDefaultTemplates();
        for (int i = 0; tr1 != null && i < tr1.length; i++) {
            TemplateResource tr = tr1[i];
            resources.add(tr);
        }

        TemplateResource[] tr2 = getAdditionalTemplates();
        for (int i = 0; tr2 != null && i < tr2.length; i++) {
            TemplateResource tr = tr2[i];
            resources.add(tr);
        }

       return (TemplateResource[]) resources.toArray(new TemplateResource[resources.size()]);
    }

    /**
     * Get's the template folder where additional templates are stored.
     * @return  the absolute foldername.
     */
    public static String getTemplateFolder() {
        return PathManager.getPluginsPath() + File.separatorChar + FOLDER_NAME;
    }

    /**
     * Creates the template folder if missing.
     */
    public static void createTemplateFolderIfMissing() {
        String path = getTemplateFolder();

        // check for template folder exists
        File dir = new File(path);
        if (! dir.exists()) {
            log.info("Creating template folder: " + path);
            if (! dir.mkdirs()) {
                log.fatal("Error creating template folder: " + path);
                throw new RuntimeException("Error creating template folder: " + path);
            }
        }

        templateFolderExists = true; // the folder now exists so we do not need to check for it all the time
    }

    /**
     * Auto saves the active template in the to-string plugin folder (creates this folder if missing)
     * @param content   the content of the template
     * @throws TemplateResourceException  is thrown if error autosaving template.
     */
    public static void autosaveActiveTemplate(String content) throws TemplateResourceException {

        // only backup if user has customized template
        if (content == null)
            return;

        // create template folder if missing
        if (! templateFolderExists)
            createTemplateFolderIfMissing();

        try {
            String filename = getTemplateFolder() + File.separatorChar + AUTOSAVE_ACTIVE_TEMPLATE_FILE_NAME + '_' + Version.VERSION_NO_DOT + ".vm";
            log.debug("Autosaving active template: filename='" + filename + "'");
            FileUtil.saveFile(filename, content);
        } catch (IOException e) {
            throw new TemplateResourceException("Error autosaving active template", e);
        }
    }


}
