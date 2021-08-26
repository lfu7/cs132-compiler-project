package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.DepthFirstVisitor;

import cs132.IR.sparrow.*;
import java.util.ArrayList;


public class GoalVisitor extends DepthFirstVisitor {

    private Program sparrowProgram;
    private Context my_context;

    public GoalVisitor(Context c) {
        my_context = c;
    }

    @Override
    public void visit(Goal n) {
        ArrayList<FunctionDecl> functionDecls = new ArrayList<>();

        TypeVisitor tv = new TypeVisitor();

        ArrayList<FunctionDecl> functions = n.f0.accept(tv, my_context);

        for (Node node: n.f1.nodes) {
            ArrayList<FunctionDecl> temp = node.accept(tv, my_context);

            if (temp != null) {
                functions.addAll(temp);
            }
        }

        this.sparrowProgram = new Program(functions);
    }

    public Program getProgram() {
        return this.sparrowProgram;
    }
}
