package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.DepthFirstVisitor;
import java.util.Enumeration;

public class PrintVisitor extends DepthFirstVisitor {

    public PrintVisitor() {
        //System.out.println("hello world");
    }

    @Override
    //f0 = MainClass
    //f1 = TypeDeclarations -> multiple classes
    //f2 = nodetoken -> empty??
    public void visit(Goal n) {
        //System.out.println("Visiting: Goal");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
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
    public void visit(MainClass n) {
        //System.out.println("Visiting: MainClass");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);
        n.f13.accept(this);
        n.f14.accept(this);
        n.f15.accept(this);
        n.f16.accept(this);
        n.f17.accept(this);
    }

    @Override
    //vector of nodes
    public void visit(NodeList n) {
        Enumeration e = n.elements();

        while(e.hasMoreElements()) {
            ((Node)e.nextElement()).accept(this);
        }

    }

    @Override
    //optional vector of nodes
    public void visit(NodeListOptional n) {
        if (n.present()) {
            Enumeration e = n.elements();

            while(e.hasMoreElements()) {
                ((Node)e.nextElement()).accept(this);
            }
        }

    }

    //optional grammar token
    @Override
    public void visit(NodeOptional n) {
        if (n.present()) {
            n.node.accept(this);
        }

    }

    @Override
    //vector of nodes -> unclear difference between this and NodeList
    public void visit(NodeSequence n) {
        Enumeration e = n.elements();

        while(e.hasMoreElements()) {
            ((Node)e.nextElement()).accept(this);
        }

    }

    @Override
    //single grammar token
    public void visit(NodeToken n) {
        System.out.println(n.tokenImage);
    }

    @Override
    //f0 either classDeclaration or classExtendsDeclaration
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
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
    }

    //f0 = "class"
    //f1 = Identifier
    //f2 = "extends"
    //f3 = Identifier
    //f4 = "{"
    //f5 = Variable Declarations
    //f6 = Statements
    //f7 = "}"
    @Override
    public void visit(ClassExtendsDeclaration n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
    }

    //f0 = Type
    //f1 = Identifier
    //f2 = ";"
    @Override
    public void visit(VarDeclaration n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

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
    @Override
    public void visit(MethodDeclaration n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);
    }

    //f0 = Formal Parameter
    //f1 = List of Formal Parameter Rests
    @Override
    public void visit(FormalParameterList n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    //f0 = Type
    //f1 = Identifier
    @Override
    public void visit(FormalParameter n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    // f0 = ","
    // f1 = Formal Parameter
    @Override
    public void visit(FormalParameterRest n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    //f0 = NodeChoice -> 1 of many types!
    @Override
    public void visit(Type n) {
        n.f0.accept(this);
    }

    //f0 = "int"
    //f1 = "["
    //f2 = "]"
    @Override
    public void visit(ArrayType n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    //f0 = "boolean"
    @Override
    public void visit(BooleanType n) {
        n.f0.accept(this);
    }

    //f0 = "int"
    @Override
    public void visit(IntegerType n) {
        n.f0.accept(this);
    }


    @Override
    //f0 = NodeChoice
    // Block
    // Assignment Statement
    // Array Assignment Statement
    // If Statement
    // While Statement
    // Print Statement
    public void visit(Statement n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = "{"
    //f1 = more statements
    //f2 = "}"
    public void visit(Block n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Identifier
    //f1 = "="
    //f2 = Expression
    //f3 = ";"
    public void visit(AssignmentStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    @Override
    //f0 = Identifier
    //f1 = "["
    //f2 = Expression
    //f3 = "]"
    //f4 = "="
    //f5 = Expression
    //f6 = ";"
    public void visit(ArrayAssignmentStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
    }

    @Override
    //f0 = "if"
    //f1 = "("
    //f2 = Expression -> Boolean
    //f3 = ")"
    //f4 = Statement
    //f5 = "else"
    //f6 = Statement
    public void visit(IfStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
    }

    @Override
    //f0 = "while"
    //f1 = "("
    //f2 = Expression -> Boolean
    //f3 = ")"
    //f4 = Statement
    public void visit(WhileStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    @Override
    //f0 = System.out.println
    //f1 = "("
    //f2 = Expression -> int
    //f3 = ")"
    //f4 = ";"
    public void visit(PrintStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
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
    public void visit(Expression n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "&&"
    //f2 = Primary Expression
    public void visit(AndExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "<"
    //f2 = Primary Expression
    public void visit(CompareExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "+"
    //f2 = Primary Expression
    public void visit(PlusExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "-"
    //f2 = Primary Expression
    public void visit(MinusExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "*"
    //f2 = Primary Expression
    public void visit(TimesExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "["
    //f2 = Primary Expression
    //f1 = "]"
    public void visit(ArrayLookup n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    @Override
    //f0 = Primary Expression
    //f1 = .
    //f2 = length
    public void visit(ArrayLength n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    //method call ????
    //f0 = Primary Expression
    //f1 = "."
    //f2 = Identifier
    //f3 = "("
    //f4 = Expression List
    //f5 = ")"
    public void visit(MessageSend n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
    }

    @Override
    //f0 = Expression
    //f1 = Rest of Expression
    public void visit(ExpressionList n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    @Override
    //f0 = ","
    //f1 = Expression
    public void visit(ExpressionRest n) {
        n.f0.accept(this);
        n.f1.accept(this);
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
    public void visit(PrimaryExpression n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = NodeToken -> int
    public void visit(IntegerLiteral n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = NodeToken -> boolean
    public void visit(TrueLiteral n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = NodeToken -> boolean
    public void visit(FalseLiteral n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = NodeToken -> Existing Identifier
    public void visit(Identifier n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = this
    public void visit(ThisExpression n) {
        n.f0.accept(this);
    }

    @Override
    //f0 = new
    //f1 = int
    //f2 = "["
    //f3 = Expression
    //f4 = "]"
    public void visit(ArrayAllocationExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    @Override
    //f0 = new
    //f1 = Identifier
    //f2 = "("
    //f3 = ")"
    public void visit(AllocationExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    @Override
    //f0 = "!"
    //f1 = Expression -> boolean
    public void visit(NotExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    @Override
    //f0 = "("
    //f1 = Expression
    //f2 = ")"
    public void visit(BracketExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }
}

    
