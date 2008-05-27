package org.intellij.idea.plugin.genprop.config;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiMember;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.psi.PsiAdapterFactory;

/**
 * This policy is to create a duplicate <code>toString</code> method.
 *
 * @author Igor Levit
 * @author Claus Ibsen
 */
public class DuplicatePolicy implements ConflictResolutionPolicy {

    private static final DuplicatePolicy instance = new DuplicatePolicy();

    private DuplicatePolicy() {
    }

    public static DuplicatePolicy getInstance() {
        return instance;
    }

    public boolean applyField(Editor editor,
			PsiClass clazz,
			PsiField existingField,
			PsiField newField) throws IncorrectOperationException {
        // lazy initialize otherwise IDEA throws error: Component requests are not allowed before they are created
			PsiAdapter psi = PsiAdapterFactory.getPsiAdapter();

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
		insertNewField(clazz, newField);
		return true;
    }

    public boolean applyJavaDoc(PsiClass clazz,
			PsiField newField,
			CodeStyleManager codeStyleManager,
			PsiElementFactory elementFactory,
			String existingJavaDoc,
			String newJavaDoc) throws IncorrectOperationException {
        // lazy initialize otherwise IDEA throws error: Component requests are not allowed before they are created
            PsiAdapter psi = PsiAdapterFactory.getPsiAdapter();

        String text = existingJavaDoc == null ? newJavaDoc : existingJavaDoc; // keep existing

        if (psi.addOrReplaceJavadoc(elementFactory, codeStyleManager, newField, text, false) != null) {
            return true;
        } else
            return false;
    }

	public boolean insertNewField(PsiClass clazz, PsiField newField) throws IncorrectOperationException {
		Project project = clazz.getProject();
		PsiAdapter psi = PsiAdapterFactory.getPsiAdapter();

		PsiJavaFile javaFile = psi.getSelectedJavaFile(project, psi.getPsiManager(project));
		Editor editor = psi.getSelectedEditor(project);

		// find the element the cursor is postion on
		PsiElement cur = psi.findElementAtCursorPosition(javaFile, editor);

		// ID 10, ID 12, ID14: handle caret position can be outside the class braces
		if (beforeLeftBrace(cur, clazz)) {
			clazz.addAfter(newField, clazz.getLBrace());
		} else if (beforeRightBrace(cur, clazz)) {
			// assuming within the clazz, try find better spot to insert, since cur can be anywhere
			PsiElement spot = findBestSpotToInsert(cur);
			if (spot != null) {
				clazz.addAfter(newField, spot);
			} else {
				// default to add it after the current position
				clazz.addAfter(newField, cur);
			}
		} else {
			// caret is at/after the right brace so add it before
			clazz.addBefore(newField, clazz.getRBrace());
		}

		return true;
	}

	private  PsiElement findBestSpotToInsert(PsiElement elem) {
		// we can insert after whitespace, method or a member
		if (elem instanceof PsiWhiteSpace) {
			// parent must not be a method, then we are at whitespace within a method and therefore want to insert after the method
			PsiMethod method = PsiAdapter.findParentMethod(elem);
			return method == null ? elem : method;
		} else if (elem instanceof PsiMethod) {
			// a method is fine
			return elem;
		} else if (elem instanceof PsiMember) {
			// okay only problem is that we can't insert at class position and PsiClass is a subclass for PsiMember
			if (!(elem instanceof PsiClass)) {
				return elem;
			}
		}

		// we reached to far up in the top and can't find a good spot to insert
		if (elem instanceof PsiJavaFile) {
			return null;
		}

		// search up for a good spot
		PsiElement parent = elem.getParent();
		if (parent != null) {
			return findBestSpotToInsert(parent);
		} else {
			return null;
		}
	}

	private  boolean beforeRightBrace(PsiElement elem, PsiClass clazz) {
		if (clazz == null || clazz.getRBrace() == null) {
			return true; // if no brace assume yes
		}

		return elem.getTextOffset() < clazz.getRBrace().getTextOffset();
	}

	private  boolean beforeLeftBrace(PsiElement elem, PsiClass clazz) {
		if (clazz == null || clazz.getLBrace() == null) {
			return true; // if no brace assume yes
		}

		return elem.getTextOffset() < clazz.getLBrace().getTextOffset();
	}


	public String toString() {
        return "Duplicate";
    }

}
