package org.intellij.idea.plugin.genprop.config;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.psi.PsiAdapterFactory;
import org.jetbrains.annotations.Nullable;

/**
 * This policy is to create a duplicate {@code toString} method.
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
@SuppressWarnings("Singleton")
public class DuplicatePolicy
        implements ConflictResolutionPolicy {

    private static final DuplicatePolicy instance = new DuplicatePolicy();

    private DuplicatePolicy() {
    }

    public static DuplicatePolicy getInstance() {
        return instance;
    }

    public boolean applyField(Editor editor,
                              PsiClass clazz,
                              PsiField existingField,
                              PsiField newField)
            throws IncorrectOperationException {
//		PsiJavaFile javaFile = psi.getSelectedJavaFile(clazz.getProject(), clazz.getManager());
//		PsiElement element = javaFile.findElementAt(editor.getCaretModel().getOffset());
//		while (element != null && element.getParent() != clazz) {
//			element = element.getParent();
//		}
//
//		if (element != null && element.getParent() == clazz) {
//			clazz.addBefore(newField,element);
//		} else {
//			clazz.add(newField);
//		}
        insertNewElement(clazz, newField);
        return true;
    }

    public boolean applyMethod(Editor editor, PsiClass clazz, PsiMethod existingMethod, PsiMethod newMethod)
            throws IncorrectOperationException {
        insertNewElement(clazz, newMethod);
        return true;
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
        PsiAdapter psi = PsiAdapterFactory.getPsiAdapter();

        String text = existingJavaDoc == null ? newJavaDoc : existingJavaDoc; // keep existing

        return psi.addOrReplaceJavadoc(elementFactory, codeStyleManager, newElement, text, false) != null;
    }

    protected boolean insertNewElement(PsiClass clazz, PsiElement newElement)
            throws IncorrectOperationException {
        Project project = clazz.getProject();
        PsiAdapter psi = PsiAdapterFactory.getPsiAdapter();

        PsiJavaFile javaFile = psi.getSelectedJavaFile(project, psi.getPsiManager(project));
        Editor editor = psi.getSelectedEditor(project);

        // find the element the cursor is postion on
        PsiElement cur = psi.findElementAtCursorPosition(javaFile, editor);

        // ID 10, ID 12, ID14: handle caret position can be outside the class braces
        if (beforeLeftBrace(cur, clazz)) {
            clazz.addAfter(newElement, clazz.getLBrace());
        } else if (beforeRightBrace(cur, clazz)) {
            // assuming within the clazz, try find better spot to insert, since cur can be anywhere
            PsiElement spot = findBestSpotToInsert(cur);
            if (spot != null) {
                clazz.addAfter(newElement, spot);
            } else {
                // default to add it after the current position
                clazz.addAfter(newElement, cur);
            }
        } else {
            // caret is at/after the right brace so add it before
            clazz.addBefore(newElement, clazz.getRBrace());
        }

        return true;
    }

    @Nullable
    private PsiElement findBestSpotToInsert(PsiElement elem) {
        PsiElement elem1 = elem;
        while (true) {
            // we can insert after whitespace, method or a member
            if (elem1 instanceof PsiWhiteSpace) {
                // parent must not be a method, then we are at whitespace within a method and therefore want to insert after the method
                PsiMethod method = PsiAdapter.findParentMethod(elem1);
                return method == null ? elem1 : method;
            } else if (elem1 instanceof PsiMethod) {
                // a method is fine
                return elem1;
            } else if (elem1 instanceof PsiMember) {
                // okay only problem is that we can't insert at class position and PsiClass is a subclass for PsiMember
                if (!(elem1 instanceof PsiClass)) {
                    return elem1;
                }
            }

            // we reached to far up in the top and can't find a good spot to insert
            if (elem1 instanceof PsiJavaFile) {
                return null;
            }

            // search up for a good spot
            PsiElement parent = elem1.getParent();
            if (parent != null) {
                elem1 = parent;
            } else {
                return null;
            }
        }
    }

    private boolean beforeRightBrace(PsiElement elem, PsiClass clazz) {
        if (clazz == null) {
            return true;
        }
        PsiElement brace = clazz.getRBrace();
        if (brace == null) {
            return true; // if no brace assume yes
        }

        return elem.getTextOffset() < brace.getTextOffset();
    }

    private boolean beforeLeftBrace(PsiElement elem, PsiClass clazz) {
        if (clazz == null) {
            return true; // if no brace assume yes
        }
        PsiElement brace = clazz.getLBrace();
        if (brace == null) {
            return true; // if no brace assume yes
        }

        return elem.getTextOffset() < brace.getTextOffset();
    }


    public String toString() {
        return "Duplicate";
    }

}
