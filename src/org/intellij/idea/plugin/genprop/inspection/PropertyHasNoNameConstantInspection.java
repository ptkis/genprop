package org.intellij.idea.plugin.genprop.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.GeneratePropertyNameContext;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.util.StringUtil;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Intention to check if the current class overwrites the toString() method. <p/> This inspection will use filter
 * information from the settings to exclude certain fields (eg. constants etc.). <p/> This inspection will only perform
 * inspection if the class have fields to be dumped but does not have a toString method.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public class PropertyHasNoNameConstantInspection
		extends AbstractPropertyInspection {

	private static Logger log = Logger.getLogger(PropertyHasNoNameConstantInspection.class);
	private LocalQuickFix fix = new PropertyHasNoNameConstantQuickFix();

	/**
	 * User options for classes to exclude. Must be a regexp pattern
	 */
	public String excludeClassNames = ".*Exception";  // must be public for JDOMSerialization

	public String getDisplayName() {
		return "Java bean property has no property name constant";
	}

	public String getShortName() {
		return "PropertyHasNoNameConstant";
	}

	public ProblemDescriptor[] checkClass(PsiClass clazz, InspectionManager im, boolean onTheFly) {
		if (log.isDebugEnabled()) {
			log.debug("PropertyHasNoNameConstantInspection.checkClass: clazz=" + clazz + ", onTheFly=" + onTheFly);
		}

		// must be enabled to do check on the fly
		if (onTheFly && !onTheFlyEnabled()) { return null; }

		// must be a class
		if (clazz == null || clazz.getName() == null) { return null; }

		List problems = new ArrayList(1);

		// if it is an excluded class - then skip
		if (StringUtil.isNotEmpty(excludeClassNames) && clazz.getName().matches(excludeClassNames)) {
			log.debug("This class is excluded");
			return null;
		}

		// must have fields
		PsiAdapter psi = GeneratePropertyNameContext.getPsi();
		PsiField[] fields = psi.getFields(clazz);
		if (fields.length == 0) {
			log.debug("Class does not have any fields");
			return null;
		}

		// get list of fields supposed to be dumped in the toString method
		List dumpedFields = filterFields(fields, psi);
		if (dumpedFields.size() == 0) {
			log.debug("No fields to be dumped as all fields was excluded (exclude field by XXX from Settings)");
			return null;
		}

		// okay some fields are supposed to dumped, does a toString method exist
		for (int i = 0; i < dumpedFields.size(); i++) {
			PsiField field = (PsiField) dumpedFields.get(i);

			PsiField constantField = psi.findFieldByName(
					clazz,
					StringUtil.generatePropertyConstantName(field.getName()));

			if (constantField == null) {
				// a property constant field is missing
				if (log.isDebugEnabled()) {
					log.debug("Field " + field.getName() + " has no property name constant.");
				}
				ProblemDescriptor problem = im.createProblemDescriptor(
						clazz,
						"Field has no property name constant",
						fix,
						ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
				problems.add(problem);
			}
		}

		// any problems?
		if (problems.size() > 0) {
			if (log.isDebugEnabled()) { log.debug("Number of problems found: " + problems.size()); }
			return (ProblemDescriptor[]) problems.toArray(new ProblemDescriptor[problems.size()]);
		} else {
			log.debug("No problems found");
			return null; // no problems
		}
	}

	/**
	 * Creates the options panel in the settings for user changeable options.
	 */
	public JComponent createOptionsPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		final String configurationLabel = "Ignore Classes (regexp):";
		final JLabel label = new JLabel(configurationLabel);
		final JTextField excludeClassNamesField = new JTextField(excludeClassNames, 40);
		excludeClassNamesField.setMinimumSize(new Dimension(140, 20));
		final Document document = excludeClassNamesField.getDocument();
		document.addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						textChanged();
					}

					public void insertUpdate(DocumentEvent e) {
						textChanged();
					}

					public void removeUpdate(DocumentEvent e) {
						textChanged();
					}

					private void textChanged() {
						excludeClassNames = excludeClassNamesField.getText();
					}
				});

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		panel.add(label, constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 2.0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.REMAINDER;
		panel.add(excludeClassNamesField, constraints);
		return panel;
	}

}
