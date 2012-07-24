package org.intellij.idea.plugin.genprop.test;

import org.intellij.idea.plugin.genprop.config.Config;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a dummy test bean for testing the toString() plugin.
 */
public class DummyTestBean extends Config implements Serializable {

    public static final String CONST_FIELD = "XXX_XXX";
    private static final String CONST_FIELD_PRIV = "XXX_XXX";
    private transient String tran = "xxx";

    //    private static String myStaticString;
//    private String[] nameStrings = new String[] { "Claus", "Ibsen" };
//    private String otherStrs[];
//    public int[] ipAdr = new int[] { 127, 92 };
//    private List arrList = new ArrayList();
//
//    private Calendar cal = Calendar.getInstance();
    private Date bday = new java.util.Date();
    //
//    public String pubString;
//    private String firstName;
//    private java.sql.Date sqlBirthDay = new java.sql.Date(new java.util.Date().getTime());
//    private List children;
//    public Object someObject;
//    public Object[] moreObjects;
//    public Map cityMap;
//    public Set courses;
//    private byte smallNumber;
//    private float salary;
//    protected String procString;
//    String defaultPackageString;
//    private java.util.Date utilDateTime = new java.util.Date();
    private DummyTestBean singleton = null;
    private String myNewString;


    public String toString() {
        return "DummyTestBean{" +
                "tran='" + tran + "'" +
                ", bday=" + bday +
                ", singleton=" + singleton +
                ", myNewString='" + myNewString + "'" +
                "}";
    }

    public static void main(String[] args) {
        DummyTestBean me = new DummyTestBean();
        me.myNewString = "Bla";
        me.tran = null;
        System.out.println("me.my = " + me.myNewString + "," + me.tran);
    }


}