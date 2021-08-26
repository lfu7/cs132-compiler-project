package Support;

import cs132.IR.sparrowv.FunctionDecl;
import cs132.IR.sparrowv.Program;
import cs132.IR.visitor.GJDepthFirst;
import cs132.IR.syntaxtree.Node;

import java.util.ArrayList;

public class GoalVisitor extends GJDepthFirst<Program, Context> {

    @Override
    public Program visit(cs132.IR.syntaxtree.Program n, Context c) {
        ArrayList<FunctionDecl> functions = new ArrayList<>();
        FunctionVisitor fv = new FunctionVisitor();
        for (Node node: n.f0.nodes) {
            cs132.IR.sparrowv.FunctionDecl temp = node.accept(fv, c);
            functions.add(temp);
        }
        Program p = new Program(functions);
        return p;
    }
}