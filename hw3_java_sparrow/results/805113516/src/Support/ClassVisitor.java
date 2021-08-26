package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.DepthFirstVisitor;

import java.util.Enumeration;

public class ClassVisitor extends DepthFirstVisitor {

    private static Context my_context;
    public ClassVisitor(Context c) {
        this.my_context = c;
    }

    @Override
    public void visit(Goal n) {

        n.f0.accept(this);
        //process only the classes that the user seeks to define
        for (Node node: n.f1.nodes) {
            node.accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {
        String mainClassName = n.f1.f0.tokenImage;

        my_context.addClass(mainClassName);
        my_context.setCurrClass(mainClassName);
        my_context.addMethod("Main", "");
        my_context.setCurrMethod("Main");

        //avoid nodelistoptional problems
        for (Node node: n.f14.nodes) {
            node.accept(this);
        }


        my_context.exitMethod();
        my_context.exitClass();
    }

    @Override
    public void visit(TypeDeclaration n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = "class"
    //f1 = Identifier
    //f2 = "{"
    //f3 = Variable Declarations
    //f4 = Method Declarations
    //f5 - "}"
    public void visit(ClassDeclaration n) {
        //System.out.println("class declaration");
        String className = n.f1.f0.tokenImage;
        my_context.addClass(className);
        my_context.setCurrClass(className);

        //local fields
        for (Node node: n.f3.nodes) {
            node.accept(this);
        }

        //method declarations
        for (Node node: n.f4.nodes) {
            node.accept(this);
        }

        my_context.exitClass();
    }

    @Override

    //f0 = "class"
    //f1 = Identifier
    //f2 = "extends"
    //f3 = Identifier
    //f4 = "{"
    //f5 = Variable Declarations
    //f6 = Statements
    //f7 = "}"

    //does NOT DO subtypes yet
    public void visit(ClassExtendsDeclaration n) {
        String subClassName = n.f1.f0.tokenImage;
        //my_context.addClass(subClassName);
        //my_context.setCurrClass(subClassName);

        String superClassName = n.f3.f0.tokenImage;
        my_context.setParent(subClassName, superClassName);
        my_context.setCurrClass(subClassName);

        //local fields
        for (Node node: n.f5.nodes) {
            node.accept(this);
        }

        //method declarations
        for (Node node: n.f6.nodes) {
            node.accept(this);
        }

        my_context.exitClass();
    }

    @Override
    public void visit(MethodDeclaration n) {
        //System.out.println("method declaration");

        TypeGetter tg = new TypeGetter();
        String returntype = n.f1.accept(tg);
        String methodname = n.f2.f0.tokenImage;

        my_context.addMethod(methodname, returntype);
        my_context.setCurrMethod(methodname);

        //process the parameters
        n.f4.accept(this);

        //no need to process local variables yet...
        for (Node node: n.f7.nodes) {
            //System.out.println("local");
            node.accept(this);
        }
        my_context.exitMethod();
    }

    @Override
    public void visit(NodeOptional n) {
        //System.out.println("node optional");
        if (n.present()) {
            n.node.accept(this);
        }
    }

    //f0 = Formal Parameter
    //f1 = List of Formal Parameter Rests
    @Override
    public void visit(FormalParameterList n) {
        //System.out.println("formal parameter list");

        n.f0.accept(this);

        //parse the formal parameter rests
        for (Node node: n.f1.nodes) {
            node.accept(this);
        }
    }

    //f0 = Type
    //f1 = Identifier
    @Override
    public void visit(FormalParameter n) {
        //System.out.println("formal parameter");

        TypeGetter tg = new TypeGetter();
        String paramtype = n.f0.accept(tg);
        String paramname = n.f1.f0.tokenImage;

        my_context.addParam(paramname, paramtype);
    }

    @Override
    //f1 = Formal Parameter
    public void visit(FormalParameterRest n) {
        n.f1.accept(this);
    }


    @Override
    //f0 = Type
    //f1 = Identifier
    public void visit(VarDeclaration n) {

        TypeGetter tg = new TypeGetter();
        String vartype = n.f0.accept(tg);

        String varname = n.f1.f0.tokenImage;

        if (my_context.inMethod()) {
            my_context.addLocal(varname, vartype);
        } else {
            my_context.addField(varname, vartype);
        }
    }

}