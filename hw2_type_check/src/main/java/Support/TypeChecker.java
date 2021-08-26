package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.GJNoArguDepthFirst;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class TypeChecker extends GJNoArguDepthFirst<String> {

    private static Context my_context;
    private static String error_message = "Type error";

    public TypeChecker(Context c) {
        this.my_context = c;
    }

    @Override
    public String visit(Goal n) {

        String ret1 = n.f0.accept(this);


        if (ret1.equals(error_message)) {
            return error_message;
        }

        for (Node node: n.f1.nodes) {
            String temp = node.accept(this);
            ////System.out.println(temp);

            if (temp.equals(error_message)) {
                return error_message;
            }
        }
        return "Program type checked successfully";
    }

    @Override
    //f1 = Identifier
    //f15 = Statement()
    public String visit(MainClass n) {
        //System.out.println("Main");
        String mainClassName = n.f1.f0.tokenImage;
        my_context.setCurrClass(mainClassName);
        my_context.setCurrMethod("Main");

        for (Node node: n.f15.nodes) {
            String temp = node.accept(this);
            if (temp.equals(error_message)) {
                //System.out.println("main error");
                return error_message;
            }
        }

        my_context.exitMethod();
        my_context.exitClass();

        return "";


        //return super.visit(n);
    }

    @Override
    public String visit(TypeDeclaration n) {
        return n.f0.accept(this);
    }

    @Override
    //f1 = Identifier
    //f4 = Method Declarations
    public String visit(ClassDeclaration n) {
        String className = n.f1.f0.tokenImage;
        //System.out.println("class declaration -> " + className);
        my_context.setCurrClass(className);

        for (Node node: n.f4.nodes) {
            String temp = node.accept(this);

            if (temp.equals(error_message)) {
                return error_message;
            }
        }

        my_context.exitClass();
        return "";
        //return super.visit(n);
    }

    @Override
    //f1 = Identifier
    //f6 = Statements
    public String visit(ClassExtendsDeclaration n) {
        String className = n.f1.f0.tokenImage;
        //System.out.println("class extends declaration -> " + className);
        my_context.setCurrClass(className);

        for (Node node: n.f6.nodes) {
            String temp = node.accept(this);

            if (temp.equals(error_message)) {
                //System.out.println("error class extends");
                return error_message;
            }
        }

        my_context.exitClass();
        return "";
        //return super.visit(n);
    }

    @Override
    //f1 = Type
    //f2 = Identifier
    //f8 = Statements
    //f10 = Expression (return value)
    public String visit(MethodDeclaration n) {
        String methodname = n.f2.f0.tokenImage;
        //System.out.println("method declaration -> " + methodname);
        my_context.setCurrMethod(methodname);

        for (Node node: n.f8.nodes) {
            String temp = node.accept(this);
            if (temp.equals(error_message)) {
                //System.out.println("method error");
                return error_message;
            }
        }

        TypeGetter tg = new TypeGetter();
        String return_type = n.f1.accept(tg);
        String f10 = n.f10.accept(this);

        //System.out.println("method declaration -> " + return_type + ":" + f10);
        if (!return_type.equals(f10)) {
            return error_message;
        }

        my_context.exitMethod();
        return "";
    }

    @Override
    //f0 = NodeChoice
    // Block
    // Assignment Statement
    // Array Assignment Statement
    // If Statement
    // While Statement
    // Print Statement
    public String visit(Statement n) {
        ////System.out.println("statement");
        return n.f0.accept(this);
    }

    @Override
    //f1 = more statements
    public String visit(Block n) {

        //int counter = 0;
        for (Node node: n.f1.nodes) {
            String temp = node.accept(this);
            ////System.out.println(temp + counter);
            if (temp.equals(error_message)) {
                //System.out.println("block error");
                return error_message;
            }
            //counter++;
        }
        ////System.out.println("block complete");
        return "";
        //return super.visit(n);
    }

    @Override
    //f0 = Identifier
    //f2 = Expression
    public String visit(AssignmentStatement n) {
        ////System.out.println("assignment statement");

        String f0 = n.f0.accept(this);
        String f2 = n.f2.accept(this);

        if (f0.equals(error_message)) {
            return error_message;
        }

        if (f2.equals(error_message)) {
            return error_message;
        }


        //System.out.println("assignment statement -> " + f0 + ":" + f2);

        ////System.out.println(f0 + ":" + f2);
        if (!f0.equals(f2)) {
            //f2 can be subclass of f0;

            //System.out.println("not equal");
	    //boolean isSubtype = my_context.isSubtype(f2, f0);
	    //System.out.println(isSubtype);
	    
            if (my_context.isSubtype(f2, f0)) {
		//System.out.println("assignment statement -> " + f0 + ":" + f2);
		return "";
            }
             

            return error_message;
        }
        return "";
        //return super.visit(n);
    }

    @Override
    //f0 = Identifier -> int array
    //f2 = Expression -> integer
    //f5 = Expression -> integer
    public String visit(ArrayAssignmentStatement n) {
        ////System.out.println("array assignment statement");

        String f0 = n.f0.accept(this);
        if (!f0.equals("int array")) {
            return error_message;
        }

        String f2 = n.f2.accept(this);
        if (!f2.equals("integer")) {
            return error_message;
        }

        String f5 = n.f2.accept(this);
        if (!f5.equals("integer")) {
            return error_message;
        }

        //System.out.println("array assignment statement -> " + f0 + ":" + f2 + ":" + f5);

        return "";
        //return super.visit(n);
    }

    @Override
    //f2 = Expression -> Boolean
    //f4 = Statement
    //f6 = Statement
    public String visit(IfStatement n) {
        //System.out.println("if statement");
        String f2 = n.f2.accept(this);
        ////System.out.println(f2);
        if (!f2.equals("boolean")) {
            ////System.out.println("if statement: boolean error");
            return error_message;
        }

        String f4 = n.f4.accept(this);
        if (f4.equals(error_message)) {
            ////System.out.println("if statement: f4 statement error");
            return error_message;
        }

        String f6 = n.f6.accept(this);
        if (f6.equals(error_message)) {
            ////System.out.println("if statement: f6 statement error");
            return error_message;
        }

        //System.out.println("if statement complete");
        return "";
        //return super.visit(n);
    }

    @Override
    //f2 = Expression -> Boolean
    //f4 = Statement
    public String visit(WhileStatement n) {
        //System.out.println("while statement");
        String f2 = n.f2.accept(this);
        if (!f2.equals("boolean")) {
            return error_message;
        }

        String f4 = n.f4.accept(this);
        if (f4.equals(error_message)) {
            return error_message;
        }
        //System.out.println("while statement complete");
        return "";
        //return super.visit(n);
    }

    @Override
    //f2 = Expression -> int
    public String visit(PrintStatement n) {

        ////System.out.println("print statement");
        String temp = n.f2.accept(this);
        if (!temp.equals("integer")) {
            //System.out.println("print error");
            return error_message;
        } else {
            return "";
        }
        //return super.visit(n);
    }

    @Override
    //Different types of Expressions
    // And Expression
    // Compare Expression
    // Plus Expression
    // Minus Expression
    // Times Expression
    // Array Lookup
    // Array Length
    // Message Send
    // Primary Expression
    public String visit(Expression n) {
        ////System.out.println("expression");
        return n.f0.accept(this);
    }

    @Override
    //f0 = Expression
    //f1 = Rest of Expression
    public String visit(ExpressionList n) {
        ////System.out.println("expression list");
        String ret = n.f0.accept(this);
        for (Node node: n.f1.nodes) {
            String temp = node.accept(this);
            if (temp.equals(error_message)) {
                return error_message;
            } else {
                ret = ret + "," + temp;
            }
        }
        return ret;
    }

    @Override
    //f0 = ","
    //f1 = Expression
    public String visit(ExpressionRest n) {
        return n.f1.accept(this);
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "&&"
    //f2 = Primary Expression
    public String visit(AndExpression n) {
        //System.out.println("and");

        String f0 = n.f0.accept(this);
        if (!f0.equals("boolean")) {
            return error_message;
        }

        String f2 = n.f2.accept(this);
        if (!f2.equals("boolean")) {
            return error_message;
        }

        return "boolean";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "<"
    //f2 = Primary Expression
    public String visit(CompareExpression n) {
        ////System.out.println("compare");
        String f0 = n.f0.accept(this);
        String f2 = n.f2.accept(this);

        ////System.out.println(f0 + ":" + f2);

        if (!f0.equals("integer")) {
            return error_message;
        }

        if (!f2.equals("integer")) {
            return error_message;
        }

        return "boolean";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "+"
    //f2 = Primary Expression
    public String visit(PlusExpression n) {
        String f0 = n.f0.accept(this);
        String f2 = n.f2.accept(this);

        //System.out.println("plus -> " + f0 + ":" + f2);

        if (!f0.equals("integer")) {
            return error_message;
        }

        if (!f2.equals("integer")) {
            return error_message;
        }

        return "integer";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "-"
    //f2 = Primary Expression
    public String visit(MinusExpression n) {

        String f0 = n.f0.accept(this);
        String f2 = n.f2.accept(this);

        //System.out.println("minus -> " + f0 + ":" + f2);

        if (!f0.equals("integer")) {
            return error_message;
        }

        if (!f2.equals("integer")) {
            return error_message;
        }

        return "integer";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "*"f
    //f2 = Primary Expression
    public String visit(TimesExpression n) {


        String f0 = n.f0.accept(this);
        String f2 = n.f2.accept(this);

        //System.out.println("times -> " + f0 + ":" + f2);
        if (!f0.equals("integer")) {
            return error_message;
        }

        if (!f2.equals("integer")) {
            return error_message;
        }

        return "integer";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "["
    //f2 = Primary Expression
    //f1 = "]"
    public String visit(ArrayLookup n) {

        String f0 = n.f0.accept(this);
        if (!f0.equals("int array")) {
            ////System.out.println("f0 array error");
            return error_message;
        }

        String f2 = n.f2.accept(this);
        if (!f2.equals("integer")) {
            ////System.out.println("f2 array error");
            return error_message;
        }

        //System.out.println("array lookup -> " + f0 + ":" + f2);

        return "integer";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = .
    //f2 = length
    public String visit(ArrayLength n) {
        //System.out.println("array length");
        return "";
        //return super.visit(n);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "."
    //f2 = Identifier
    //f3 = "("
    //f4 = Expression List
    //f5 = ")"
    public String visit(MessageSend n) {
        ////System.out.println("message send");
        String objectName = n.f0.accept(this);
        ////System.out.println(objectName);
        String methodName = n.f2.f0.tokenImage;

        TypeClass curr_object = my_context.getClass(objectName);

        if (!curr_object.checkMethod(methodName)) {
            //System.out.println("no method error");
            return error_message;
        }

        Method temp_method = curr_object.getMethod(methodName);
        List<String> methodParameterTypes = temp_method.getParamTypes();

        String _expressionListTypes = n.f4.accept(this);
        ////System.out.println(_expressionListTypes);

        //error when dealing with no parameters
        List<String> expressionListTypes;
        if (_expressionListTypes.equals("")) {
            expressionListTypes = new ArrayList<>();
        } else {
            expressionListTypes = Arrays.asList(_expressionListTypes.split(","));
        }



        boolean isEqual = compareLists(methodParameterTypes, expressionListTypes);
        //boolean isEqual = expressionListTypes.equals(methodParameterTypes);
        ////System.out.println(methodParameterTypes);
        ////System.out.println(expressionListTypes);


        if (isEqual) {
            String return_type = temp_method.getReturnType();
            //System.out.println("message send -> " + return_type);
            return return_type;
        } else {
            //System.out.println("parameter error");
            return error_message;
        }
    }

    @Override
    // Types of Expressions
    // Integer
    // True False
    // Identifier
    // This
    // new int[x]
    // new id()
    // ! Expression
    // (expression)
    public String visit(PrimaryExpression n) {
        ////System.out.println("primary expression");
        String ret = n.f0.accept(this);
        return ret;
        //return super.visit(n);
    }

    @Override
    public String visit(IntegerLiteral n) {
        return "integer";
    }

    @Override
    public String visit(TrueLiteral n) {
        return "boolean";
    }

    @Override
    public String visit(FalseLiteral n) {
        return "boolean";
    }



    @Override
    //f0 -> NodeToken
    public String visit(Identifier n) {

        String object_name = n.f0.tokenImage;

        String object_type = my_context.getObjectType(object_name);
        ////System.out.println("identifier -> " + object_name + ":" + object_type);
        if (object_type.equals(error_message)) {
            ////System.out.println("no object error");
            return error_message;
        } else {
            return object_type;
        }

        //return super.visit(n);
    }

    @Override
    //f0 = this -> return current class type
    public String visit(ThisExpression n) {
        return my_context.currClassName();
        //return super.visit(n);
    }

    @Override
    //f3 = Expression -> int
    public String visit(ArrayAllocationExpression n) {
        String f3 = n.f3.accept(this);
        if (!f3.equals("integer")) {
            return error_message;
        }

        return "int array";
        //return super.visit(n);
    }

    @Override
    //f0 = new
    //f1 = Identifier
    //f2 = "("
    //f3 = ")"
    public String visit(AllocationExpression n) {
        //System.out.println("allocation expression");
        String className = n.f1.f0.tokenImage;
        ////System.out.println(className);
        if (!my_context.checkClass(className)) {
            return error_message;
        } else {
            return className;
        }
        //return super.visit(n);
    }

    @Override
    //f1 = Expression -> boolean
    public String visit(NotExpression n) {
        ////System.out.println("not expression");
        String f1 = n.f1.accept(this);
        if (!f1.equals("boolean")) {
            return error_message;
        }

        return "boolean";
        //return super.visit(n);
    }



    @Override
    //f0 = "("
    //f1 = Expression
    //f2 = ")"
    public String visit(BracketExpression n) {
        ////System.out.println("bracket expression");
        return n.f1.accept(this);
        //return super.visit(n);
    }

    @Override
    public String visit(NodeOptional n) {
        return n.present() ? n.node.accept(this) : "";
    }

    //helper function for comparing two lists....
    //potential error in built in equals

    //l1 = method parameters
    //l2 = actual parameters
    public boolean compareLists(List<String> l1, List<String> l2) {

        //System.out.println(l1);
        //System.out.println(l2);

        int length1 = l1.size();
        int length2 = l2.size();

        if (length1 != length2) {
            //System.out.println("size difference -> " + length1 + ":" + length2);
            return false;
        }

        int i1;

        for (i1 = 0; i1 < length1; i1++) {

            String methodParameter = l1.get(i1);
            String passedParameter = l2.get(i1);


            boolean isEqual = methodParameter.equals(passedParameter);

            if (!isEqual) {
                boolean isSubtype = my_context.isSubtype(passedParameter, methodParameter);

                if (!isSubtype) {
                    return false;
                }
            }
        }

        return true;

    }


}
