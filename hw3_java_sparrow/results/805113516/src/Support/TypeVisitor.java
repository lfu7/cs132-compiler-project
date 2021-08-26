package Support;

import cs132.minijava.syntaxtree.*;
import cs132.IR.sparrow.*;
import cs132.IR.sparrow.Block;
import cs132.IR.token.FunctionName;
import cs132.IR.token.Identifier;
import cs132.minijava.visitor.GJDepthFirst;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TypeVisitor extends GJDepthFirst<ArrayList<FunctionDecl>, Context> {


    //f0 = "class"
    //f1 = Identifier
    //f2 = "{"
    //f3 = "public"
    //f4 = "static"
    //f5 = "void"
    //f6 = "main"
    //f7 = "("
    //f8 = "String"
    //f9 = "["
    //f10 = "]"
    //f11 = Identifier
    //f12 = ")"
    //f13 = "{"
    //f14 = VarDeclaration()
    //f15 = Statement()
    //f16 = "}"
    //f17 = "}"
    @Override
    public ArrayList<FunctionDecl> visit(MainClass n, Context c) {

        String className = n.f1.f0.tokenImage;
        c.setCurrClass(className);

        String methodName = "Main";
        c.setCurrMethod(methodName);


        //get the name of the class;
        FunctionName functionName = new FunctionName("_" + methodName);

        //parameters empty for main
        List<Identifier> params = new ArrayList<>();

        //parse block
        List<Instruction> instructions = new ArrayList<>();
        StatementVisitor sv = new StatementVisitor();
        for (Node node: n.f15.nodes) {
            ArrayList<Instruction> temp =  node.accept(sv, c);
            instructions.addAll(temp);
        }



        Identifier id = new cs132.IR.token.Identifier("v0");
        Block block = new Block(instructions, id);
        FunctionDecl mainfunc = new FunctionDecl(functionName, params, block);


        c.exitMethod();
        c.exitClass();

        ArrayList<FunctionDecl> ret = new ArrayList<>();
        ret.add(mainfunc);

        c.exitMethod();
        c.exitClass();
        c.resetCounter();
        return ret;
    }



    @Override
    public ArrayList<FunctionDecl> visit(TypeDeclaration n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    //f0 = "class"
    //f1 = Identifier
    //f2 = "{"
    //f3 = Variable Declarations
    //f4 = Method Declarations
    //f5 - "}"
    public ArrayList<FunctionDecl> visit(ClassDeclaration n, Context c) {

        String f1 = n.f1.f0.tokenImage;
        c.setCurrClass(f1);

        ArrayList<FunctionDecl> methods = new ArrayList<>();
        MethodVisitor mv = new MethodVisitor();
        for (Node node: n.f4.nodes) {
            FunctionDecl temp = node.accept(mv, c);
            methods.add(temp);
        }

        c.exitClass();

        return methods;
    }

    @Override
    //f0 = "class"
    //f1 = Identifier
    //f2 = "extends"
    //f3 = Identifier
    //f4 = "{"
    //f5 = Variable Declarations
    //f6 = Method Declarations
    //f7 = "}"
    public ArrayList<FunctionDecl> visit(ClassExtendsDeclaration n, Context c) {

        String f1 = n.f1.f0.tokenImage;
        c.setCurrClass(f1);

        ArrayList<FunctionDecl> methods = new ArrayList<>();
        MethodVisitor mv = new MethodVisitor();
        for (Node node: n.f6.nodes) {
            FunctionDecl temp = node.accept(mv, c);
            methods.add(temp);
        }

        c.exitClass();
        return methods;
    }
}