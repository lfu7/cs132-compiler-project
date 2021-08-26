package Support;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class TypeClass {

    private HashMap<String, String> fields;
    private HashMap<String, Integer> fieldOffset;
    private HashMap<String, Boolean> fieldInitialized;
    private int numFields;
    private HashMap<String, Method> methods;
    private HashMap<String, Integer> methodOffset;
    private int numMethods;

    private ArrayList<String> methodNames;
    private ArrayList<String> fieldNames;


    private TypeClass parentClass;
    private String name;

    private HashMap<String, String> methodParent;
    
    
    public TypeClass(String name) {
        fields = new HashMap<>();
        fieldOffset = new HashMap<>();
        this.fieldInitialized = new HashMap<>();
        numFields = 0;

        methods = new HashMap<>();
        methodOffset = new HashMap<>();
        numMethods = 0;

        this.methodNames = new ArrayList<>();
        this.fieldNames = new ArrayList<>();

        this.parentClass = null;
        this.name = name;

        this.methodParent = new HashMap<>();
    }

    public TypeClass(TypeClass c, String subClassName) {

        this.fields = new HashMap<>(c.fields);
        this.fieldOffset = new HashMap<>(c.fieldOffset);
        this.fieldInitialized = new HashMap<>();
        this.numFields = c.numFields;

        this.methods = new HashMap<>(c.methods);
        this.methodOffset = new HashMap<>(c.methodOffset);
        this.numMethods = c.numMethods;

        this.methodNames = new ArrayList<>(c.methodNames);
        this.fieldNames = new ArrayList<>(c.fieldNames);

        this.parentClass = c;
        this.name = subClassName;

        this.methodParent = new HashMap<>(c.methodParent);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //ADD TO CLASS//
    //////////////////////////////////////////////////////////////////////////////////////////
    public void setParent(TypeClass parent) {
        this.parentClass = parent;
    }

    public void addField(String name, String type) {

        if (checkField(name)) {
            fields.put(name, type);
        } else {
            fields.put(name, type);
            numFields += 1;
            fieldOffset.put(name, (numFields-1)*8);
        }
        setUninitialized(name);
        fieldNames.add(name);
    }

    //adds new method of desired return type
    public void addMethod(String name, String returnType) {
        Method newMethod = new Method(name, returnType);

        if (checkMethod(name)) {
            methods.put(name, newMethod);
        } else {
            methods.put(name, newMethod);
            numMethods += 1;
            methodOffset.put(name, (numMethods-1)*4);
        }
        methodNames.add(name);
        methodParent.put(name, this.name);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //CHECK IN CLASS//
    //////////////////////////////////////////////////////////////////////////////////////////
    public Boolean checkField(String name) {
        if (fields.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    //check if method exists
    //inherit methods from parent classes
    public Boolean checkMethod(String name) {
        if (methods.containsKey(name)) {
            return true;
        } else {
            TypeClass parentClass = getParent();
            if (parentClass != null) {
                return parentClass.checkMethod(name);
            } else {
                return false;
            }
        }
    }

    public void setUninitialized(String name) {
        fieldInitialized.put(name, false);
    }

    public void setInitialized(String name) {
        fieldInitialized.put(name, true);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //GET FROM CLASS//
    //////////////////////////////////////////////////////////////////////////////////////////
    public String getName() {
        return name;
    }

    public TypeClass getParent() {
        return parentClass;
    }

    public String getFieldType(String name) {
        return fields.get(name);
    }

    public Method getMethod(String name) {

	    if (methods.containsKey(name)) {
	        return methods.get(name);
	    } else {
	        return parentClass.getMethod(name);
	    }
    }

    public int getNumFields() {
        return numFields;
    }

    public int getNumMethods() {
        return numMethods;
    }

    public int getFieldOffset(String name) {
        return fieldOffset.get(name);
    }

    public int getMethodOffset(String name) {
        return methodOffset.get(name);
    }

    public Boolean isInitialized(String name) {
        if (fieldInitialized.get(name)) {
            return true;
        } else {
            return false;
        }
    }

    public String getMethodParent(String name) {
        return methodParent.get(name);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public ArrayList<String> methodNames() {
	    ArrayList<String> ret = new ArrayList<>();

	    for (String key: methods.keySet()) {
	        ret.add(key);
	    }

	    return ret;
    }

    public ArrayList<String> methodNames2() {
        return methodNames;
    }

    public ArrayList<String> fieldNames() {
        ArrayList <String> ret = new ArrayList<>();
        for (String key: fields.keySet()) {
            ret.add(key);
        }
        return ret;
    }

    public ArrayList<String> fieldTypes() {
        ArrayList<String> ret = new ArrayList<>();
        for (String key: fields.keySet()) {
            ret.add(fields.get(key));
        }
        return ret;
	
    }
}
