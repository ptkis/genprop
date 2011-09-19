package org.intellij.idea.plugin.genprop;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;

/**
 * The IDEA action for this plugin. <p/> This action handles the generation of getter, setter and property name
 * constants of the class.
 *
 * @author Claus Ibsen
 */
public class GenerateGetterSetterAction
		extends EditorAction {
	/**
	 * Constructor.
	 */
	public GenerateGetterSetterAction() {
		super(new GenerateGetterSetterActionHandler()); // register our action handler
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
