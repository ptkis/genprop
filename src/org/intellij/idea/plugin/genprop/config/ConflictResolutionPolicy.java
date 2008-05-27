package org.intellij.idea.plugin.genprop.config;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.editor.Editor;

/**
 * Interface that defines a policy for dealing with conflicts (i.e., the user is
 * trying to genprop a {@link Object#toString} method but one already exists in
 * this class).
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
public interface ConflictResolutionPolicy {

    /**
     * Applies the choosen policy.
     *
     * @param editor
	 *@param clazz          PSIClass.
	 * @param existingField existing method if one exists.
	 * @param newField      new method. @return if the policy was executed normally (not cancelled)
     * @throws IncorrectOperationException is thrown if there is an IDEA error.
     */
    boolean applyField(Editor editor,
			PsiClass clazz,
			PsiField existingField,
			PsiField newField) throws IncorrectOperationException;

    /**
     * Applies the choose policy for javadoc.
     *
     * @param clazz              PSIClass
     * @param newField          New toString method
     * @param codeStyleManager
	 *@param elementFactory     Element factory
	 * @param existingJavaDoc    Existing javadoc if any
	 * @param newJavaDoc         The new javadoc if any @return                   true if javadoc replace, false if left as it was before
     * @throws IncorrectOperationException is thrown if there is an IDEA error.
     * @since 2.20
     */
    boolean applyJavaDoc(PsiClass clazz,
			PsiField newField,
			CodeStyleManager codeStyleManager,
			PsiElementFactory elementFactory,
			String existingJavaDoc,
			String newJavaDoc) throws IncorrectOperationException;

}
