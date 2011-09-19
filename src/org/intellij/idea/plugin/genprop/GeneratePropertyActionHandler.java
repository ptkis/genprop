package org.intellij.idea.plugin.genprop;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.config.ConflictResolutionPolicy;
import org.intellij.idea.plugin.genprop.config.DuplicatePolicy;
import org.intellij.idea.plugin.genprop.element.FieldElement;
import org.intellij.idea.plugin.genprop.exception.GenerateCodeException;
import org.intellij.idea.plugin.genprop.util.StringUtil;
import org.intellij.idea.plugin.genprop.view.MethodExistsDialog;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The action-handler that does the code generation.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public class GeneratePropertyActionHandler
		extends AbstractGenerateActionHandler {
	private static final Logger log = Logger.getLogger(GeneratePropertyActionHandler.class);

	public GeneratePropertyActionHandler() {
		super();
	}

	/**
	 * This method get's the choise if there is an existing {@code toString} method. <br/> 1) If there is a settings to
	 * always override use this. <br/> 2) Prompt a dialog and let the user decide.
	 *
	 * @param clazz the class.
	 *
	 * @return the policy the user selected (never null)
	 */
	@Override
	protected ConflictResolutionPolicy existsMethodDialog(PsiClass clazz,
			Collection<? extends PsiElement> selectedMembers) {
		boolean exists = false;
		for (PsiElement member : selectedMembers) {
			if (member instanceof PsiField) {
				PsiField field = (PsiField) member;
				if (getPsi().findFieldByName(clazz, StringUtil.generatePropertyConstantName(field.getName())) != null) {
					exists = true;
					break;
				}
			}
		}
		if (exists) {
			ConflictResolutionPolicy def = getConfig().getReplaceDialogInitialOption();
			// is always use default set in config?
			if (getConfig().isUseDefaultAlways()) {
				return def;
			} else {
				// no, so ask user what to do
				return MethodExistsDialog.showDialog(getEditor().getComponent(), def);
			}
		}

		// If there is no conflict, duplicate policy will do the trick
		return DuplicatePolicy.getInstance();
	}

	/**
	 * Creates the property constant fields.
	 *
	 * @param clazz the PsiClass object.
	 * @param selectedMembers the selected members as both {@link com.intellij.psi.PsiField} and {@link
	 * com.intellij.psi.PsiMethod}.
	 * @param policy conflict resolution policy
	 * @param params additional parameters stored with key/value in the map.
	 *
	 * @return the created method, null if the method is not created due the user cancels this operation
	 *
	 * @throws GenerateCodeException is thrown when there is an error generating the javacode.
	 */
	@Override
	@Nullable
	protected void doCreateFromFields(PsiClass clazz,
			Collection<? extends PsiElement> selectedMembers,
			ConflictResolutionPolicy policy,
			Map params)
			throws IncorrectOperationException, GenerateCodeException {
		// generate code using velocity
		List<PsiField> newFields = new LinkedList<PsiField>();
		List<FieldElement> fields = getOnlyAsFieldElements(selectedMembers);
		if (log.isDebugEnabled()) {
			log.debug("The fields are " + fields);
		}
		for (FieldElement fieldElement : fields) {
			String fieldName = StringUtil.generatePropertyConstantName(fieldElement.getName());
			String declaration = "public static final String " +
					fieldName + " = \"" + fieldElement.getName() + "\";";
			if (log.isDebugEnabled()) {
				log.debug("Creating field is: " + declaration);
			}


			// applyField conflict resolution policy (add/replace, duplicate, cancel)
			PsiField existingField = getPsi().findFieldByName(clazz, fieldName);
			PsiField newField = getElementFactory().createFieldFromText(declaration, null);
			boolean operationExecuted = policy.applyField(
					getEditor(),
					clazz, existingField, newField);
			if (existingField != null && !operationExecuted) {
				//noinspection ReturnOfNull
				return; // user cancelled so return null
			}

			String existingJavaDoc = (String) params.get("existingJavaDoc");
			String newJavaDoc = "/** Property name constant for {@code " + fieldElement.getName() + "}. */";
			PsiField propertyNameField = getPsi().findFieldByName(
					clazz,
					fieldName); // must find again to be able to add javadoc (IDEA does not add if using method parameter)
			policy.applyJavaDoc(
					clazz, propertyNameField,
					getCodeStyleManager(), getElementFactory(), existingJavaDoc, newJavaDoc);

			// reformat code style
			getCodeStyleManager().reformat(newField);
			newFields.add(newField);
		}
	}

	protected List<PsiElement> preselect(PsiClass clazz, PsiField[] filteredFields) {
		List<PsiElement> preselected = new ArrayList<PsiElement>();
		for (PsiField psiField : filteredFields) {
			if (getPsi().findAllFieldByName(clazz, StringUtil.generatePropertyConstantName(psiField.getName())) ==
					null) {
				preselected.add(psiField);
			}
		}
		return preselected;
	}
}
