package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.GJNoArguDepthFirst;

public class TypeGetter extends GJNoArguDepthFirst<String> {

    @Override
    public String visit(Type n) {
        return n.f0.accept(this);
    }

    @Override
    public String visit(BooleanType n) {
        return "boolean";
        //return super.visit(n);
    }

    @Override
    public String visit(IntegerType n) {
        return "integer";
        //return super.visit(n);
    }

    @Override
    public String visit(ArrayType n) {
        return "int array";
        //return super.visit(n);
    }

    @Override
    public String visit(Identifier n) {
        return n.f0.tokenImage;
        //return super.visit(n);
    }
}
