package Support;

import cs132.IR.sparrowv.Block;
import cs132.IR.sparrowv.FunctionDecl;
import cs132.IR.sparrowv.Instruction;
import cs132.IR.syntaxtree.FunctionDeclaration;
import cs132.IR.syntaxtree.Node;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Identifier;
import cs132.IR.visitor.GJDepthFirst;

import java.util.ArrayList;

public class FunctionVisitor extends GJDepthFirst<FunctionDecl, Context> {

    @Override
    public FunctionDecl visit(FunctionDeclaration n, Context c) {
        c.resetContext();
        String functionName = n.f1.f0.tokenImage;
        if (functionName.equals("Main")) {
            c.setSaveCalleeRegisters(false);
        } else {
            c.setSaveCalleeRegisters(true);
        }

        c.addMethod(functionName);
        c.setCurrMethod(functionName);

        FunctionName retName = new FunctionName(functionName);

        //for now keep the params the same...
        ArrayList<Identifier> retParams = new ArrayList<>();
        int numParams = 0;

        for (Node node: n.f3.nodes) {
            cs132.IR.syntaxtree.Identifier param_tree_id = (cs132.IR.syntaxtree.Identifier)node;
            String param_name = param_tree_id.f0.tokenImage;
            c.assignDef(param_name, 0);

            if (numParams < 6) {
                int registerNumber = c.a_getNext();
                c.a_assignRegister(param_name, registerNumber);
            } else {
                Identifier param_id = new Identifier(param_name);
                retParams.add(param_id);
                c.assignStack(param_name);
            }

            numParams += 1;
        }

        //get SCCs

        //System.out.println("----------------------------------------------------------");
        //System.out.println(functionName);
        //System.out.println("----------------------------------------------------------");

        SCCVisitor sv = new SCCVisitor();
        n.f5.accept(sv,c);
        c.cleanSCC();
        //c.printSCC();

        //get liveness of every variable
        LivenessVisitor lv = new LivenessVisitor();
        n.f5.accept(lv, c);
        c.cleanLiveness();
        c.extendLiveness();
        //c.printLiveness();


        //visit the block of instructions
        BlockVisitor bv = new BlockVisitor();
        Block retBlock = n.f5.accept(bv, c);

        FunctionDecl ret = new FunctionDecl(retName, retParams, retBlock);
        return ret;
    }
}