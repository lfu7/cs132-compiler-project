package Support;

import java.util.HashMap;

public class Context {
    private String mainFuncName;
    private HashMap<String, SparrowVMethod> methods;
    private SparrowVMethod currMethod;
    private int endCounter;

    public Context() {
        methods = new HashMap<>();
        endCounter = 0;
    }

    public void setMainFuncName(String mainFuncName) {
        this.mainFuncName = mainFuncName;
    }

    public String getMainFuncName() {
        return mainFuncName;
    }



    ////////////////////////////////////////////////////////////////////////
    //INVOLVING METHODS/////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public void newMethod(String methodName) {
        SparrowVMethod newMethod = new SparrowVMethod(methodName);
        methods.put(methodName, newMethod);
    }

    public void setCurrMethod(String methodName) {
        currMethod = methods.get(methodName);
    }

    /////////////////////////////////////////////////////////////////////////
    //GET FROM METHOD////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public int getNumLocals() {
        return currMethod.getNumLocals();
    }

    public int getNumParams(String methodName) {
        SparrowVMethod tempMethod = methods.get(methodName);
        return tempMethod.getNumParams();
    }

    public int getNumArgsOnStack() {
        return currMethod.getNumArgumentsOnStack();
    }

    public String getVarType(String varName) {
        if (currMethod.checkLocal(varName)) {
            return "local";
        } else if (currMethod.checkParam(varName)) {
            return "param";
        } else {
            return "error";
        }
    }

    public int getLocalOffset(String varName) {
        return currMethod.getLocalOffset(varName);
    }

    public int getParamOffset(String varName) {
        return currMethod.getParamOffset(varName);
    }

    public boolean checkExists(String varName) {
        return currMethod.checkExists(varName);
    }

    public String getLabelName(String labelName) {
        return currMethod.getLabelName(labelName);
    }

    /////////////////////////////////////////////////////////////////////////
    //ADD TO METHOD//////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public void addLocal(String varName) {
        currMethod.addLocal(varName);
    }

    public void addParam(String paramName) {
        currMethod.addParam(paramName);
    }

    public void addArgOnStack() {
        currMethod.addArgumentOnStack();
    }

    ////////////////////////////////////////////////////////////////////////////
    //END COUNTER//////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public int getEndCounter() {
        return endCounter;
    }

    public void incrementEndCounter() {
        endCounter ++;
    }

    /////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    public void printAll() {
        for (String methodName: methods.keySet()) {
            SparrowVMethod currMethod = methods.get(methodName);
            currMethod.printMethod();
        }
    }
}