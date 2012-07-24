package org.intellij.idea.plugin.genprop.config;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.psi.PsiAdapterFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This policy is to replace the existing {@code toString} method.
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
@SuppressWarnings("Singleton")
public class ReplacePolicy
        implements ConflictResolutionPolicy {

    private static final ReplacePolicy instance = new ReplacePolicy();
    private AtomicReference<PsiAdapter> psi = new AtomicReference<PsiAdapter>();

    private ReplacePolicy() {
    }

    public static ReplacePolicy getInstance() {
        return instance;
    }

    public boolean applyField(Editor editor,
                              PsiClass clazz,
                              PsiField existingField,
                              PsiField newField)
            throws IncorrectOperationException {
        if (existingField != null) {
            existingField.replace(newField);
            return true;
        } else {
            return DuplicatePolicy.getInstance().applyField(
                    editor,
                    clazz, existingField, newField);
        }
    }

    public boolean applyMethod(Editor editor, PsiClass clazz, PsiMethod existingMethod, PsiMethod newMethod)
            throws IncorrectOperationException {
        if (existingMethod != null) {
            existingMethod.replace(newMethod);
            return true;
        } else {
            return DuplicatePolicy.getInstance().applyMethod(
                    editor,
                    clazz, existingMethod, newMethod);
        }
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public boolean applyJavaDoc(PsiClass clazz,
                                PsiDocCommentOwner newElement,
                                CodeStyleManager codeStyleManager,
                                PsiElementFactory elementFactory,
                                String existingJavaDoc,
                                String newJavaDoc)
            throws IncorrectOperationException {
        // lazy initialize otherwise IDEA throws error: Component requests are not allowed before they are created
        if (psi.get() == null) {
            psi.compareAndSet(null, PsiAdapterFactory.getPsiAdapter());
        }

        String text = existingJavaDoc == null ? newJavaDoc : existingJavaDoc; // keep existing

        return psi.get().addOrReplaceJavadoc(elementFactory, codeStyleManager, newElement, text, false) != null;
    }

    public String toString() {
        return "Replace Existing   "; // add spaces to fix ConfigUI being displayed nicely
    }

}
