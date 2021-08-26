package Support;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class Method {
    
    private HashMap<String, String> parameters;
    private List<String> parameters2;
    private HashMap<String, String> locals;
    private String name;
    private String returnType;

    
    public Method(String name, String returnType) {
	parameters = new HashMap<>();
	parameters2 = new ArrayList<>();
	locals = new HashMap<>();
	this.name = name;
	this.returnType = returnType;
    }

    public void addParam(String name, String type) {
	parameters.put(name, type);
	parameters2.add(type);
    }

    

    public Boolean checkParam(String name) {
        if (parameters.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    public String getParamType(String name) {
	return parameters.get(name);
    }
    

    public List<String> getParamNames() {
	
	List<String> ret = new ArrayList<>();
	
	
	for (String key: parameters.keySet()) {
	    ret.add(key);
	}
	
	return ret;
	
    }

    public List<String> getParamTypes() {

	return parameters2;
	/*
	List<String> ret = new ArrayList<>();


	for (String key : parameters.keySet()) {
	    ret.add(parameters.get(key));
	}

	return ret;
	*/
    }

    

    public void addLocal(String name, String type) {
	locals.put(name, type);
    }

    public Boolean checkLocal(String name) {
	if (locals.containsKey(name)) {
	    return true;
	} else {
	    return false;
	}
    }
    

    public String getLocalType(String name) {
	return locals.get(name);
    }
    

    public List<String> getLocalNames() {
	List<String> ret = new ArrayList<>();
	
        for (String key : locals.keySet()) {
            ret.add(key);
        }
	
        return ret;
    }

    public List<String> getLocalTypes() {
	List<String> ret = new ArrayList<>();

	for (String key: locals.keySet()) {
	    ret.add(locals.get(key));
	}

	return ret;
    }
		    

    public String getReturnType() {
	return returnType;
    }
}
