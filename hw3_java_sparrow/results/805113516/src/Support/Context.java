package Support;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class Context {

    private HashMap<String, TypeClass> classes;
    private HashMap<String, List<String>> subtypes;

    private TypeClass currClass;
    private boolean inClass;
    private Method currMethod;
    private boolean inMethod;

    private int localCounter;
    private Stack<Integer> s;

    private boolean initializeVariables;
    private boolean checkInitialize;

    public Context() {
		classes = new HashMap<>();
		subtypes = new HashMap<>();
		currClass = null;
		inClass = false;
		currMethod = null;
		inMethod = false;

        localCounter = 0;
        s = new Stack<>();

        initializeVariables = false;
        checkInitialize = false;

    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //COUNTER VARIABLE//
    //////////////////////////////////////////////////////////////////////////////////////////

    public int getLocalCounter() {
        return localCounter;
    }

    public void incrementCounter() {
        localCounter += 1;
    }

    public void resetCounter() {
        localCounter = 0;
    }

    public void pushStack(int i) {
        s.push(i);
    }

    public Integer popStack() {
        return s.pop();
    }





    //////////////////////////////////////////////////////////////////////////////////////////
    //ADD TO CONTEXT//
    //////////////////////////////////////////////////////////////////////////////////////////

    //makes a new class of the given name, adds it to the list of classes
    public void addClass(String name) {
        TypeClass newClass = new TypeClass(name);
        classes.put(name, newClass);
    }

    //adds method to the current class
    public void addMethod(String methodname, String returnType) {
        currClass.addMethod(methodname, returnType);
    }

    //adds field to the current class if name is not already in use
    public void addField(String varname, String vartype) {
        currClass.addField(varname, vartype);
    }

    //adds local if the variable name is not already in use by parameter
    public void addLocal(String varname, String type) {
        currMethod.addLocal(varname, type);
    }

    //adds parameter to function if variable name not in use
    public void addParam(String varname, String vartype) {
        currMethod.addParam(varname, vartype);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //CHANGE IN CONTEXT//
    //////////////////////////////////////////////////////////////////////////////////////////

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

    //exit the scope of the current class;
    public void exitClass() {
        currClass = null;
        inClass = false;
    }

    //set the scope to be the current method
    public void setCurrMethod(String methodname) {
        currMethod = currClass.getMethod(methodname);
        inMethod = true;
    }

    //exit the scope of the current method
    public void exitMethod() {
        currMethod = null;
        inMethod = false;
    }

    public void setParent(String subClassName, String superClassName) {
        //System.out.println(subClassName + superClassName);
        TypeClass superClass = this.getClass(superClassName);
        TypeClass subClass = new TypeClass(superClass, subClassName);

        classes.put(subClassName, subClass);
    }

    public void setLocalInitialized(String varname) {
        if (currMethod.checkLocal(varname)) {
            currMethod.setInitialized(varname);
        }
    }

    /*
    public void setFieldInitialized(String varname){
        if (currClass.checkField(varname)){
            currClass.setInitialized(varname);
        }
    }

     */

    public void initializeOn() {
        initializeVariables = true;
    }

    public void initializeOff() {
        initializeVariables = false;
    }

    public void checkInitializeOn() {
        checkInitialize = true;
    }

    public void checkInitializeOff() {
        checkInitialize = false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //CHECK IN CONTEXT//
    //////////////////////////////////////////////////////////////////////////////////////////

    //returns whether or not the current scope is in a class
    //should almost always be true
    public boolean inClass() {
        return inClass;
    }

    //returns whether or not the scope is in a method
    public boolean inMethod() {
        return inMethod;
    }

    public boolean initalizeVariables() {
        return initializeVariables;
    }

    public boolean isLocalInitialized(String varname) {

        if (currMethod.checkLocal(varname)) {
            if (!currMethod.isInitialized(varname)) {
                return false;
            }
        }

        return true;
    }

    public boolean isCheckInitialize() {
        return checkInitialize;
    }

    /*
    public boolean isFieldInitialized(String varname) {
        if (currClass.checkField(varname)) {
            if (!currClass.isInitialized(varname)) {
                return false;
            }
        }
        return true;
    }

     */

    //////////////////////////////////////////////////////////////////////////////////////////
    //GET FROM CONTEXT//
    //////////////////////////////////////////////////////////////////////////////////////////

    //returns the name of the current class
    public String currClassName() {
        return currClass.getName();
    }

    //gets the current class object;
    public TypeClass getCurrClass() {
        return currClass;
    }

    //returns the desired class;
    public TypeClass getClass(String classname) {
        return classes.get(classname);
    }

    public ArrayList<String> getCurrMethodParams() {
        return currMethod.getParamNames();
    }

    public String getVariableType(String varname) {
        if (currMethod.checkLocal(varname)) {
            return "local";
        } else if (currMethod.checkParam(varname)) {
            return "param";
        } else if (currClass.checkField(varname)) {
            return "field";
        } else {
            return "error";
        }
    }

    public String getObjectType(String varname) {
        if (currMethod.checkLocal(varname)) {
            return currMethod.getLocalType(varname);
        } else if (currMethod.checkParam(varname)) {
            return currMethod.getParamType(varname);
        } else if (currClass.checkField(varname)) {
            return currClass.getFieldType(varname);
        } else {
            return "error";
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                /*
                Method currMethod = currClass.getMethod(methodname);
                //System.out.println("Return Type: " + currMethod.getReturnType());

                List<String> methodParams = currMethod.getParamNames();
                //List<String> methodParamTypes = currMethod.getParamTypes();
                List<String> methodLocals = currMethod.getLocalNames();
                List<String> methodLocalTypes = currMethod.getLocalTypes();
                System.out.println("Params");
                System.out.println(methodParams);
                //System.out.println("Param Types");
                //System.out.println(methodParamTypes);
                System.out.println("Locals");
                System.out.println(methodLocals);
                System.out.println("Local Types");
                System.out.println(methodLocalTypes);

                 */


            }

        }
        //System.out.println(classes.keySet());
    }
}
