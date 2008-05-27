package org.intellij.idea.plugin.genprop.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.intellij.idea.plugin.genprop.GeneratePropertyNameActionHandler;
import org.intellij.idea.plugin.genprop.GeneratePropertyNameContext;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;

/**
 * Quick fix to run Generate toString() to fix any code inspection problems.
 *
 * @author Claus Ibsen
 * @since 2.20
 */
public class PropertyHasNoNameConstantQuickFix implements LocalQuickFix {

    private PsiAdapter psi;

    public PropertyHasNoNameConstantQuickFix() {
        psi = GeneratePropertyNameContext.getPsi();
    }

    public String getName() {
        return "Generate property name constants";
    }

    public void applyFix(Project project, ProblemDescriptor desc) {
        PsiClass clazz = psi.findClass(desc.getPsiElement());

        GeneratePropertyNameActionHandler handler = new GeneratePropertyNameActionHandler();
        handler.executeAction(project, clazz);
    }

	//to appear in "Apply Fix" statement when multiple Quick Fixes exist
	public String getFamilyName() {
		return "Java Bean";
	}

}
