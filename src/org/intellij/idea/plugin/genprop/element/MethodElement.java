package org.intellij.idea.plugin.genprop.element;

import org.intellij.idea.plugin.genprop.config.FilterPattern;
import org.intellij.idea.plugin.genprop.config.Filterable;
import org.intellij.idea.plugin.genprop.util.StringUtil;

import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

/**
 * This is a method element containing information about the method.
 *
 * @see ElementFactory
 * @author Claus Ibsen
 * @since 2.15
 */
public class MethodElement extends AbstractElement implements Serializable, Element, Filterable {

    private String methodName;
    private String fieldName;
    private boolean modifierAbstract;
    private boolean modifierSynchronzied;
    private boolean returnTypeVoid;
    private boolean getter;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAccessor() {
        return methodName + "()";
    }

    public boolean isModifierAbstract() {
        return modifierAbstract;
    }

    public void setModifierAbstract(boolean modifierAbstract) {
        this.modifierAbstract = modifierAbstract;
    }

    public boolean isModifierSynchronzied() {
        return modifierSynchronzied;
    }

    public void setModifierSynchronzied(boolean modifierSynchronzied) {
        this.modifierSynchronzied = modifierSynchronzied;
    }

    public boolean isReturnTypeVoid() {
        return returnTypeVoid;
    }

    public void setReturnTypeVoid(boolean returnTypeVoid) {
        this.returnTypeVoid = returnTypeVoid;
    }

    public boolean isGetter() {
        return getter;
    }

    public void setGetter(boolean getter) {
        this.getter = getter;
    }

    /**
     * Performs a regular expression matching the methodname.
     *
     * @param regexp regular expression.
     * @return true if the methodname matches the regular expression.
     * @throws java.util.regex.PatternSyntaxException   is throw if there is an error performing the matching.
     * @throws IllegalArgumentException is throw if the given input is invalid (an empty String).
     */
    public boolean matchName(String regexp) throws PatternSyntaxException, IllegalArgumentException {
        if (StringUtil.isEmpty(regexp))
            throw new IllegalArgumentException("Can't perform regular expression since the given input is empty. Check the Method body velocity code: regexp='" + regexp + "'");

        return methodName.matches(regexp);
    }

    public boolean applyFilter(FilterPattern pattern) {
        if (pattern == null)
            return false;

        if (StringUtil.isNotEmpty(pattern.getMethodName()) && methodName.matches(pattern.getMethodName())) {
            return true;
        }

        return false;
    }

    public String toString() {
        return super.toString() + " ::: MethodElement{" +
                "fieldName='" + fieldName + "'" +
                ", methodName='" + methodName + "'" +
                ", modifierAbstract=" + modifierAbstract +
                ", modifierSynchronzied=" + modifierSynchronzied +
                ", returnTypeVoid=" + returnTypeVoid +
                ", getter=" + getter +
                "}";
    }


}
