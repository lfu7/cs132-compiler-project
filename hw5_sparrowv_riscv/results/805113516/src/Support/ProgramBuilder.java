package Support;

import cs132.IR.sparrowv.*;
import cs132.IR.sparrowv.visitor.DepthFirst;
import cs132.IR.token.Identifier;

public class ProgramBuilder extends DepthFirst {

    private Context c;

    public ProgramBuilder(Context c) {
        this.c = c;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //OVERRIDE IMPLEMENTATIONS///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void visit(Program n) {
        printEquiv();
        printText();

        for (FunctionDecl fd: n.funDecls) {
            fd.accept(this);
        }

        printError();
        printAlloc();
        printData();
        printNullErrorString();
        printBoundsErrorString();
    }

    @Override
    public void visit(FunctionDecl n) {
        String funcName = n.functionName.name;
        c.setCurrMethod(funcName);

        System.out.println(".globl " + funcName);
        System.out.println(funcName + ":");

        int numLocals = c.getNumLocals();
        int allocSize = 4*numLocals + 8;

        ///////////////////////////////////////////////////////
        //CALLEE PROLOGUE//////////////////////////////////////
        ///////////////////////////////////////////////////////
        System.out.println("sw fp, -8(sp)"); //store word
        System.out.println("mv fp, sp"); //move
        System.out.println("li t6, " + allocSize); //load immediate
        System.out.println("sub sp, sp, t6"); //subtract, store in sp
        System.out.println("sw ra, -4(fp)"); //store return address

        ////////////////////////////////////////////////////////
        //VISIT BLOCK///////////////////////////////////////////
        ////////////////////////////////////////////////////////
        n.block.accept(this);

        //return should be stored in stack, taken care of visit block

        ////////////////////////////////////////////////////////
        //CALLEE EPILOGUE///////////////////////////////////////
        ////////////////////////////////////////////////////////
        System.out.println("lw ra, -4(fp)"); //restore return address
        System.out.println("lw fp, -8(fp)"); //restore frame pointer
        System.out.println("addi sp, sp, " + allocSize); //restore old stack pointer initial alloc size

        /*
        int numArgs = c.getNumArgsOnStack();
        int argsSpace = 4*numArgs;
        System.out.println("addi sp, sp, " + argsSpace); //restore old stack pointer initial alloc size
        */

        System.out.println("jr ra");
        System.out.println();
    }

    @Override
    public void visit(Block n) {
        for (Instruction i: n.instructions) {
            i.accept(this);
        }

        String returnId = n.return_id.toString();
        int retOffset;

        if (c.getVarType(returnId).equals("local")) {
            retOffset = c.getLocalOffset(returnId);
            System.out.println("lw a0, -"+ retOffset + "(fp)");
        } else if (c.getVarType(returnId).equals("param")) {
            retOffset = c.getParamOffset(returnId);
            System.out.println("lw a0, "+ retOffset + "(fp)");
        }
    }

    @Override
    public void visit(LabelInstr n) {
        String labelName = n.label.toString();
        String methodLabelName = c.getLabelName(labelName);
        System.out.println(methodLabelName + ":");
    }

    @Override
    public void visit(Move_Reg_Integer n) {
        String registerName = n.lhs.toString();
        int val = n.rhs;
        System.out.println("li " + registerName + ", " + val);
    }

    @Override
    public void visit(Move_Reg_FuncName n) {
        String registerName = n.lhs.toString();
        String funcName = n.rhs.name;
        System.out.println("la " + registerName + ", " + funcName);
    }

    @Override
    public void visit(Add n) {
        String register_lhs = n.lhs.toString();
        String register_rhs1 = n.arg1.toString();
        String register_rhs2 = n.arg2.toString();

        System.out.println("add " + register_lhs + ", " + register_rhs1 + ", " + register_rhs2);
    }

    @Override
    public void visit(Subtract n) {
        String register_lhs = n.lhs.toString();
        String register_rhs1 = n.arg1.toString();
        String register_rhs2 = n.arg2.toString();

        System.out.println("sub " + register_lhs + ", " + register_rhs1 + ", " + register_rhs2);
    }

    @Override
    public void visit(Multiply n) {
        String register_lhs = n.lhs.toString();
        String register_rhs1 = n.arg1.toString();
        String register_rhs2 = n.arg2.toString();

        System.out.println("mul " + register_lhs + ", " + register_rhs1 + ", " + register_rhs2);
    }

    @Override
    public void visit(LessThan n) {
        String register_lhs = n.lhs.toString();
        String register_rhs1 = n.arg1.toString();
        String register_rhs2 = n.arg2.toString();

        System.out.println("slt " + register_lhs + ", " + register_rhs1 + ", " + register_rhs2);
    }

    @Override
    public void visit(Load n) {
        String register_lhs = n.lhs.toString();
        String register_base = n.base.toString();
        int offset = n.offset;

        System.out.println("lw " + register_lhs + ", " + offset + "(" + register_base + ")");
    }

    @Override
    public void visit(Store n) {
        String register_base = n.base.toString();
        String register_rhs = n.rhs.toString();
        int offset = n.offset;
        System.out.println("sw " + register_rhs + ", " + offset + "(" + register_base + ")");

    }

    @Override
    public void visit(Move_Reg_Reg n) {
        String register_lhs = n.lhs.toString();
        String register_rhs = n.rhs.toString();

        System.out.println("mv " + register_lhs + ", " + register_rhs);
    }

    @Override
    public void visit(Move_Id_Reg n) {
        String register_rhs = n.rhs.toString();
        String varName = n.lhs.toString();

        if (c.getVarType(varName).equals("local")) {
            int localOffset = c.getLocalOffset(varName);
            System.out.println("sw " + register_rhs + ", -" + localOffset + "(fp)");
        } else if (c.getVarType(varName).equals("param")) {
            int paramOffset = c.getParamOffset(varName);
            System.out.println("sw " + register_rhs + ", " + paramOffset + "(fp)");
        }

    }

    @Override
    public void visit(Move_Reg_Id n) {
        String register_lhs = n.lhs.toString();
        String varName = n.rhs.toString();

        if (c.getVarType(varName).equals("local")) {
            int localOffset = c.getLocalOffset(varName);
            System.out.println("lw " + register_lhs + ", -" + localOffset + "(fp)");
        } else if (c.getVarType(varName).equals("param")) {
            int paramOffset = c.getParamOffset(varName);
            System.out.println("lw " + register_lhs + ", " + paramOffset + "(fp)");
        }
    }

    @Override
    public void visit(Alloc n) {
        String register_lhs = n.lhs.toString();
        String register_size = n.size.toString();

        System.out.println("mv a0, " + register_size);
        System.out.println("jal alloc");
        System.out.println("mv " + register_lhs + ", a0");
    }

    @Override
    public void visit(Print n) {
        String register_print = n.content.toString();
        System.out.println("mv a1, " + register_print);
        System.out.println("li a0, @print_int");
        System.out.println("ecall");

        //print new line
        System.out.println("li a1, 10");
        System.out.println("li a0, 11");
        System.out.println("ecall");
    }

    @Override
    public void visit(ErrorMessage n) {
        String errorMessage = n.msg;
        if (errorMessage.equals("\"null pointer\"")) {
            System.out.println("la a0, msg_0");
        } else if (errorMessage.equals("\"array index out of bounds\"")) {
            System.out.println("la a0, msg_1");
        } else {
            //System.out.println(errorMessage);
        }
        System.out.println("j error");
    }


    @Override
    public void visit(Goto n) {
        String labelName = n.label.toString();
        String methodLabelName = c.getLabelName(labelName);
        System.out.println("j " + methodLabelName);
    }

    @Override
    public void visit(IfGoto n) {
        String register_bool = n.condition.toString();
        String labelName = n.label.toString();
        String methodLabelName = c.getLabelName(labelName);

        int endCounter = c.getEndCounter();
        c.incrementEndCounter();
        String endLabel = c.getLabelName("end_"+endCounter);
        System.out.println("bne " + register_bool + ", zero ," + endLabel);
        System.out.println("j " + methodLabelName);
        System.out.println(endLabel + ":");
    }

    @Override
    public void visit(Call n) {
        //allocate space for params of next block....
        int numArgs = n.args.size();

        if (numArgs > 0) {
            int allocSize = 4*numArgs;
            System.out.println("li t6, " + allocSize);
            System.out.println("sub sp, sp, t6");
        }

        int offset = 0;
        for (Identifier arg: n.args) {
            String argName = arg.toString();
            if (c.getVarType(argName).equals("local")) {
                int localOffset = c.getLocalOffset(argName);
                System.out.println("lw t6, -" + localOffset + "(fp)");
                System.out.println("sw t6, " + offset + "(sp)");
            } else if (c.getVarType(argName).equals("param"))  {
                int paramOffset = c.getParamOffset(argName);
                System.out.println("lw t6, " + paramOffset + "(fp)");
                System.out.println("sw t6, " + offset + "(sp)");
            }
            offset += 4;
        }

        String register_callee = n.callee.toString();
        System.out.println("jalr " + register_callee);

        String register_lhs = n.lhs.toString();
        System.out.println("mv " + register_lhs + ", a0");


        if (numArgs > 0) {
            int allocSize = 4*numArgs;
            System.out.println("li t6, " + allocSize);
            System.out.println("add sp, sp, t6");
        }


    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //PRINT HELPER///////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void printText() {
        System.out.println(".text");
        String mainFuncName = c.getMainFuncName();
        System.out.println("jal " + mainFuncName);
        System.out.println("li a0, @exit");
        System.out.println("ecall");
        System.out.println();
    }

    public void printEquiv() {
        System.out.println(".equiv @sbrk, 9");
        System.out.println(".equiv @print_string, 4");
        System.out.println(".equiv @print_char, 11");
        System.out.println(".equiv @print_int, 1");
        System.out.println(".equiv @exit 10");
        System.out.println(".equiv @exit2, 17");
        System.out.println();
    }

    public void printError() {
        System.out.println(".globl error");
        System.out.println("error:");
        System.out.println("mv a1, a0                                # Move msg address to a1");
        System.out.println("li a0, @print_string                     # Code for print_string ecall");
        System.out.println("ecall # Print error message in a1");
        System.out.println("li a1, 10                                # Load newline character");
        System.out.println("li a0, @print_char                       # Code for print_char ecall");
        System.out.println("ecall                                    # Print newline");
        System.out.println("li a0, @exit                             # Code for exit ecall");
        System.out.println("ecall                                    # Exit with code");
        System.out.println("abort_17:                                  # Infinite loop");
        System.out.println("j abort_17                               # Prevent fallthrough");
        System.out.println();
    }

    public void printAlloc() {
        System.out.println(".globl alloc");
        System.out.println("alloc:");
        System.out.println("mv a1, a0                                # Move requested size to a1");
        System.out.println("li a0, @sbrk                             # Code for ecall: sbrk");
        System.out.println("ecall                                    # Request a1 bytes");
        System.out.println("jr ra                                    # Return to caller");
        System.out.println();
    }

    public void printData() {
        System.out.println(".data");
        System.out.println();
    }

    public void printNullErrorString() {
        System.out.println(".globl msg_0");
        System.out.println("msg_0:");
        System.out.println(".asciiz \"null pointer\"");
        System.out.println(".align 2");
        System.out.println();
    }

    public void printBoundsErrorString() {
        System.out.println(".globl msg_1");
        System.out.println("msg_1:");
        System.out.println(".asciiz \"array index out of bounds\"");
        System.out.println(".align 2");
        System.out.println();
    }

}