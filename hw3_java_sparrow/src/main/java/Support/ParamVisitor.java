package Support;


import cs132.minijava.syntaxtree.Expression;
import cs132.minijava.syntaxtree.ExpressionList;
import cs132.minijava.syntaxtree.ExpressionRest;
import cs132.minijava.syntaxtree.NodeOptional;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.visitor.GJDepthFirst;

import cs132.IR.sparrow.Instruction;
import cs132.IR.token.Identifier;

import java.util.ArrayList;

public class ParamVisitor extends GJDepthFirst<ParamWrapper, Context> {

    @Override
    public ParamWrapper visit(NodeOptional n, Context c) {
        ParamWrapper w = new ParamWrapper();
        return n.present() ? n.node.accept(this, c) : w;
    }

    @Override
    public ParamWrapper visit(ExpressionList n, Context c) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<Identifier> identifiers = new ArrayList<>();

        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);

        StatementVisitor sv = new StatementVisitor();
        ArrayList<Instruction> ret1 = n.f0.accept(sv, c);
        instructions.addAll(ret1);

        Identifier id1 = new Identifier("v"+i1);
        identifiers.add(id1);

        for (Node node: n.f1.nodes) {
            int i = c.getLocalCounter();
            c.incrementCounter();
            c.pushStack(i);

            ArrayList<Instruction> ret_i = node.accept(sv,c);
            instructions.addAll(ret_i);

            Identifier id_i = new Identifier("v"+i);
            identifiers.add(id_i);
        }

        ParamWrapper ret = new ParamWrapper();
        ret.setInstructions(instructions);
        ret.setIdentifiers(identifiers);

        return ret;
    }
}