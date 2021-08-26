package Support;

import java.util.ArrayList;
import java.util.HashMap;

public class SparrowVMethod {

    private String methodName;

    private ArrayList<String> locals;
    private ArrayList<String> params;
    private HashMap<String, Integer> local_offsets;
    private HashMap<String, Integer> param_offsets;

    int local_offset;
    int param_offset;

    int numArgumentsOnStack;

    public SparrowVMethod(String methodName) {

        this.methodName = methodName;

        locals = new ArrayList<>();
        params = new ArrayList<>();

        local_offsets = new HashMap<>();
        param_offsets = new HashMap<>();

        local_offset = 12;
        param_offset = 0;

        numArgumentsOnStack = 0;
    }

    ///////////////////////////////////////////////////////////////
    //GET FROM METHOD//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    public int getNumLocals() {
        return locals.size();
    }

    public int getNumParams() {
        return params.size();
    }

    public int getNumArgumentsOnStack() {
        return numArgumentsOnStack;
    }

    public int getLocalOffset(String varName) {
        return local_offsets.get(varName);
    }

    public int getParamOffset(String paramName) {
        return param_offsets.get(paramName);
    }

    public String getLabelName(String labelName) {
        return methodName + "_" + labelName;
    }

    ////////////////////////////////////////////////////////////////
    //CHECK IN METHOD///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    public boolean checkLocal(String varName) {
        if (locals.contains(varName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkParam(String varName) {
        if (params.contains(varName)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkExists(String varName) {

        if (params.contains(varName)) {
            return true;
        } else if (locals.contains(varName)) {
            return true;
        } else {
            return false;
        }
    }


    /////////////////////////////////////////////////////////////////
    //ADD TO METHOD//////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public void addLocal(String varName) {
        locals.add(varName);
        local_offsets.put(varName, local_offset);
        local_offset += 4;
    }

    public void addParam(String paramName) {
        params.add(paramName);
        param_offsets.put(paramName, param_offset);
        param_offset += 4;
    }

    public void addArgumentOnStack() {
        numArgumentsOnStack += 1;
    }

    ////////////////////////////////////////////////////////////////////////
    //PRINT METHOD//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    public void printMethod(){
        System.out.println("---------------------------------");
        System.out.println(methodName);
        System.out.println("---------------------------------");

        for (String paramName : params) {
            int paramOffset = param_offsets.get(paramName);
            System.out.println(paramName + ": " + paramOffset);
        }

        for (String localName: locals) {
            int localOffset= local_offsets.get(localName);
            System.out.println(localName + ": " + localOffset);
        }
    }


}