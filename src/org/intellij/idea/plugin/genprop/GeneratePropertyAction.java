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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The IDEA action for this plugin. <p/> This action handles the generation of getter, setter and property name
 * constants of the class.
 *
 * @author Claus Ibsen
 */
public class GeneratePropertyAction
		extends EditorAction {

	private static final Logger log = Logger.getLogger(GeneratePropertyAction.class);
	private static final AtomicBoolean menusAlreadyDisabled = new AtomicBoolean(false); // has the menus already been disabled?

	/**
	 * Constructor.
	 */
	public GeneratePropertyAction() {
		super(new GeneratePropertyActionHandler()); // register our action handler

		disableActionInMenus(); // menus are always enabled when starting IDEA - disable if user selected in settings
	}

	/**
	 * Remove the menus for the Generate toString() action if the user has selected to. After removing the menus the
	 * boolean flag is set to true, to prevent removing the menus all the time the action is invoked.
	 */
	public static void disableActionInMenus() {
		if (!menusAlreadyDisabled.get() && GeneratePropertyNameContext.getConfig().isDisableActionInMenus()) {
			log.debug("Disabling action from code and editor pop up menus");
			GeneratePropertyNameContext.getPsi()
					.removeActionFromMenu("Actions.ActionsPlugin.GeneratePropertyGroup", "EditorPopupMenu");
			GeneratePropertyNameContext.getPsi()
					.removeActionFromMenu("Actions.ActionsPlugin.GeneratePropertyGroup", "CodeMenu");
			menusAlreadyDisabled.set(true);
		}
	}

	/**
	 * Add the menus for the Generate toString() action if the user has selected to. After add the menus the boolean flag
	 * is set to false, to prevent adding the menus all the time the action is invoked.
	 */
	public static void enableActionsInMenus() {
		if (menusAlreadyDisabled.get() && !GeneratePropertyNameContext.getConfig().isDisableActionInMenus()) {
			log.debug("Adding action to code and editor pop up menus");
			GeneratePropertyNameContext.getPsi()
					.addActionToMenu(
							"Actions.ActionsPlugin.GeneratePropertyGroup",
							"EditorPopupMenu",
							"Generate",
							true);
			GeneratePropertyNameContext.getPsi()
					.addActionToMenu("Actions.ActionsPlugin.GeneratePropertyGroup", "CodeMenu", "Generate", true);
			menusAlreadyDisabled.set(false);
		}
	}

	/**
	 * Updates the presentation of this action. Will disable this action for non-java files.
	 *
	 * @param editor IDEA editor.
	 * @param presentation Presentation.
	 * @param dataContext data context.
	 */
	@Override
	public void update(Editor editor, Presentation presentation, DataContext dataContext) {
		Project project = editor.getProject();
		PsiAdapter psi = GeneratePropertyNameContext.getPsi();
		PsiManager manager = psi.getPsiManager(project);
		PsiJavaFile javaFile = psi.getSelectedJavaFile(project, manager);
		presentation.setEnabled(javaFile != null && psi.getCurrentClass(javaFile, editor) != null);
	}


}
