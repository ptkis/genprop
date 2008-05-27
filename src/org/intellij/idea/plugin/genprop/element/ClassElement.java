package org.intellij.idea.plugin.genprop.element;

import org.intellij.idea.plugin.genprop.util.StringUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

/**
 * Information about the class that contains the fields that are target for
 * the toString() code generation.
 *
 * @author Claus Ibsen
 */
public class ClassElement implements Serializable {

    private String name;
    private String qualifiedName;
    private boolean hasSuper;
    private String superName;
    private String superQualifiedName;
    private String[] implementNames;

    /**
     * Does the class implement the given interface?
     * <p/>
     * The name should <b>not</b> be the qualified name.
     * <br/>The interface name can also be a comma seperated list to test against several interfaces. Will return true if the class implement just one of the interfaces.
     *
     * @param interfaceName  interface name.
     * @return   true if the class implements this interface, false if not.
     */
    public boolean isImplements(String interfaceName) {
        for (int i = 0; i < implementNames.length; i++) {
            String className = implementNames[i];
            if (interfaceName.indexOf(className) != -1)
                return true;
        }

        return false;
    }

    /**
     * Does the class extends any of the given classnames?
     *
     * @param classNames  list of classes seperated by comma.
     * @return  true if this class extends one of the given classnames.
     */
    public boolean isExtends(String classNames) {
        return (classNames.indexOf(superName) != -1);
    }

    /**
     * Performs a regular expression matching the classname (getName()).
     *
     * @param regexp regular expression.
     * @return true if the classname matches the regular expression.
     * @throws PatternSyntaxException   is throw if there is an error performing the matching.
     * @throws IllegalArgumentException is throw if the given input is invalid (an empty String).
     */
    public boolean matchName(String regexp) throws PatternSyntaxException, IllegalArgumentException {
        if (StringUtil.isEmpty(regexp))
            throw new IllegalArgumentException("Can't perform regular expression since the given input is empty. Check your Velocity template: regexp='" + regexp + "'");

        return name.matches(regexp);
    }

    public String[] getImplementNames() {
        return implementNames;
    }

    public void setImplementNames(String[] implementNames) {
        this.implementNames = implementNames;
    }

    public String getSuperQualifiedName() {
        return superQualifiedName;
    }

    public void setSuperQualifiedName(String superQualifiedName) {
        this.superQualifiedName = superQualifiedName;
    }

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String FQClassname) {
        this.qualifiedName = FQClassname;
    }

    public boolean isHasSuper() {
        return hasSuper;
    }

    public void setHasSuper(boolean hasSuper) {
        this.hasSuper = hasSuper;
    }

    public String toString() {
        return "ClassElement{" +
                "name='" + name + "'" +
                ", qualifiedName='" + qualifiedName + "'" +
                ", hasSuper=" + hasSuper +
                ", superName='" + superName + "'" +
                ", superQualifiedName='" + superQualifiedName + "'" +
                ", implementNames=" + (implementNames == null ? null : Arrays.asList(implementNames)) +
                "}";
    }


}
