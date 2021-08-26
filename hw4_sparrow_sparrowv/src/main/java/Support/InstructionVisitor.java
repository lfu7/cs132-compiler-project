package Support;

import cs132.IR.sparrowv.*;
import cs132.IR.sparrowv.Instruction;
import cs132.IR.syntaxtree.*;
import cs132.IR.syntaxtree.Add;
import cs132.IR.syntaxtree.Alloc;
import cs132.IR.syntaxtree.Call;
import cs132.IR.syntaxtree.ErrorMessage;
import cs132.IR.syntaxtree.Goto;
import cs132.IR.syntaxtree.IfGoto;
import cs132.IR.syntaxtree.LessThan;
import cs132.IR.syntaxtree.Load;
import cs132.IR.syntaxtree.Multiply;
import cs132.IR.syntaxtree.Print;
import cs132.IR.syntaxtree.Store;
import cs132.IR.syntaxtree.Subtract;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Register;
import cs132.IR.visitor.GJDepthFirst;

import cs132.IR.token.Identifier;
import cs132.IR.token.Label;

import java.util.ArrayList;

public class InstructionVisitor extends GJDepthFirst<ArrayList<Instruction>, Context> {

    @Override
    public ArrayList<Instruction> visit(cs132.IR.syntaxtree.Instruction n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    public ArrayList<Instruction> visit(LabelWithColon n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String labelName = n.f0.f0.tokenImage;
        Label myLabel = new Label(labelName);
        LabelInstr myLabelInstr = new LabelInstr(myLabel);
        ret.add(myLabelInstr);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(SetInteger n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));

        String valString = n.f2.f0.tokenImage;
        int val = Integer.parseInt(valString);


        if (c.getVarLocation(lhs_name).equals("stack")) {
            Identifier lhs_id = new Identifier(lhs_name);
            Register lhs_r = new Register("s10");
            Move_Reg_Integer loadIntToTemp = new Move_Reg_Integer(lhs_r, val);
            ret.add(loadIntToTemp);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            Register lhs_r = new Register(registerId);
            Move_Reg_Integer loadIntToTemp = new Move_Reg_Integer(lhs_r, val);
            ret.add(loadIntToTemp);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(SetFuncName n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String functionString = n.f3.f0.tokenImage;
        FunctionName func_name = new FunctionName(functionString);

        if (c.getVarLocation(lhs_name).equals("stack")) {
            Identifier lhs_id = new Identifier(lhs_name);
            Register lhs_r = new Register("s10");
            Move_Reg_FuncName loadFuncToTemp = new Move_Reg_FuncName(lhs_r, func_name);
            ret.add(loadFuncToTemp);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            Register lhs_r = new Register(registerId);
            Move_Reg_FuncName loadFuncToTemp = new Move_Reg_FuncName(lhs_r, func_name);
            ret.add(loadFuncToTemp);
        }


        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Add n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String rhs1_name = n.f2.f0.tokenImage;
        String rhs2_name = n.f4.f0.tokenImage;

        Register rhs1_r;
        if (c.getVarLocation(rhs1_name).equals("stack")) {
            rhs1_r = new Register("s10");
            Identifier rhs1_id = new Identifier(rhs1_name);
            Move_Reg_Id loadRhs1Temp1 = new Move_Reg_Id(rhs1_r, rhs1_id);
            ret.add(loadRhs1Temp1);
        } else {
            String registerId = c.getRegisterId(rhs1_name);
            rhs1_r = new Register(registerId);
        }

        Register rhs2_r;
        if (c.getVarLocation(rhs2_name).equals("stack")) {
            rhs2_r = new Register("s11");
            Identifier rhs2_id = new Identifier(rhs2_name);
            Move_Reg_Id loadRhs2Temp2 = new Move_Reg_Id(rhs2_r, rhs2_id);
            ret.add(loadRhs2Temp2);
        } else {
            String registerId = c.getRegisterId(rhs2_name);
            rhs2_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);
            cs132.IR.sparrowv.Add addRhs = new cs132.IR.sparrowv.Add(lhs_r, rhs1_r, rhs2_r);
            ret.add(addRhs);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            cs132.IR.sparrowv.Add addRhs = new cs132.IR.sparrowv.Add(lhs_r, rhs1_r, rhs2_r);
            ret.add(addRhs);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Subtract n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String rhs1_name = n.f2.f0.tokenImage;
        String rhs2_name = n.f4.f0.tokenImage;

        Register rhs1_r;
        if (c.getVarLocation(rhs1_name).equals("stack")) {
            rhs1_r = new Register("s10");
            Identifier rhs1_id = new Identifier(rhs1_name);
            Move_Reg_Id loadRhs1Temp1 = new Move_Reg_Id(rhs1_r, rhs1_id);
            ret.add(loadRhs1Temp1);
        } else {
            String registerId = c.getRegisterId(rhs1_name);
            rhs1_r = new Register(registerId);
        }

        Register rhs2_r;
        if (c.getVarLocation(rhs2_name).equals("stack")) {
            rhs2_r = new Register("s11");
            Identifier rhs2_id = new Identifier(rhs2_name);
            Move_Reg_Id loadRhs2Temp2 = new Move_Reg_Id(rhs2_r, rhs2_id);
            ret.add(loadRhs2Temp2);
        } else {
            String registerId = c.getRegisterId(rhs2_name);
            rhs2_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);
            cs132.IR.sparrowv.Subtract subtractRhs = new cs132.IR.sparrowv.Subtract(lhs_r, rhs1_r, rhs2_r);
            ret.add(subtractRhs);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            cs132.IR.sparrowv.Subtract subtractRhs = new cs132.IR.sparrowv.Subtract(lhs_r, rhs1_r, rhs2_r);
            ret.add(subtractRhs);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Multiply n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String rhs1_name = n.f2.f0.tokenImage;
        String rhs2_name = n.f4.f0.tokenImage;

        Register rhs1_r;
        if (c.getVarLocation(rhs1_name).equals("stack")) {
            rhs1_r = new Register("s10");
            Identifier rhs1_id = new Identifier(rhs1_name);
            Move_Reg_Id loadRhs1Temp1 = new Move_Reg_Id(rhs1_r, rhs1_id);
            ret.add(loadRhs1Temp1);
        } else {
            String registerId = c.getRegisterId(rhs1_name);
            rhs1_r = new Register(registerId);
        }

        Register rhs2_r;
        if (c.getVarLocation(rhs2_name).equals("stack")) {
            rhs2_r = new Register("s11");
            Identifier rhs2_id = new Identifier(rhs2_name);
            Move_Reg_Id loadRhs2Temp2 = new Move_Reg_Id(rhs2_r, rhs2_id);
            ret.add(loadRhs2Temp2);
        } else {
            String registerId = c.getRegisterId(rhs2_name);
            rhs2_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);
            cs132.IR.sparrowv.Multiply multiplyRhs = new cs132.IR.sparrowv.Multiply(lhs_r, rhs1_r, rhs2_r);
            ret.add(multiplyRhs);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            cs132.IR.sparrowv.Multiply multiplyRhs = new cs132.IR.sparrowv.Multiply(lhs_r, rhs1_r, rhs2_r);
            ret.add(multiplyRhs);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(LessThan n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String rhs1_name = n.f2.f0.tokenImage;
        String rhs2_name = n.f4.f0.tokenImage;

        Register rhs1_r;
        if (c.getVarLocation(rhs1_name).equals("stack")) {
            rhs1_r = new Register("s10");
            Identifier rhs1_id = new Identifier(rhs1_name);
            Move_Reg_Id loadRhs1Temp1 = new Move_Reg_Id(rhs1_r, rhs1_id);
            ret.add(loadRhs1Temp1);
        } else {
            String registerId = c.getRegisterId(rhs1_name);
            rhs1_r = new Register(registerId);
        }

        Register rhs2_r;
        if (c.getVarLocation(rhs2_name).equals("stack")) {
            rhs2_r = new Register("s11");
            Identifier rhs2_id = new Identifier(rhs2_name);
            Move_Reg_Id loadRhs2Temp2 = new Move_Reg_Id(rhs2_r, rhs2_id);
            ret.add(loadRhs2Temp2);
        } else {
            String registerId = c.getRegisterId(rhs2_name);
            rhs2_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);
            cs132.IR.sparrowv.LessThan compareRhs = new cs132.IR.sparrowv.LessThan(lhs_r, rhs1_r, rhs2_r);
            ret.add(compareRhs);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            cs132.IR.sparrowv.LessThan compareRhs = new cs132.IR.sparrowv.LessThan(lhs_r, rhs1_r, rhs2_r);
            ret.add(compareRhs);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Load n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String base_name = n.f3.f0.tokenImage;
        String offsetString = n.f5.f0.tokenImage;
        int offset = Integer.parseInt(offsetString);

        Register base_r;
        if (c.getVarLocation(base_name).equals("stack")) {
            base_r = new Register("s11");
            Identifier base_id = new Identifier(base_name);
            Move_Reg_Id loadBaseTemp2 = new Move_Reg_Id(base_r, base_id);
            ret.add(loadBaseTemp2);
        } else {
            String registerId = c.getRegisterId(base_name);
            base_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);

            cs132.IR.sparrowv.Load loadToTemp1 = new cs132.IR.sparrowv.Load(lhs_r, base_r, offset);
            ret.add(loadToTemp1);

            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            cs132.IR.sparrowv.Load loadToTemp1 = new cs132.IR.sparrowv.Load(lhs_r, base_r, offset);
            ret.add(loadToTemp1);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Store n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String base_name = n.f1.f0.tokenImage;
        String rhs_name = n.f6.f0.tokenImage;
        String offsetString = n.f3.f0.tokenImage;
        int offset = Integer.parseInt(offsetString);

        Register base_r;
        if (c.getVarLocation(base_name).equals("stack")) {
            base_r = new Register("s10");
            Identifier base_id = new Identifier(base_name);
            Move_Reg_Id loadBaseTemp1 = new Move_Reg_Id(base_r, base_id);
            ret.add(loadBaseTemp1);
        } else {
            String registerId = c.getRegisterId(base_name);
            base_r = new Register(registerId);
        }

        Register rhs_r;
        if (c.getVarLocation(rhs_name).equals("stack")) {
            rhs_r = new Register("s11");
            Identifier rhs_id = new Identifier(rhs_name);
            Move_Reg_Id loadRhsTemp2 = new Move_Reg_Id(rhs_r, rhs_id);
            ret.add(loadRhsTemp2);
        } else {
            String registerId = c.getRegisterId(rhs_name);
            rhs_r = new Register(registerId);
        }

        cs132.IR.sparrowv.Store storeAtBase = new cs132.IR.sparrowv.Store(base_r, offset, rhs_r);
        ret.add(storeAtBase);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Move n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String rhs_name = n.f2.f0.tokenImage;

        Register rhs_r;
        if (c.getVarLocation(rhs_name).equals("stack")) {
            rhs_r = new Register("s11");
            Identifier rhs_id = new Identifier(rhs_name);
            Move_Reg_Id loadRhs2Temp2 = new Move_Reg_Id(rhs_r, rhs_id);
            ret.add(loadRhs2Temp2);
        } else {
            String registerId = c.getRegisterId(rhs_name);
            rhs_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);
            Move_Reg_Reg copyValue = new Move_Reg_Reg(lhs_r, rhs_r);
            ret.add(copyValue);

            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);
            Move_Reg_Reg copyValue = new Move_Reg_Reg(lhs_r, rhs_r);
            ret.add(copyValue);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Alloc n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        ret.addAll(c.defineVariable(lhs_name));
        String allocSize_name = n.f4.f0.tokenImage;

        Register allocSize_r;
        if (c.getVarLocation(allocSize_name).equals("stack")) {
            allocSize_r = new Register("s11");
            Identifier allocSize_id = new Identifier(allocSize_name);
            Move_Reg_Id loadAllocSizeTemp2 = new Move_Reg_Id(allocSize_r, allocSize_id);
            ret.add(loadAllocSizeTemp2);
        } else {
            String registerId = c.getRegisterId(allocSize_name);
            allocSize_r = new Register(registerId);
        }

        Register lhs_r;
        if (c.getVarLocation(lhs_name).equals("stack")) {
            lhs_r = new Register("s10");
            Identifier lhs_id = new Identifier(lhs_name);

            cs132.IR.sparrowv.Alloc allocToReg = new cs132.IR.sparrowv.Alloc(lhs_r, allocSize_r);
            ret.add(allocToReg);

            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            lhs_r = new Register(registerId);

            cs132.IR.sparrowv.Alloc allocToReg = new cs132.IR.sparrowv.Alloc(lhs_r, allocSize_r);
            ret.add(allocToReg);
        }

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Print n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String print_name = n.f2.f0.tokenImage;

        Register print_r;
        if (c.getVarLocation(print_name).equals("stack")) {
            print_r = new Register("s10");
            Identifier print_id = new Identifier(print_name);
            Move_Reg_Id loadPrintToReg = new Move_Reg_Id(print_r, print_id);
            ret.add(loadPrintToReg);
        } else {
            String registerId = c.getRegisterId(print_name);
            print_r = new Register(registerId);
        }

        cs132.IR.sparrowv.Print printReg = new cs132.IR.sparrowv.Print(print_r);
        ret.add(printReg);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(ErrorMessage n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String errorMessage = n.f2.f0.tokenImage;
        cs132.IR.sparrowv.ErrorMessage myErrorMessage = new cs132.IR.sparrowv.ErrorMessage(errorMessage);
        ret.add(myErrorMessage);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Goto n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String labelName = n.f1.f0.tokenImage;
        Label myLabel = new Label(labelName);

        cs132.IR.sparrowv.Goto myGoto = new cs132.IR.sparrowv.Goto(myLabel);
        ret.add(myGoto);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(IfGoto n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String bool_name = n.f1.f0.tokenImage;
        String labelName = n.f3.f0.tokenImage;
        Label myLabel = new Label(labelName);

        Register bool_r;
        if (c.getVarLocation(bool_name).equals("stack")) {
            bool_r = new Register("s10");
            Identifier bool_id = new Identifier(bool_name);
            Move_Reg_Id loadBool = new Move_Reg_Id(bool_r, bool_id);
            ret.add(loadBool);
        } else {
            String registerId = c.getRegisterId(bool_name);
            bool_r = new Register(registerId);
        }

        cs132.IR.sparrowv.IfGoto myIfGoto = new cs132.IR.sparrowv.IfGoto(bool_r, myLabel);
        ret.add(myIfGoto);

        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(Call n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        String lhs_name = n.f0.f0.tokenImage;
        String func_name = n.f3.f0.tokenImage;

        Register temp_lhs_r = new Register("s10");
        Register temp_func_r = new Register("s11");

        if (c.getVarLocation(func_name).equals("stack")) {
            Identifier func_id = new Identifier(func_name);
            Move_Reg_Id loadFunc = new Move_Reg_Id(temp_func_r, func_id);
            ret.add(loadFunc);
        } else {
            String registerId = c.getRegisterId(func_name);
            Register func_r = new Register(registerId);
            Move_Reg_Reg loadFunc = new Move_Reg_Reg(temp_func_r, func_r);
            ret.add(loadFunc);
        }

        int numParams = n.f5.nodes.size();

        //save all a registers
        ArrayList<String> a_active_across_call = c.a_activeVariables();
        ArrayList<Instruction> a_saveRegisters = c.a_saveRegisters(a_active_across_call);
        ret.addAll(a_saveRegisters);

        ArrayList<Identifier> params = new ArrayList<>();
        int a_index = 2;
        for (Node node: n.f5.nodes) {
            if (a_index > 7) {
                cs132.IR.syntaxtree.Identifier param_id = (cs132.IR.syntaxtree.Identifier)node;
                String param_name = param_id.f0.tokenImage;
                Identifier stack_param = new Identifier(param_name);
                params.add(stack_param);
                if (c.getVarLocation(param_name).equals("stack")) {

                } else {
                    String registerId = c.getRegisterId(param_name);
                    Register s_reg = new Register(registerId);
                    Move_Id_Reg loadStackArgument = new Move_Id_Reg(stack_param, s_reg);
                    ret.add(loadStackArgument);
                }
            }

            a_index += 1;
        }

        a_index = 2;
        for (Node node: n.f5.nodes) {
            cs132.IR.syntaxtree.Identifier param_id = (cs132.IR.syntaxtree.Identifier)node;
            String param_name = param_id.f0.tokenImage;

            if (a_index <= 7) {
                Register a_register = new Register("a"+a_index);
                if (c.getVarLocation(param_name).equals("stack")) {
                    Identifier stack_param = new Identifier(param_name);
                    Move_Reg_Id a_loadStackToRegister = new Move_Reg_Id(a_register, stack_param);
                    ret.add(a_loadStackToRegister);
                } else {
                    String registerId = c.getRegisterId(param_name);
                    Register s_register = new Register(registerId);
                    Move_Reg_Reg loadRegArgument = new Move_Reg_Reg(a_register, s_register);
                    ret.add(loadRegArgument);
                }
            }

            a_index += 1;
        }

        ArrayList<String> t_active_across_call = c.t_activeVariables();
        ArrayList<Instruction> t_saveRegisters = c.t_saveRegisters(t_active_across_call);
        ret.addAll(t_saveRegisters);


        cs132.IR.sparrowv.Call callFunc = new cs132.IR.sparrowv.Call(temp_lhs_r, temp_func_r, params);
        ret.add(callFunc);

        //restore all a registers
        ArrayList<Instruction> a_restoreRegisters = c.a_restoreRegisters(a_active_across_call);
        ret.addAll(a_restoreRegisters);
        ArrayList<Instruction> t_restoreRegisters = c.t_restoreRegisters(t_active_across_call);
        ret.addAll(t_restoreRegisters);


        ret.addAll(c.defineVariable(lhs_name));
        if (c.getVarLocation(lhs_name).equals("stack")) {
            Identifier lhs_id = new Identifier(lhs_name);
            Move_Id_Reg saveToStack = new Move_Id_Reg(lhs_id, temp_lhs_r);
            ret.add(saveToStack);
        } else {
            String registerId = c.getRegisterId(lhs_name);
            Register lhs_r = new Register(registerId);
            Move_Reg_Reg updateRegister = new Move_Reg_Reg(lhs_r, temp_lhs_r);
            ret.add(updateRegister);
        }


        return ret;

    }
}