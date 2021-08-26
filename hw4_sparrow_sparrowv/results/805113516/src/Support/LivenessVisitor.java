package Support;

import cs132.IR.syntaxtree.*;
import cs132.IR.visitor.GJVoidDepthFirst;

public class LivenessVisitor extends GJVoidDepthFirst<Context> {

    @Override
    //f0 = Instructions
    //f2 = return Identifier
    public void visit(Block n, Context c) {
        c.resetDfn();
        for (Node node: n.f0.nodes) {
            node.accept(this, c);
            c.incrementDfn();
        }

        int lineNumber = c.getDfn();
        String retID = n.f2.f0.tokenImage;
        c.assignUse(retID, lineNumber);
    }

    @Override
    public void visit(cs132.IR.syntaxtree.Instruction n, Context c) {
        n.f0.accept(this, c);
    }

    @Override
    //f0 = Identifier
    public void visit(SetInteger n, Context c) {
        int lineNumber = c.getDfn();
        String varName = n.f0.f0.tokenImage;
        c.assignDef(varName, lineNumber);
    }

    @Override
    //f0 = Identifier
    public void visit(SetFuncName n, Context c) {
        int lineNumber = c.getDfn();
        String varName = n.f0.f0.tokenImage;
        c.assignDef(varName, lineNumber);
    }

    @Override
    //f0 = LHS
    //f2 = RHS1
    //f4 = RHS2
    public void visit(Add n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String rhs1 = n.f2.f0.tokenImage;
        c.assignUse(rhs1, lineNumber);

        String rhs2 = n.f4.f0.tokenImage;
        c.assignUse(rhs2, lineNumber);
    }

    @Override
    //f0 = LHS
    //f2 = RHS1
    //f4 = RHS2
    public void visit(Subtract n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String rhs1 = n.f2.f0.tokenImage;
        c.assignUse(rhs1, lineNumber);

        String rhs2 = n.f4.f0.tokenImage;
        c.assignUse(rhs2, lineNumber);
    }

    @Override
    //f0 = LHS
    //f2 = RHS1
    //f4 = RHS2
    public void visit(Multiply n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String rhs1 = n.f2.f0.tokenImage;
        c.assignUse(rhs1, lineNumber);

        String rhs2 = n.f4.f0.tokenImage;
        c.assignUse(rhs2, lineNumber);
    }

    @Override
    //f0 = LHS
    //f2 = RHS1
    //f4 = RHS2
    public void visit(LessThan n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String rhs1 = n.f2.f0.tokenImage;
        c.assignUse(rhs1, lineNumber);

        String rhs2 = n.f4.f0.tokenImage;
        c.assignUse(rhs2, lineNumber);
    }

    @Override
    //f0 = LHS
    //f3 = Base
    public void visit(Load n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String base = n.f3.f0.tokenImage;
        c.assignUse(base, lineNumber);
    }

    @Override
    //f1 = Base
    //f6 = RHS
    public void visit(Store n, Context c) {
        int lineNumber = c.getDfn();
        String base = n.f1.f0.tokenImage;
        c.assignUse(base, lineNumber);

        String rhs = n.f6.f0.tokenImage;
        c.assignUse(rhs, lineNumber);
    }

    @Override
    //f0 = LHS
    //f2 = RHS
    public void visit(Move n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String rhs = n.f2.f0.tokenImage;
        c.assignUse(rhs, lineNumber);
    }

    @Override
    //f0 = LHS
    //f4 = Alloc Size
    public void visit(Alloc n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String allocSize = n.f4.f0.tokenImage;
        c.assignUse(allocSize, lineNumber);
    }

    @Override
    //f2 = print id
    public void visit(Print n, Context c) {
        int lineNumber = c.getDfn();
        String printID = n.f2.f0.tokenImage;
        c.assignUse(printID, lineNumber);
    }

    @Override
    //f1 = boolID
    public void visit(IfGoto n, Context c) {
        int lineNumber = c.getDfn();
        String boolID = n.f1.f0.tokenImage;
        c.assignUse(boolID, lineNumber);
    }

    @Override
    //f0 = LHS
    //f3 = funcName
    //f5 = List of Ids
    public void visit(Call n, Context c) {
        int lineNumber = c.getDfn();
        String lhs = n.f0.f0.tokenImage;
        c.assignDef(lhs, lineNumber);

        String funcName = n.f3.f0.tokenImage;
        c.assignUse(funcName, lineNumber);

        for (Node node : n.f5.nodes) {
            node.accept(this, c);
        }
    }

    @Override
    public void visit(Identifier n, Context c) {
        int lineNumber = c.getDfn();
        String idName = n.f0.tokenImage;
        c.assignUse(idName, lineNumber);
    }


}