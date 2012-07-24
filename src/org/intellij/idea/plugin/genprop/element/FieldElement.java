package org.intellij.idea.plugin.genprop.element;

import org.intellij.idea.plugin.genprop.config.FilterPattern;
import org.intellij.idea.plugin.genprop.config.Filterable;
import org.intellij.idea.plugin.genprop.util.StringUtil;

import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

/**
 * This is a field element containing information about the field.
 *
 * @author Claus Ibsen
 * @see ElementFactory
 */
public class FieldElement
        extends AbstractElement
        implements Serializable, Element, Filterable {

    private boolean isConstant;

    private boolean isModifierTransient;
    private boolean isModifierVolatile;

    public String getAccessor() {
        return getName();
    }

    /**
     * Is the element a constant type?
     *
     * @return true if this element is a constant type.
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Does the field have a transient modifier?
     *
     * @return true if the field has a transient modifier.
     */
    public boolean isModifierTransient() {
        return isModifierTransient;
    }

    /**
     * Does the field have a volatile modifier?
     *
     * @return true if the field has a volatile modifier.
     */
    public boolean isModifierVolatile() {
        return isModifierVolatile;
    }

    void setConstant(boolean constant) {
        isConstant = constant;
    }

    void setModifierTransient(boolean modifierTransient) {
        isModifierTransient = modifierTransient;
    }

    void setModifierVolatile(boolean modifierVolatile) {
        this.isModifierVolatile = modifierVolatile;
    }

    /**
     * Performs a regular expression matching the fieldname.
     *
     * @param regexp regular expression.
     * @return true if the fieldname matches the regular expression.
     * @throws PatternSyntaxException   is throw if there is an error performing the matching.
     * @throws IllegalArgumentException is throw if the given input is invalid (an empty String).
     */
    public boolean matchName(String regexp)
            throws PatternSyntaxException, IllegalArgumentException {
        if (StringUtil.isEmpty(regexp)) {
            throw new IllegalArgumentException(
                    "Can't perform regular expression since the given input is empty. Check the Method body velocity code: regexp='" +
                            regexp + "'");
        }

        return getName().matches(regexp);
    }

    public boolean applyFilter(FilterPattern pattern) {
        if (pattern == null) {
            return false;
        }

        if (pattern.isConstantField() && isConstant) {
            return true;
        }

        if (pattern.isStaticModifier() && isModifierStatic()) {
            return true;
        }

        if (pattern.isTransientModifier() && isModifierTransient) {
            return true;
        }

        if (StringUtil.isNotEmpty(pattern.getFieldName()) && getName().matches(pattern.getFieldName())) {
            return true;
        }

        return false;
    }

    public String toString() {
        return super.toString() + " ::: FieldElement{" +
                "isConstant=" + isConstant +
                ", isModifierTransient=" + isModifierTransient +
                ", isModifierVolatile=" + isModifierVolatile +
                "}";
    }


}
