package org.intellij.idea.plugin.genprop.test;

/**
 * This is a dummy test bean for testing the toString() plugin.
 */
public abstract class DummyModelTestBean {

    private String age;

    private static class MyInnerClass {

        private String title;

        public boolean isMyMethod() { return true; }

        public String toString() {
            return "MyInnerClass{" +
                    "title='" + title + "'" +
                    ", myMethod=" + isMyMethod() +
                    "}";
        }

    }

    public String getName() {
        return "Claus";
    }

    public String getAge() {
        return age;
    }

    public boolean isOld() {
        return true;
    }

    public abstract String getConfiguration();

    public static String getCache() {
        return null;
    }

    public void nonGetterMethod() {
    }

    public boolean isMyMethod() { return true; }

    public String toString() {
        return "DummyModelTestBean{" +
                "age='" + age + "'" +
                ", name='" + getName() + "'" +
                ", old=" + isOld() +
                ", myMethod=" + isMyMethod() +
                "}";
    }


}
