package Support;

import cs132.IR.syntaxtree.*;
import cs132.IR.visitor.GJVoidDepthFirst;

public class SCCVisitor extends GJVoidDepthFirst<Context> {

    @Override
    //f0 = Instructions
    //f2 = return Identifier
    public void visit(Block n, Context c) {
        c.resetDfn();
        for (Node node: n.f0.nodes) {
            node.accept(this, c);
            c.incrementDfn();
        }
    }

    @Override
    //f0 = Label
    public void visit(LabelWithColon n, Context c) {
        String labelName = n.f0.f0.tokenImage;
        int dfn = c.getDfn();
        c.newSCC(labelName, dfn);
    }

    @Override
    //f1 = Label
    public void visit(Goto n, Context c) {
        String target = n.f1.f0.tokenImage;
        int dfn = c.getDfn();
        c.endSCC(target, dfn);
    }

    @Override
    //f3 = Label
    public void visit(IfGoto n, Context c) {
        String target = n.f3.f0.tokenImage;
        int dfn = c.getDfn();
        c.endSCC(target, dfn);
    }
}