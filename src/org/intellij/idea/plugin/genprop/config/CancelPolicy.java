package org.intellij.idea.plugin.genprop.config;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;

/**
 * This policy is to cancel.
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
public class CancelPolicy
        implements ConflictResolutionPolicy {

    private static final CancelPolicy instance = new CancelPolicy();

    private CancelPolicy() {
    }

    public static CancelPolicy getInstance() {
        return instance;
    }

    public boolean applyField(Editor editor,
                              PsiClass clazz,
                              PsiField existingField,
                              PsiField newField)
            throws IncorrectOperationException {
        return false; // the user cancels
    }

    public boolean applyMethod(Editor editor, PsiClass clazz, PsiMethod existingMethod, PsiMethod newMethod)
            throws IncorrectOperationException {
        return false;
    }

    public boolean applyJavaDoc(PsiClass clazz,
                                PsiDocCommentOwner newElement,
                                CodeStyleManager codeStyleManager,
                                PsiElementFactory elementFactory,
                                String existingJavaDoc,
                                String newJavaDoc)
            throws IncorrectOperationException {
        return false;  // the user cancels
    }

    public String toString() {
        return "Cancel";
    }


}
