package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.GJNoArguDepthFirst;

import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

public class ContextBuilder extends GJNoArguDepthFirst<Boolean> {

    private static Context my_context;
    
    public ContextBuilder(Context c) {
	this.my_context = c;
    }


    @Override
    public Boolean visit(Goal n) {
        boolean ret = n.f0.accept(this);

        for (Node node: n.f1.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        return ret;
    }

    @Override
    //add all fields to the main class
    //f1 = Identifier();
    //f14 = VarDeclaration();
    //f15 = Statement();
    public Boolean visit(MainClass n) {
        String mainClassName = n.f1.f0.tokenImage;
        my_context.setCurrClass(mainClassName);
        my_context.addMethod("Main", "");
        my_context.setCurrMethod("Main");
        Boolean ret = true;

        //avoid nodelistoptional problems
        for (Node node: n.f14.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }


        my_context.exitMethod();
        my_context.exitClass();

        return ret;
        //return super.visit(n);
    }

    @Override
    //f0 = class or class extends
    public Boolean visit(TypeDeclaration n) {
        return n.f0.accept(this);
    }


    @Override

    //f1 = Identifier
    //f3 = Variable Declarations
    //f4 = Method Declarations
    public Boolean visit(ClassDeclaration n) {
        //ystem.out.println("class declaration");
        String className = n.f1.f0.tokenImage;
        my_context.setCurrClass(className);
        Boolean ret = true;

        //local fields
        for (Node node: n.f3.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        //method declarations
        for (Node node: n.f4.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        my_context.exitMethod();
        my_context.exitClass();
        //super.visit(n);
        return ret;
    }

    //f1 = Identifier
    //f5 = Variable Declarations
    //f6 = Method Declarations
    @Override
    public Boolean visit(ClassExtendsDeclaration n) {

        //System.out.println("class extends declaration");
        String className = n.f1.f0.tokenImage;
        my_context.setCurrClass(className);


        Boolean ret = true;

        //local fields
        for (Node node: n.f5.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        //method declarations
        for (Node node: n.f6.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        my_context.exitMethod();
        my_context.exitClass();
        return ret;
    }

    //f1 = Type
    //f2 = Identifier
    //f4 = Formal Parameters
    //f7 = Variable Declarations
    //f8 = Statements
    //f10 = Expression (return value)
    @Override
    public Boolean visit(MethodDeclaration n) {
        //System.out.println("method declaration");

        TypeGetter tg = new TypeGetter();
        String returntype = n.f1.accept(tg);
        String methodname = n.f2.f0.tokenImage;

        TypeClass currClass = my_context.getCurrClass();

        if (currClass.checkMethodCurrClass(methodname)) {
            return false;
        }

        List<String> oldParamTypes = null;
        boolean methodExists = currClass.checkMethod(methodname);

        if (methodExists) {
            Method currMethod = currClass.getMethod(methodname);


            //make sure no overloading...
            String oldReturnType = currMethod.getReturnType();

            if (!oldReturnType.equals(returntype)) {
                return false;
            }


            oldParamTypes = currMethod.getParamTypes();
        }


        /*
        //checks for overloading
        if (currClass.checkMethod(methodname)) {
            return false;
        }
         */


        my_context.addMethod(methodname, returntype);
        my_context.setCurrMethod(methodname);

        Boolean ret = true;

        //parse formal parameters
        ret = ret && n.f4.accept(this);

        //check for overloading
        Method currMethod = currClass.getMethod(methodname);
        List<String> newParamTypes = currMethod.getParamTypes();
        if (methodExists && !oldParamTypes.equals(newParamTypes)) {
            return false;
        }

        //local variables
        for (Node node: n.f7.nodes) {
            //System.out.println("local");
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }

        return ret;
        //return super.visit(n);
    }

    @Override
    public Boolean visit(NodeOptional n) {
        //System.out.println("node optional");
        return n.present() ? n.node.accept(this) : true;
    }

    //f0 = Formal Parameter
    //f1 = List of Formal Parameter Rests
    @Override
    public Boolean visit(FormalParameterList n) {
        //System.out.println("formal parameter list");

        Boolean ret = n.f0.accept(this);

        //parse the formal parameter rests
        for (Node node: n.f1.nodes) {
            Boolean temp = node.accept(this);
            ret = ret && temp;
        }


        return ret;
        //return super.visit(n);
    }

    //f0 = Type
    //f1 = Identifier
    @Override
    public Boolean visit(FormalParameter n) {
        //System.out.println("formal parameter");

        TypeGetter tg = new TypeGetter();
        String paramtype = n.f0.accept(tg);
        String paramname = n.f1.f0.tokenImage;

        //System.out.println(paramname + ":" + paramtype);
        boolean name_conflict = !my_context.addParam(paramname, paramtype);
        if (name_conflict) {
            //System.out.println("name conflict");
            return false;
        }

        return true;
        //return super.visit(n);
    }

    @Override
    //f1 = Formal Parameter
    public Boolean visit(FormalParameterRest n) {
        return n.f1.accept(this);
    }


    @Override
    //f0 = Type
    //f1 = Identifier
    public Boolean visit(VarDeclaration n) {
        //System.out.println("variable declaration");
        TypeGetter tg = new TypeGetter();
        String vartype = n.f0.accept(tg);

        String varname = n.f1.f0.tokenImage;

        if (my_context.inMethod()) {
            boolean name_conflict = !my_context.addLocal(varname, vartype);
            if (name_conflict) {
                //System.out.println(varname);
                return false;
            }
        } else {
            boolean name_conflict = !my_context.addField(varname, vartype);
            if (name_conflict) {
                return false;
            }
        }
        //System.out.println(varname + ":" + vartype);

        return true;


        //return super.visit(n);
    }
}
