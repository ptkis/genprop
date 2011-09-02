package org.intellij.idea.plugin.genprop.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.psi.PsiField;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.GeneratePropertyNameContext;
import org.intellij.idea.plugin.genprop.element.ElementFactory;
import org.intellij.idea.plugin.genprop.element.FieldElement;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for GenerateProperty Inspection support.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public abstract class AbstractPropertyInspection
		extends LocalInspectionTool {

	private static Logger log = Logger.getLogger(AbstractPropertyInspection.class);

	public String getGroupDisplayName() {
		return "JavaBean Issues";
	}

	/**
	 * Is the on the fly code inspection enabled?
	 *
	 * @return true if enabled, false if not.
	 */
	protected boolean onTheFlyEnabled() {
		return GeneratePropertyNameContext.getConfig().isInspectionOnTheFly();
	}

	/**
	 * Filters all the fields using the exluded fields filter from settings.
	 *
	 * @param fields all the fields.
	 * @param psi psi adapter.
	 *
	 * @return a list with all the fields <b>not</b> exluded by the filter.
	 */
	protected List filterFields(PsiField[] fields, PsiAdapter psi) {
		List filtered = new ArrayList();

		for (int i = 0; i < fields.length; i++) {
			PsiField field = fields[i];
			if (log.isDebugEnabled()) { log.debug("Field being filtered: " + field + " of type: " + field.getType()); }

			FieldElement fe = ElementFactory.newFieldElement(
					field,
					GeneratePropertyNameContext.getElementFactory(),
					psi);
			if (log.isDebugEnabled()) { log.debug(fe); }

			if (!fe.applyFilter(GeneratePropertyNameContext.getConfig().getFilterPattern())) {
				if (log.isDebugEnabled()) { log.debug("Field is NOT excluded: " + fe.getName()); }
				filtered.add(fe);
			}
		}

		return filtered;
	}


}
