package org.intellij.idea.plugin.genprop.config;

/**
 * This is a filtering pattern, used to filter unwanted fields for this action.
 */
public class FilterPattern {

    private String fieldName;
    private String methodName;
    private boolean constantField;
    private boolean staticModifier;
    private boolean transientModifier;

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set's a filtering using regular expression on the field name.
     *
     * @param regexp the regular expression.
     */
    public void setFieldName(String regexp) {
        this.fieldName = regexp;
    }

    public boolean isConstantField() {
        return constantField;
    }

    /**
     * Set this to true to filter by constant fields.
     *
     * @param constantField if true constant fields is unwanted.
     */
    public void setConstantField(boolean constantField) {
        this.constantField = constantField;
    }

    public boolean isTransientModifier() {
        return transientModifier;
    }

    /**
     * Set this to true to filter by transient modifier.
     *
     * @param transientModifier if true fields with transient modifier is unwanted.
     */
    public void setTransientModifier(boolean transientModifier) {
        this.transientModifier = transientModifier;
    }

    public boolean isStaticModifier() {
        return staticModifier;
    }

    /**
     * Set this to true to filter by static modifier.
     *
     * @param staticModifier if true fields with static modifier is unwanted.
     */
    public void setStaticModifier(boolean staticModifier) {
        this.staticModifier = staticModifier;
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * Set's a filtering using regular expression on the method name.
     *
     * @param regexp the regular expression.
     */
    public void setMethodName(String regexp) {
        this.methodName = regexp;
    }

    public String toString() {
        return "FilterPattern{" +
                "fieldName='" + fieldName + "'" +
                ", methodName='" + methodName + "'" +
                ", constantField=" + constantField +
                ", staticModifier=" + staticModifier +
                ", transientModifier=" + transientModifier +
                "}";
    }


}
