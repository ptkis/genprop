package org.intellij.idea.plugin.genprop.config;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.editor.Editor;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.psi.PsiAdapterFactory;

/**
 * This policy is to replace the existing <code>toString</code> method.
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
public class ReplacePolicy implements ConflictResolutionPolicy {

    private static final ReplacePolicy instance = new ReplacePolicy();
    private static PsiAdapter psi;

    private ReplacePolicy() {
    }

    public static ReplacePolicy getInstance() {
        return instance;
    }

    public boolean applyField(Editor editor,
			PsiClass clazz,
			PsiField existingField,
			PsiField newField) throws IncorrectOperationException {
        if (existingField != null) {
            existingField.replace(newField);
            return true;
        } else {
            return DuplicatePolicy.getInstance().applyField(editor,
					clazz, existingField, newField);
        }
    }

    public boolean applyJavaDoc(PsiClass clazz,
			PsiField newField,
			CodeStyleManager codeStyleManager,
			PsiElementFactory elementFactory,
			String existingJavaDoc,
			String newJavaDoc) throws IncorrectOperationException {
        // lazy initialize otherwise IDEA throws error: Component requests are not allowed before they are created
        if (psi == null)
            psi = PsiAdapterFactory.getPsiAdapter();

        String text = existingJavaDoc == null ? newJavaDoc : existingJavaDoc; // keep existing

        if (psi.addOrReplaceJavadoc(elementFactory, codeStyleManager, newField, text, false) != null) {
            return true;
        } else
            return false;
    }

    public String toString() {
        return "Replace Existing   "; // add spaces to fix ConfigUI being displayed nicely
    }

}
