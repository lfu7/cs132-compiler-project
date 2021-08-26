package Support;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs132.IR.sparrowv.Instruction;
import cs132.IR.sparrowv.Move_Id_Reg;
import cs132.IR.sparrowv.Move_Reg_Id;
import cs132.IR.token.Identifier;
import cs132.IR.token.Register;

public class Context {

    private HashMap<String, String> var_locations;
    private HashMap<String, Integer> a_registers;
    private HashMap<Integer, String> a_registers_in_use;
    private ArrayList<LivenessWrapper> a_active_intervals;

    private HashMap<String, Integer> t_registers;
    private HashMap<Integer, String> t_registers_in_use;
    private ArrayList<LivenessWrapper> t_active_intervals;

    private HashMap<String, Integer> s_registers;
    private HashMap<Integer, String> s_registers_in_use;
    private ArrayList<LivenessWrapper> s_active_intervals;

    private HashSet<String> used_callee_saved_registers;
    private boolean saveCalleeRegisters;

    private HashMap<String, SparrowVMethod> methods;
    private SparrowVMethod currMethod;

    private int dfn;


    public Context() {
        resetContext();
        methods = new HashMap<>();
    }

    public void resetContext() {
        var_locations = new HashMap<>();

        a_registers = new HashMap<>();
        a_registers_in_use = new HashMap<>();
        for (int i = 2; i <= 7; i++) {
            a_registers_in_use.put(i, "FALSE");
        }
        a_active_intervals = new ArrayList<>();

        t_registers = new HashMap<>();
        t_registers_in_use = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            t_registers_in_use.put(i, "FALSE");
        }
        t_active_intervals = new ArrayList<>();

        s_registers = new HashMap<>();
        s_registers_in_use = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            s_registers_in_use.put(i, "FALSE");
        }
        s_active_intervals = new ArrayList<>();

        used_callee_saved_registers = new HashSet<>();

        currMethod = null;
        resetDfn();
    }

    ////////////////////////////////////////////////////////////////////////
    //DEFINE VARIABLE///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public ArrayList<Instruction> defineVariable(String varName) {

        ArrayList<Instruction> ret = new ArrayList<>();
        if (!var_locations.containsKey(varName)) {
            int t_registerNumber = t_getNext();
            int a_registerNumber = a_getNext();
            int s_registerNumber = s_getNext();

            if (t_registerNumber >= 0) {
                t_assignRegister(varName, t_registerNumber);

            } else if (s_registerNumber > 0) {
                s_assignRegister(varName, s_registerNumber);
            } else if (a_registerNumber > 0) {
                a_assignRegister(varName, a_registerNumber);
            } else {
                //ret.addAll(moveOldestToStack(varName));
                var_locations.put(varName, "stack");
            }
        }
        return ret;
    }

    /////////////////////////////////////////////////////////////////////////
    //MOVE OLDEST TO STACK///////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public ArrayList<Instruction> moveOldestToStack(String varName) {
        ArrayList<Instruction> ret = new ArrayList<>();

        LivenessInterval curr_li = currMethod.getLivenessInterval(varName);
        LivenessWrapper oldest_lw = getOldest();
        String oldestName = oldest_lw.varName;

        //System.out.println(curr_li);
        //System.out.println(oldest_lw.li);
        if (curr_li.use < oldest_lw.li.use) {
            String registerType = getVarLocation(oldestName);
            String registerId = getRegisterId(oldestName);
            String registerNumber_string = registerId.substring(1);
            int registerNumber = Integer.parseInt(registerNumber_string);

            Register oldestReg = new Register(registerId);
            Identifier oldestStack = new Identifier("stack_save_"+oldestName);
            Move_Id_Reg spillToStack = new Move_Id_Reg(oldestStack, oldestReg);
            ret.add(spillToStack);

            ArrayList<String> toDelete = new ArrayList<>();
            toDelete.add(oldestName);
            removeRegisters(toDelete);
            var_locations.put(oldestName, "stack");

            var_locations.put(varName, registerType);
            if (registerType.equals("a")) {
                a_assignRegister(varName, registerNumber);
            } else if (registerType.equals("s")) {
                s_assignRegister(varName, registerNumber);
            } else if (registerType.equals("t")) {
                t_assignRegister(varName, registerNumber);
            }

        } else {
            var_locations.put(varName, "stack");
        }

        return ret;
    }

    public LivenessWrapper getOldest() {
        int oldest = -1;
        String oldestVarName = "";
        LivenessInterval oldestLi = null;
        LivenessWrapper ret = new LivenessWrapper();

        for (LivenessWrapper lw: a_active_intervals) {
            if (lw.li.use > oldest) {
                oldest = lw.li.use;
                oldestVarName = lw.varName;
                oldestLi = lw.li;
            }
        }

        for (LivenessWrapper lw: s_active_intervals) {
            if (lw.li.use > oldest) {
                oldest = lw.li.use;
                oldestVarName = lw.varName;
                oldestLi = lw.li;
            }
        }

        for (LivenessWrapper lw: t_active_intervals) {
            if (lw.li.use > oldest) {
                oldest = lw.li.use;
                oldestVarName = lw.varName;
                oldestLi = lw.li;
            }
        }

        ret.varName = oldestVarName;
        ret.li = oldestLi;

        return ret;
    }


    /////////////////////////////////////////////////////////////////////////
    //ASSIGN T REGISTER//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public void t_assignRegister(String varName, int registerNumber) {
        var_locations.put(varName, "t");
        t_registers.put(varName, registerNumber);
        t_registers_in_use.put(registerNumber, varName);

        LivenessWrapper lw = new LivenessWrapper();
        LivenessInterval li = currMethod.getLivenessInterval(varName);
        lw.varName = varName;
        lw.li = li;
        t_active_intervals.add(lw);

    }

    public int t_getNext() {
        for (int i = 0; i <= 5; i++) {
            if (t_registers_in_use.get(i).equals("FALSE")) {
                return i;
            }
        }
        return -1;
    }

    /////////////////////////////////////////////////////////////////////////
    //ASSIGN S REGISTER//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public void s_assignRegister(String varName, int registerNumber) {
        var_locations.put(varName, "s");
        s_registers.put(varName, registerNumber);
        s_registers_in_use.put(registerNumber,varName);

        add_callee_saved_register("s"+registerNumber);

        LivenessWrapper lw = new LivenessWrapper();
        LivenessInterval li = currMethod.getLivenessInterval(varName);
        lw.varName = varName;
        lw.li = li;
        s_active_intervals.add(lw);
    }

    public int s_getNext() {
        for (int i = 1; i <= 9; i++) {
            if (s_registers_in_use.get(i).equals("FALSE")) {
                return i;
            }
        }
        return -1;
    }


    /////////////////////////////////////////////////////////////////////////
    //ASSIGN A REGISTER//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public void a_assignRegister(String varName, int registerNumber) {
        var_locations.put(varName, "a");
        a_registers.put(varName, registerNumber);
        a_registers_in_use.put(registerNumber, varName);

        LivenessWrapper lw = new LivenessWrapper();
        LivenessInterval li = currMethod.getLivenessInterval(varName);
        lw.varName = varName;
        lw.li = li;
        a_active_intervals.add(lw);
    }

    public int a_getNext() {
        for (int i = 2; i <= 7; i++) {
            if (a_registers_in_use.get(i).equals("FALSE")) {
                return i;
            }
        }
        return -1;
    }

    /////////////////////////////////////////////////////////////////////////
    //ASSIGN TO STACK////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public void assignStack(String varName) {
        var_locations.put(varName, "stack");
    }

    /////////////////////////////////////////////////////////////////////////
    //CALL SEQUENCE//////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    public ArrayList<String> a_activeVariables() {

        ArrayList<String> ret = new ArrayList<>();

        for (LivenessWrapper lw : a_active_intervals) {
            //System.out.println(lw.varName + " " + lw.li.def + " " + lw.li.use);
            if (lw.li.use >= dfn) {
                String varName = lw.varName;
                ret.add(varName);
            }
        }

        return ret;
    }


    public ArrayList<Instruction> a_saveRegisters(ArrayList<String> activeVars) {
        ArrayList<Instruction> ret = new ArrayList<>();

        for (int i = 2; i <= 7; i++) {
            String varName = a_registers_in_use.get(i);
            if (!varName.equals("FALSE")) {
                if (activeVars.contains(varName)) {
                    assignStack(varName);

                    Register a_register = new Register("a"+i);
                    Identifier a_stack_save = new Identifier(varName);
                    Move_Id_Reg save_a = new Move_Id_Reg(a_stack_save, a_register);
                    ret.add(save_a);
                }
            }
        }

        return ret;
    }

    public ArrayList<Instruction> a_restoreRegisters(ArrayList<String> activeVars) {
        ArrayList<Instruction> ret = new ArrayList<>();

        for (int i = 2; i <= 7; i++) {
            String varName = a_registers_in_use.get(i);
            if (!varName.equals("FALSE")) {
                if (activeVars.contains(varName)) {
                    var_locations.put(varName, "a");
                    Identifier a_stack_save = new Identifier(varName);
                    Register a_register = new Register("a"+i);
                    Move_Reg_Id restore_a = new Move_Reg_Id(a_register, a_stack_save);
                    ret.add(restore_a);
                }
            }
        }

        return ret;
    }

    public ArrayList<String> t_activeVariables() {

        ArrayList<String> ret = new ArrayList<>();

        for (LivenessWrapper lw : t_active_intervals) {
            if (lw.li.use > dfn) {
                String varName = lw.varName;
                ret.add(varName);
            }
        }
        return ret;
    }

    public ArrayList<Instruction> t_saveRegisters(ArrayList<String> activeVars) {
        ArrayList<Instruction> ret = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            String varName = t_registers_in_use.get(i);
            if (!varName.equals("FALSE")) {
                if (activeVars.contains(varName)) {
                    assignStack(varName);

                    Register t_register = new Register("t"+i);
                    Identifier t_stack_save = new Identifier(varName);
                    Move_Id_Reg save_t = new Move_Id_Reg(t_stack_save, t_register);
                    ret.add(save_t);
                }
            }
        }

        return ret;
    }

    public ArrayList<Instruction> t_restoreRegisters(ArrayList<String> activeVars) {
        ArrayList<Instruction> ret = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            String varName = t_registers_in_use.get(i);
            if (!varName.equals("FALSE")) {
                if (activeVars.contains(varName)) {
                    var_locations.put(varName, "t");

                    Register t_register = new Register("t"+i);
                    Identifier t_stack_save = new Identifier(varName);
                    Move_Reg_Id restore_t = new Move_Reg_Id(t_register, t_stack_save);
                    ret.add(restore_t);
                }
            }
        }

        return ret;
    }


    /////////////////////////////////////////////////////////////////////////
    //GET FROM CONTEXT///////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    public String getVarLocation(String varName) {
        if (var_locations.containsKey(varName)) {
            return var_locations.get(varName);
        } else {
            return "stack";
        }
    }

    public String getRegisterId(String varName) {
        String registerType = var_locations.get(varName);
        if (registerType.equals("a")) {
            int registerNumber = a_registers.get(varName);
            return "a"+registerNumber;
        }

        if (registerType.equals("s")) {
            int registerNumber = s_registers.get(varName);
            return "s"+registerNumber;
        }

        if (registerType.equals("t")) {
            int registerNumber = t_registers.get(varName);
            return "t"+registerNumber;
        }

        return "not a register";
    }

    //////////////////////////////////////////////////////////////////////////
    //CLEAN REGISTERS/////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public void cleanRegisters() {
        ArrayList<String> toRemove = new ArrayList<>();

        for (LivenessWrapper lw: a_active_intervals) {
            if (lw.li.use <= dfn) {
                toRemove.add(lw.varName);
            }
        }

        for (LivenessWrapper lw: s_active_intervals) {
            if (lw.li.use <= dfn) {
                toRemove.add(lw.varName);
            }
        }

        for (LivenessWrapper lw: t_active_intervals) {
            if (lw.li.use <= dfn) {
                toRemove.add(lw.varName);
            }
        }

        removeRegisters(toRemove);
    }

    //////////////////////////////////////////////////////////////////////////
    //REMOVE REGISTERS////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public void removeRegisters(ArrayList<String> varNames) {
        for (String varName: varNames) {
            String varLocation = getVarLocation(varName);
            String registerId = getRegisterId(varName);
            String registerNumber_String = registerId.substring(1);
            int registerNumber = Integer.parseInt(registerNumber_String);

            if (varLocation.equals("a")) {
                a_removeActiveInterval(varName);
                a_registers_in_use.put(registerNumber, "FALSE");
                a_registers.remove(varName);
                var_locations.remove(varName);

            } else if (varLocation.equals("s")) {
                s_removeActiveInterval(varName);
                s_registers_in_use.put(registerNumber, "FALSE");
                s_registers.remove(varName);
                var_locations.remove(varName);

            } else if (varLocation.equals("t")) {
                t_removeActiveInterval(varName);
                t_registers_in_use.put(registerNumber, "FALSE");
                t_registers.remove(varName);
                var_locations.remove(varName);

            } else {
                System.out.println("variable doesnt exist");
            }
        }
    }

    public void a_removeActiveInterval(String varName) {
        LivenessWrapper toRemove = null;
        for (LivenessWrapper lw: a_active_intervals) {
            if (lw.varName.equals(varName)) {
                toRemove = lw;
                break;
            }
        }

        if (toRemove != null) {
            a_active_intervals.remove(toRemove);
        }
    }

    public void s_removeActiveInterval(String varName) {
        LivenessWrapper toRemove = null;
        for (LivenessWrapper lw: s_active_intervals) {
            if (lw.varName.equals(varName)) {
                toRemove = lw;
                break;
            }
        }

        if (toRemove != null) {
            s_active_intervals.remove(toRemove);
        }
    }

    public void t_removeActiveInterval(String varName) {
        LivenessWrapper toRemove = null;
        for (LivenessWrapper lw: t_active_intervals) {
            if (lw.varName.equals(varName)) {
                toRemove = lw;
                break;
            }
        }

        if (toRemove != null) {
            t_active_intervals.remove(toRemove);
        }
    }



    //////////////////////////////////////////////////////////////////////////
    //CALLEE SAVED REGISTERS//////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    public void add_callee_saved_register(String registerName) {
        used_callee_saved_registers.add(registerName);
    }

    public ArrayList<Instruction> save_callee_registers() {
        ArrayList<Instruction> ret = new ArrayList<>();
        for (String registerName: used_callee_saved_registers) {
            Register s = new Register(registerName);
            Identifier s_save = new Identifier("stack_save_variable_"+registerName);
            Move_Id_Reg save_callee_register = new Move_Id_Reg(s_save, s);
            ret.add(save_callee_register);
        }
        return ret;
    }

    public ArrayList<Instruction> restore_callee_registers() {
        ArrayList<Instruction> ret = new ArrayList<>();
        for (String registerName: used_callee_saved_registers) {
            Register s = new Register(registerName);
            Identifier s_save = new Identifier("stack_save_variable_"+registerName);
            Move_Reg_Id restore_callee_register = new Move_Reg_Id(s, s_save);
            ret.add(restore_callee_register);
        }
        return ret;
    }

    public void setSaveCalleeRegisters(boolean bool) {
        saveCalleeRegisters = bool;
    }

    public boolean getSaveCalleeRegisters() {
        return saveCalleeRegisters;
    }


    ///////////////////////////////////////////////////////////////////////////
    //SCC ANALYSIS/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public void newSCC(String labelName, int dfn) {
        currMethod.newSCC(labelName, dfn);
    }

    public void endSCC(String labelName, int dfn) {
        currMethod.endSCC(labelName, dfn);
    }

    public void cleanSCC() {
        currMethod.cleanSCC();
    }


    ///////////////////////////////////////////////////////////////////////////
    //LIVENESS ANALYSIS////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public void addMethod(String methodName) {
        SparrowVMethod newMethod = new SparrowVMethod();
        methods.put(methodName, newMethod);
    }

    public void setCurrMethod(String methodName) {
        currMethod = methods.get(methodName);
    }

    public void resetDfn() {
        dfn = 0;
    }

    public void incrementDfn() {
        dfn += 1;
    }

    public int getDfn() {
        return dfn;
    }

    public void assignDef(String varName, int dfn) {
        currMethod.assignDef(varName, dfn);

    }
    public void assignUse(String varName, int dfn) {
        currMethod.assignUse(varName, dfn);
    }

    public void cleanLiveness() {
        currMethod.cleanLiveness();
    }

    public void extendLiveness() {
        currMethod.extendLiveness();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPERS/////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    public void printSCC() {
        currMethod.printSCC();
    }

    public void printLiveness() {
        currMethod.printLiveness();
    }


}
