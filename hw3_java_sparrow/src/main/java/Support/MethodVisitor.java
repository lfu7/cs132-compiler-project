package Support;
import cs132.IR.token.Identifier;
import cs132.minijava.syntaxtree.*;
import cs132.IR.sparrow.*;
import cs132.IR.token.*;
import cs132.minijava.visitor.GJDepthFirst;

import cs132.IR.sparrow.Block;
import java.util.ArrayList;
import java.util.List;

public class MethodVisitor extends GJDepthFirst<FunctionDecl, Context> {

    @Override
    //f0 = "public"
    //f1 = Type
    //f2 = Identifier
    //f3 = "("
    //f4 = Formal Parameters
    //f5 = ")"
    //f6 = "{"
    //f7 = Variable Declarations
    //f8 = Statements
    //f9 = "return"
    //f10 = Expression (return value)
    //f10 = ";"
    //f11 = "}";
    public FunctionDecl visit(MethodDeclaration n, Context c) {

        //////////////////////////////////////////////////////
        //NAME////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        String f2 = n.f2.f0.tokenImage;
        c.setCurrMethod(f2);

        TypeClass currClass = c.getCurrClass();
        String className = currClass.getName();

        FunctionName functionName = new FunctionName(className + "_" + f2);


        //////////////////////////////////////////////////////
        //PARAMETERS//////////////////////////////////////////
        //////////////////////////////////////////////////////
        ArrayList<String> _paramNames = c.getCurrMethodParams();
        ArrayList<Identifier> paramNames= new ArrayList<>();

        Identifier this_iden = new Identifier("this");
        paramNames.add(this_iden);
        for (String s: _paramNames) {
            Identifier temp = new Identifier(s);
            paramNames.add(temp);
        }

        //////////////////////////////////////////////////////
        //BLOCK///////////////////////////////////////////////
        //////////////////////////////////////////////////////
        List<Instruction> instructions = new ArrayList<>();
        StatementVisitor sv = new StatementVisitor();
        for (Node node: n.f8.nodes) {
            ArrayList<Instruction> temp =  node.accept(sv, c);
            instructions.addAll(temp);
        }

        //////////////////////////////////////////////////////
        //RETURN//////////////////////////////////////////////
        //////////////////////////////////////////////////////
        int retVal = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(retVal);
        ArrayList<Instruction> temp = n.f10.accept(sv, c);
        instructions.addAll(temp);
        Identifier retIden =  new Identifier("v"+retVal);


        //////////////////////////////////////////////////////
        //CLEANUP/////////////////////////////////////////////
        //////////////////////////////////////////////////////
        Block retBlock = new Block(instructions, retIden);
        FunctionDecl ret = new FunctionDecl(functionName, paramNames, retBlock);
        c.exitMethod();
        c.resetCounter();
        return ret;

    }
}
