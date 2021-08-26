package Support;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class TypeClass {

    
    private HashMap<String, String> fields;
    private HashMap<String, Method> methods;
    private TypeClass parentClass;
    private String name;
    
    
    public TypeClass(String name) {
	fields = new HashMap<>();
	methods = new HashMap<>();
	this.parentClass = null;
	this.name = name;
    }

    //sets the parent class parameter to the provided name
    public void setParent(TypeClass parent) {
        this.parentClass = parent;
    }

    //gets the parent of the current class
    public TypeClass getParent() {
	return parentClass;
    }
    
    //gets the name of the current class
    public String getName() {
	return name;
    }

    public void addField(String name, String type) {
	fields.put(name, type);
    }

    public Boolean checkField(String name) {
	if (fields.containsKey(name)) {
	    return true;
	} else {
	    return false;
	}
    }

    public String getFieldType(String name) {
	return fields.get(name);
    }
        
    public void addMethod(String name, String returnType) {
	Method newMethod = new Method(name, returnType);
	methods.put(name, newMethod);
    }
    
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

    public Boolean checkMethodCurrClass(String name) {
	if (methods.containsKey(name)) {
            return true;
        }
	return false;
    }

    public Method getMethod(String name) {

	if (methods.containsKey(name)) {
	    return methods.get(name);
	} else {
	    return parentClass.getMethod(name);
	}
    }

    public List<String> methodNames() {
	List<String> ret = new ArrayList<>();

	for (String key: methods.keySet()) {
	    ret.add(key);
	}

	return ret;
    }

    public List<String> fieldNames() {
	List<String> ret = new ArrayList<>();
        for (String key: fields.keySet()) {
            ret.add(key);
        }
        return ret;
    }

    public List<String> fieldTypes() {
        List<String> ret = new ArrayList<>();
        for (String key: fields.keySet()) {
            ret.add(fields.get(key));
        }
        return ret;
	
    }
}
