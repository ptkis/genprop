package org.intellij.idea.plugin.genprop.test;

/**
 * This is a dummy test bean for testing the toString() plugin.
 */
public class DummyGetterTestBean {

    public int getX() { return 0; }

    /**
     * Insert your javadoc comments here - okay
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "DummyGetterTestBean{" +
                "x=" + getX() +
                "}";
    }

}
