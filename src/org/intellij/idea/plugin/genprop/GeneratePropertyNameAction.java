package org.intellij.idea.plugin.genprop;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;

/**
 * The IDEA action for this plugin.
 * <p/>
 * This action handles the generation of a <code>toString()</code> method that dumps the fields
 * of the class.
 * 
 * @author Claus Ibsen
 */
public class GeneratePropertyNameAction extends EditorAction {

    private static Logger log = Logger.getLogger(GeneratePropertyNameAction.class);
    private static boolean menusAlreadyDisabled = false; // has the menus already been disabled?

    /**
     * Constructor.
     */
    public GeneratePropertyNameAction() {
        super(new GeneratePropertyNameActionHandler()); // register our action handler

        disableActionInMenus(); // menus are always enabled when starting IDEA - disable if user selected in settings
    }

    /**
     * Remove the menus for the Generate toString() action if the user has selected to.
     * After removing the menus the boolean flag is set to true, to prevent removing the
     * menus all the time the action is invoked.
     */
    public static void disableActionInMenus() {
        if (menusAlreadyDisabled == false && GeneratePropertyNameContext.getConfig().isDisableActionInMenus()) {
            log.debug("Disabling action from code and editor popup menus");
            GeneratePropertyNameContext.getPsi().removeActionFromMenu("Actions.ActionsPlugin.GenerateToStringGroup", "EditorPopupMenu");
            GeneratePropertyNameContext.getPsi().removeActionFromMenu("Actions.ActionsPlugin.GenerateToStringGroup", "CodeMenu");
            menusAlreadyDisabled = true;
        }
    }

    /**
     * Add the menus for the Generate toString() action if the user has selected to.
     * After add the menus the boolean flag is set to false, to prevent adding the
     * menus all the time the action is invoked.
     */
    public static void enableActionsInMenus() {
        if (menusAlreadyDisabled == true && GeneratePropertyNameContext.getConfig().isDisableActionInMenus() == false) {
            log.debug("Adding action to code and editor popup menus");
            GeneratePropertyNameContext.getPsi().addActionToMenu("Actions.ActionsPlugin.GenerateToStringGroup", "EditorPopupMenu", "Generate", true);
            GeneratePropertyNameContext.getPsi().addActionToMenu("Actions.ActionsPlugin.GenerateToStringGroup", "CodeMenu", "Generate", true);
            menusAlreadyDisabled = false;
        }
    }

    /**
     * Updates the presentation of this action. Will disable this action for non-java files.
     *
     * @param editor       IDEA editor.
     * @param presentation Presentation.
     * @param dataContext  data context.
     */
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
		Project project = editor.getProject();
		PsiAdapter psi = GeneratePropertyNameContext.getPsi();
		PsiManager manager = psi.getPsiManager(project);
		PsiJavaFile javaFile = psi.getSelectedJavaFile(project, manager);
        presentation.setEnabled(javaFile != null && psi.getCurrentClass(javaFile, editor) != null);
    }


}