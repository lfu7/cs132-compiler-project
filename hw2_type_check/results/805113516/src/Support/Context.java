package Support;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.HashSet;
import java.util.Set;


public class Context {

    private HashMap<String, TypeClass> classes;
    private HashMap<String, List<String>> subtypes;

    private TypeClass currClass;
    private boolean inClass;
    private Method currMethod;
    private boolean inMethod;


    public Context() {
	classes = new HashMap<>();
	subtypes = new HashMap<>();
	currClass = null;
	inClass = false;
	currMethod = null;
	inMethod = false;
    }

    //makes a new class of the given name, adds it to the list of classes
    public void addClass(String name) {
	TypeClass newClass = new TypeClass(name);
	classes.put(name, newClass);
    }

    //checks if classname already exists
    public boolean checkClass(String name) {
	if (classes.containsKey(name)) {
	    return true;
	} else {
	    return false;
	}
    }

    //set the current class be the class of the given name
    //important for scoping
    public void setCurrClass(String name) {

	if (name.equals("")) {
	    currClass = null;
	    inClass = false;
	} else {
	    currClass = classes.get(name);
	    inClass = true;
	}
    }

    //returns whether or not the current scope is in a class
    //should almost always be true
    public boolean inClass() {
	return inClass;
    }

    //returns the name of the current class
    public String currClassName() {
	return currClass.getName();
    }

    //exit the scope of the current class;
    public void exitClass() {
	currClass = null;
	inClass = false;
    }

    //gets the current class object;
    public TypeClass getCurrClass() {
        return currClass;
    }

    //returns the desired class;
    public TypeClass getClass(String classname) {
	return classes.get(classname);
    }


    
    //add subtype relationship
    public void addSubtype(String subclass, String superclass) {

	if (subtypes.containsKey(superclass)) {
	    List<String> temp = subtypes.get(superclass);
	    temp.add(subclass);
	    subtypes.put(superclass, temp);
	} else {
	    List<String> temp = new ArrayList<>();
	    temp.add(subclass);
	    subtypes.put(superclass, temp);
	}

	//set the parent pointer of the child to be to the parent 
	TypeClass child = getClass(subclass);
	TypeClass parent = getClass(superclass);
	child.setParent(parent);
    }


    //performs a BFS to check whether a class is a subclass of another
    public Boolean isSubtype(String subclass, String superclass) {

	//System.out.println(subclass + ":" + superclass);
	
	Queue<String> q = new LinkedList<>();
	q.add(superclass);

	while(q.size() > 0) {
	    String front = q.peek();
	    q.remove();

	    List<String> temp = subtypes.get(front);
	    if (temp != null) {
		
	    
		for (String s: temp) {
		    if (s.equals(subclass)) {
			return true;
		    } else {
			q.add(s);
		    }
		}
	    }
	}

	return false;
	
    }

    //prints all subtypes for debugging purposes
    public void printSubtypes() {
	System.out.println(subtypes);
    }

    //adds method to the current class
    public void addMethod(String methodname, String returnType) {
	currClass.addMethod(methodname, returnType);
    }

    //set the scope to be the current method
    public void setCurrMethod(String methodname) {
	currMethod = currClass.getMethod(methodname);
	inMethod = true;
    }

    //returns whether or not the scope is in a method
    public boolean inMethod() {
	return inMethod;
    }

    //exit the scope of the current method
    public void exitMethod() {
	currMethod = null;
	inMethod = false;
    }

    //adds field to the current class
    public boolean addField(String varname, String vartype) {
	if (currClass.checkField(varname)) {
	    return false;
	} else {
	    currClass.addField(varname, vartype);
	    return true;
	}

    }

    //adds local if the variable name is not already in use by parameter
    public boolean addLocal(String varname, String vartype) {
	if (currMethod.checkParam(varname)) {
	    //System.out.println("fail");
	    return false;
	} else {

	    if (currMethod.checkLocal(varname)) {
		return false;
	    } else {
		currMethod.addLocal(varname, vartype);
		return true;
	    }
	}
    }

    public boolean addParam(String varname, String vartype) {
	if (currMethod.checkParam(varname)) {
	    return false;
	} else {	
	    currMethod.addParam(varname, vartype);
	    return true;
	}
    }

    //need to check parents....
    //params have precedence over fields
    public String getObjectType(String varname) {
	if (currMethod.checkLocal(varname)) {
	    return currMethod.getLocalType(varname);
	} else if (currMethod.checkParam(varname)) {
	    return currMethod.getParamType(varname);
	} else if (currClass.checkField(varname)) {
	    return currClass.getFieldType(varname);
	} else {
	    TypeClass parentClass = currClass.getParent();
            while (parentClass != null) {
                if (parentClass.checkField(varname)) {
                    return parentClass.getFieldType(varname);
                } else {
                    parentClass = parentClass.getParent();
                }
            }
            return "Type error";
	}
	
	    


	/*
	if (currClass.checkField(varname)) {
	    return currClass.getFieldType(varname);
	} else if (currMethod.checkParam(varname)) {
	    return currMethod.getParamType(varname);
	} else if (currMethod.checkLocal(varname)) {
	    return currMethod.getLocalType(varname);
	} else {
	    TypeClass parentClass = currClass.getParent();

	    while (parentClass != null) {		
		if (parentClass.checkField(varname)) {
		    return parentClass.getFieldType(varname);
		} else {
		    parentClass = parentClass.getParent();
		}		
	    }
	    return "Type error";
	}
	*/
    }
    

    //likely unnecessary, cut if need to save time
    public boolean checkCycle() {

	for(Map.Entry<String, TypeClass> c: classes.entrySet()) {
	    Set<String> visited = new HashSet<>();
	    

	    
	    visited.add(c.getKey());

	    if (!dfs(c.getValue(), visited)) {
		System.out.println("cycle detected");
		return true;
	    }
	}
        return false;
    }

    public boolean dfs(TypeClass currClass, Set<String> visited) {
	if (currClass == null) {
	    return true;
	}

	TypeClass parent = currClass.getParent();
	if (parent == null) {
	    return true;
	}


	String parentName = parent.getName();

	if (visited.contains(parentName)) {
	    return false;
	} else {
	    visited.add(parentName);
	    return dfs(parent, visited);
	}
    }

    public void printAll() {

	for (String classkey: classes.keySet()) {
	    System.out.println("Class: " + classkey);
	    TypeClass currClass = classes.get(classkey);
	    List<String> currMethods = currClass.methodNames();
	    List<String> currFields = currClass.fieldNames();
	    List<String> currFieldTypes = currClass.fieldTypes();
	    System.out.println("Fields");
	    System.out.println(currFields);
	    System.out.println("Field Types");
	    System.out.println(currFieldTypes);
	    

	    for (String methodname: currMethods) {
		System.out.println("Method: " + methodname);
		Method currMethod = currClass.getMethod(methodname);
		System.out.println("Return Type: " + currMethod.getReturnType());
		List<String> methodParams = currMethod.getParamNames();
		List<String> methodParamTypes = currMethod.getParamTypes();
		List<String> methodLocals = currMethod.getLocalNames();
		List<String> methodLocalTypes = currMethod.getLocalTypes();
		System.out.println("Params");
		System.out.println(methodParams);
		System.out.println("Param Types");
		System.out.println(methodParamTypes);
		System.out.println("Locals");
		System.out.println(methodLocals);
		System.out.println("Local Types");
		System.out.println(methodLocalTypes);

		
	    }
	    
	}
	//System.out.println(classes.keySet());
    }


    
}
