package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.GJDepthFirst;


public class NameGetter extends GJDepthFirst<String, Context> {

    @Override
    // Integer
    // True False
    // Identifier
    // This
    // new int[x]
    // new id()
    // ! Expression
    // (expression)
    public String visit(PrimaryExpression n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    public String visit(Identifier n, Context c) {
        String varName = n.f0.tokenImage;
        String className = c.getObjectType(varName);

        return className;
        //return super.visit(n);
    }

    @Override
    public String visit(AllocationExpression n, Context c) {
        return n.f1.f0.tokenImage;
    }

    @Override
    public String visit(ThisExpression n, Context c) {
        TypeClass currClass = c.getCurrClass();
        return currClass.getName();
    }

    @Override
    public String visit(BracketExpression n, Context c) {
        return n.f1.accept(this,c);
    }

    @Override
    // Message Send
    // Primary Expression
    public String visit(Expression n, Context c) {
        return n.f0.accept(this,c);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "."
    //f2 = Identifier
    //f3 = "("
    //f4 = Expression List
    //f5 = ")"
    public String visit(MessageSend n, Context c) {

        String f0 = n.f0.accept(this, c);
        TypeClass currClass = c.getClass(f0);

        String methodName = n.f2.f0.tokenImage;
        Method currMethod = currClass.getMethod(methodName);

        return currMethod.getReturnType();
    }
}
