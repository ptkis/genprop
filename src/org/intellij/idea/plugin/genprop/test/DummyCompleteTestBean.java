package org.intellij.idea.plugin.genprop.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * This is a dummy test bean for testing the toString() plugin. Has a long list of fields.
 */
public class DummyCompleteTestBean implements Serializable {

    // Constants
    private static final String CONSTANT_PRIVATE = "CONSTANT_PRIVATE";
    public static final String CONSTANT_PUBLIC = "CONSTANT_PUBLIC";
    private static final Object LOCK = new Object();

    // Singleton
    private static DummyCompleteTestBean singleton;

    // Transient
    private transient Object doNotStreamMe;

    // Primitives
    private byte _byte;
    private boolean _boolean;
    private char _char;
    private double _double;
    private float _float;
    private int _int;
    private long _long;
    private short _short;
    private byte[] _byteArr;
    private boolean[] _booleanArr;
    private char[] _charArr;
    private double[] _doubleArr;
    private float[] _floatArr;
    private int[] _intArr;
    private long[] _longArr;
    private short[] _shortArr;

    // Primitive Objects
    private Byte _byteObj;
    private Boolean _booleanObj;
    private Character _charObj;
    private Double _doubleObj;
    private Float _floatObj;
    private Integer _intObj;
    private Long _longObj;
    private Short _shortObj;
    private Byte[] _byteObjArr;
    private Boolean[] _booleanObjArr;
    private Character[] _charObjArr;
    private Double[] _doubleObjArr;
    private Float[] _floatObjArr;
    private Integer[] _intObjArr;
    private Long[] _longObjArr;
    private Short[] _shortObjArr;

    // Object
    private Object _private_object;
    public Object _public_object;
    protected Object _protected_object;
    Object _packagescope_object;
    private Object[] _objArr;

    // String
    private String nameString;
    private String[] nameStrings;

    // Collections
    private Collection collection;
    private List list;
    private Map map;
    private SortedMap sortedMap;
    private Set set;
    private SortedSet sortedSet;
    private Vector vector;
    private ArrayList arrayList;
    private LinkedList linkedList;
    private HashMap hashMap;
    private Hashtable hashtable;
    private TreeMap treeMap;
    private LinkedHashMap linkedHashMap;
    private HashSet hashSet;
    private TreeSet treeSet;
    private LinkedHashSet linkedHashSet;

    // Other frequent used objects
    private String _string;
    private java.util.Date _utilDate;
    private java.sql.Date _sqlDate;
    private java.sql.Time _sqlTime;
    private java.sql.Timestamp _sqlTimestamp;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;

    /**
     * Insert your javadoc comments here
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "DummyCompleteTestBean{" +
                "doNotStreamMe=" + doNotStreamMe +
                ", _byte=" + _byte +
                ", _boolean=" + _boolean +
                ", _char=" + _char +
                ", _double=" + _double +
                ", _float=" + _float +
                ", _int=" + _int +
                ", _long=" + _long +
                ", _short=" + _short +
                ", _byteArr=" + _byteArr +
                ", _booleanArr=" + _booleanArr +
                ", _charArr=" + _charArr +
                ", _doubleArr=" + _doubleArr +
                ", _floatArr=" + _floatArr +
                ", _intArr=" + _intArr +
                ", _longArr=" + _longArr +
                ", _shortArr=" + _shortArr +
                ", _byteObj=" + _byteObj +
                ", _booleanObj=" + _booleanObj +
                ", _charObj=" + _charObj +
                ", _doubleObj=" + _doubleObj +
                ", _floatObj=" + _floatObj +
                ", _intObj=" + _intObj +
                ", _longObj=" + _longObj +
                ", _shortObj=" + _shortObj +
                ", _byteObjArr=" + (_byteObjArr == null ? null : Arrays.asList(_byteObjArr)) +
                ", _booleanObjArr=" + (_booleanObjArr == null ? null : Arrays.asList(_booleanObjArr)) +
                ", _charObjArr=" + (_charObjArr == null ? null : Arrays.asList(_charObjArr)) +
                ", _doubleObjArr=" + (_doubleObjArr == null ? null : Arrays.asList(_doubleObjArr)) +
                ", _floatObjArr=" + (_floatObjArr == null ? null : Arrays.asList(_floatObjArr)) +
                ", _intObjArr=" + (_intObjArr == null ? null : Arrays.asList(_intObjArr)) +
                ", _longObjArr=" + (_longObjArr == null ? null : Arrays.asList(_longObjArr)) +
                ", _shortObjArr=" + (_shortObjArr == null ? null : Arrays.asList(_shortObjArr)) +
                ", _private_object=" + _private_object +
                ", _public_object=" + _public_object +
                ", _protected_object=" + _protected_object +
                ", _packagescope_object=" + _packagescope_object +
                ", _objArr=" + (_objArr == null ? null : Arrays.asList(_objArr)) +
                ", nameString='" + nameString + "'" +
                ", nameStrings=" + (nameStrings == null ? null : Arrays.asList(nameStrings)) +
                ", collection=" + collection +
                ", list=" + list +
                ", map=" + map +
                ", sortedMap=" + sortedMap +
                ", set=" + set +
                ", sortedSet=" + sortedSet +
                ", vector=" + vector +
                ", arrayList=" + arrayList +
                ", linkedList=" + linkedList +
                ", hashMap=" + hashMap +
                ", hashtable=" + hashtable +
                ", treeMap=" + treeMap +
                ", linkedHashMap=" + linkedHashMap +
                ", hashSet=" + hashSet +
                ", treeSet=" + treeSet +
                ", linkedHashSet=" + linkedHashSet +
                ", _string='" + _string + "'" +
                ", _utilDate=" + _utilDate +
                ", _sqlDate=" + _sqlDate +
                ", _sqlTime=" + _sqlTime +
                ", _sqlTimestamp=" + _sqlTimestamp +
                ", bigDecimal=" + bigDecimal +
                ", bigInteger=" + bigInteger +
                "}";
    }

}
