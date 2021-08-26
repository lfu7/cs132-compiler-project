package Support;

import cs132.IR.sparrowv.*;
import cs132.IR.sparrowv.visitor.DepthFirst;
import cs132.IR.token.Identifier;

public class ContextBuilder extends DepthFirst {

    private Context c;
    public ContextBuilder(Context c) {
        this.c = c;
    }

    @Override
    public void visit(Program n) {

        int index = 0;
        for (FunctionDecl f: n.funDecls) {
            if (index == 0) {
                String funcName = f.functionName.name;
                c.setMainFuncName(funcName);
            }
            f.accept(this);
            index += 1;
        }
    }

    @Override
    public void visit(FunctionDecl n) {
        String funcName = n.functionName.name;
        c.newMethod(funcName);
        c.setCurrMethod(funcName);

        for (Identifier id : n.formalParameters) {
            c.addParam(id.toString());
        }

        n.block.accept(this);
    }

    @Override
    public void visit(Block n) {
        for (Instruction i : n.instructions) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Move_Id_Reg n) {
        String stackVarName = n.lhs.toString();
        if (!c.checkExists(stackVarName)) {
            c.addLocal(stackVarName);
        }
    }

    @Override
    public void visit(Call n) {
        for (Identifier i : n.args) {
            c.addArgOnStack();
        }
    }
}