package org.intellij.idea.plugin.genprop;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.config.ConflictResolutionPolicy;
import org.intellij.idea.plugin.genprop.config.DuplicatePolicy;
import org.intellij.idea.plugin.genprop.element.FieldElement;
import org.intellij.idea.plugin.genprop.exception.GenerateCodeException;
import org.intellij.idea.plugin.genprop.util.StringUtil;
import org.intellij.idea.plugin.genprop.view.MethodExistsDialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The action-handler that does the code generation.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public class GenerateGetterSetterActionHandler
		extends AbstractGenerateActionHandler {
	private static final Logger log = Logger.getLogger(GenerateGetterSetterActionHandler.class);

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
			if (member instanceof PsiMethod) {
				PsiMethod field = (PsiMethod) member;
				if (getPsi().findMethodByName(clazz, StringUtil.generateSetterName(field.getName())) != null) {
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
	 * @throws org.intellij.idea.plugin.genprop.exception.GenerateCodeException is thrown when there is an error generating
	 * the javacode.
	 */
	@Override
	protected void doCreateFromFields(PsiClass clazz,
			Collection<? extends PsiElement> selectedMembers,
			ConflictResolutionPolicy policy,
			Map params)
			throws IncorrectOperationException, GenerateCodeException {
		// generate code using velocity
		List<FieldElement> fields = getOnlyAsFieldElements(selectedMembers);
		if (log.isDebugEnabled()) {
			log.debug("The fields are " + fields);
		}
		for (FieldElement fieldElement : fields) {
			String propName = StringUtil.generatePropertyConstantName(fieldElement.getName());
			//noinspection UnnecessaryCodeBlock
			{
				String methodName;
				if (fieldElement.isBoolean()) {
					methodName = StringUtil.generateGetterNameBoolean(fieldElement.getName());
				} else {
					methodName = StringUtil.generateGetterNameGeneral(fieldElement.getName());
				}
				String declaration = MessageFormat.format(
						"public {1} {0}() '{'\n" +
								"return {2};\n" +
								"'}'"
						, methodName, fieldElement.getTypePresentableText(), fieldElement.getName());
				if (createMethod(clazz, policy, params, fieldElement, methodName, declaration)) {
					return;
				}
			}
			//noinspection UnnecessaryCodeBlock
			{
				String methodName = StringUtil.generateSetterName(fieldElement.getName());
				String declaration = MessageFormat.format(
						"public void {0}({1} {2}) '{'\n" +
								"Object old = this.{2};\n" +
								"this.{2} = {2};\n" +
								"firePropertyChange({3}, old, {2});\n'}'"
						, methodName, fieldElement.getTypePresentableText(), fieldElement.getName(),
						propName);
				if (createMethod(clazz, policy, params, fieldElement, methodName, declaration)) {
					return;
				}
			}
		}

		// return the created fields

	}

	private boolean createMethod(PsiClass clazz,
			ConflictResolutionPolicy policy,
			Map params,
			FieldElement fieldElement,
			String methodName, String declaration) {

		if (log.isDebugEnabled()) {
			log.debug("Creating method: " + declaration);
		}


		// applyField conflict resolution policy (add/replace, duplicate, cancel)
		PsiMethod existingMethod = getPsi().findMethodByName(clazz, methodName);
		PsiMethod newMethod = getElementFactory().createMethodFromText(declaration, null);
		boolean operationExecuted = policy.applyMethod(
				getEditor(),
				clazz, existingMethod, newMethod);
		if (existingMethod != null && !operationExecuted) {
			//noinspection ReturnOfNull
			return true;
		}

		String existingJavaDoc = (String) params.get("existingJavaDoc");
		String newJavaDoc = "/** Property bound setter for {@code " + fieldElement.getName() + "}. */";
		PsiMethod propertyNameField = getPsi().findMethodByName(
				clazz,
				methodName); // must find again to be able to add javadoc (IDEA does not add if using method parameter)
		policy.applyJavaDoc(
				clazz, propertyNameField,
				getCodeStyleManager(), getElementFactory(), existingJavaDoc, newJavaDoc);

		// reformat code style
		getCodeStyleManager().reformat(newMethod);
		return false;
	}


	@Override
	protected List<PsiElement> preselect(PsiClass clazz, PsiField... filteredFields) {
		List<PsiElement> preselected = new ArrayList<PsiElement>();
		for (PsiField psiField : filteredFields) {
			if (getPsi().findAllMethodByName(clazz, StringUtil.generateSetterName(psiField.getName())) ==
					null) {
				preselected.add(psiField);
			}
		}
		return preselected;
	}
}
