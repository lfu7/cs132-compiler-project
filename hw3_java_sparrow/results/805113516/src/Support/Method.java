package Support;

import java.util.HashMap;
import java.util.ArrayList;


public class Method {
    
    private HashMap<String, String> parameters;
    private ArrayList<String> parameterNames;
    private HashMap<String, String> locals;
    private HashMap<String, Boolean> localInitialized;
    private String name;
    private String returnType;

    
    public Method(String name, String returnType) {
	    parameters = new HashMap<>();
	    parameterNames = new ArrayList<>();
	    locals = new HashMap<>();
        localInitialized = new HashMap<>();
	    this.name = name;
	    this.returnType = returnType;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //ADD TO METHOD//
    //////////////////////////////////////////////////////////////////////////////////////////
    public void addParam(String name, String type) {
        parameters.put(name, type);
        parameterNames.add(name);
    }

    public void addLocal(String name, String type) {
        locals.put(name, type);
        setUninitialized(name);
    }

    public void setUninitialized(String name) {
        localInitialized.put(name, false);
    }

    public void setInitialized(String name) {
        localInitialized.put(name, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //CHECK IN METHOD//
    //////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkParam(String name) {
        if (parameters.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkLocal(String name) {
        if (locals.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInitialized(String name) {
        //System.out.println(name);
        //System.out.println(localInitialized);
        if (localInitialized.get(name)) {
            return true;
        } else {
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //GET FROM METHOD//
    //////////////////////////////////////////////////////////////////////////////////////////
    public String getName() {
	return this.name;
    }

    public String getParamType(String name) {
        return parameters.get(name);
    }

    public String getLocalType(String name) {
        return locals.get(name);
    }

    public String getReturnType() { return returnType; }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<String> getParamNames() {
        return parameterNames;
    }

    public ArrayList<String> getLocalNames() {
        ArrayList<String> localNames = new ArrayList<>();
        for (String s: locals.keySet()) {
            localNames.add(s);
        }
        return localNames;
    }

    public ArrayList<String> getLocalTypes() {
        ArrayList<String> localTypes = new ArrayList<>();
        for (String s: locals.keySet()) {
            localTypes.add(locals.get(s));
        }
        return localTypes;
    }
}
