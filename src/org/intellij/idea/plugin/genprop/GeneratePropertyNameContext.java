package org.intellij.idea.plugin.genprop;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.config.Config;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.psi.PsiAdapterFactory;

/**
 * Application context for this plugin.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public class GeneratePropertyNameContext {

    private static Logger log = Logger.getLogger(GeneratePropertyNameContext.class);
    private static Config config;
    private static PsiAdapter psi;
    private static Project project;
    private static PsiManager manager;
    private static PsiElementFactory elementFactory;

    static {
        psi = PsiAdapterFactory.getPsiAdapter();
    }

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
        }

        return config;
    }

    public static void setConfig(Config newConfig) {
        config = newConfig;
    }

    public static PsiAdapter getPsi() {
        return psi;
    }

    public static Project getProject() {
        if (project == null) {
            log.info("Getting first opened project - assuming it is current project");
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            project = projects[0];
        }
        return project;
    }
                          
    public static void setProject(Project newProject) {
        project = newProject;
    }

    public static PsiManager getManager() {
        if (manager == null) {
            manager = psi.getPsiManager(getProject());
        }
        return manager;
    }

    public static void setManager(PsiManager newManager) {
        manager = newManager;
    }

    public static PsiElementFactory getElementFactory() {
        if (elementFactory == null) {
            elementFactory = psi.getPsiElemetFactory(getManager());
        }
        return elementFactory;
    }

    public static void setElementFactory(PsiElementFactory newElementFactory) {
        elementFactory = newElementFactory;
    }


}
