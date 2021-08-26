package Support;

import cs132.minijava.syntaxtree.*;
import cs132.minijava.visitor.GJNoArguDepthFirst;

import java.util.Enumeration;

public class ClassVisitor extends GJNoArguDepthFirst<Boolean> {

    private static Context my_context;
    public ClassVisitor(Context c) {
        this.my_context = c;
    }

    @Override
    public Boolean visit(Goal n) {
        boolean f0 = n.f0.accept(this);
        boolean f1 = n.f1.accept(this);
        //n.f2.accept(this);
        return f0 && f1;
        //return super.visit(n);
    }

    @Override
    //f1 = Identifier

    // not much to do here, simply add main to the list of existing classes
    public Boolean visit(MainClass n) {
        String mainClassName = n.f1.f0.tokenImage;
        my_context.addClass(mainClassName);
        return true;
    }

    @Override
    //always casted to type declaration since only classes in list
    //visit all type declarations
    public Boolean visit(NodeListOptional n) {
        if (!n.present()) {
            return true;
        } else {
            boolean _ret = true;
            int _count = 0;

            for(Enumeration e = n.elements(); e.hasMoreElements(); ++_count) {
                Boolean temp = ((TypeDeclaration)e.nextElement()).accept(this);
                _ret = _ret && temp;

            }

            return _ret;
        }
    }

    @Override
    public Boolean visit(TypeDeclaration n) {
        return n.f0.accept(this);
    }

    @Override
    // makes sure that the class name is unique
    public Boolean visit(ClassDeclaration n) {
        //System.out.println("class declaration");

        String className = n.f1.f0.tokenImage;
        if (my_context.checkClass(className)) {
            return false;
        } else {
            //System.out.println(className);
            my_context.addClass(className);

            //System.out.println("return true");
            return true;
        }
    }

    @Override
    public Boolean visit(ClassExtendsDeclaration n) {
        //System.out.println("class extends declaration");
        String subClassName = n.f1.f0.tokenImage;
        String superClassName = n.f3.f0.tokenImage;
        //System.out.println(subClassName + "-" + superClassName);

        //class names must be distinct
        // superclass must exist
        if (my_context.checkClass(subClassName)) {
            return false;
        } else if (!my_context.checkClass(superClassName)) {
            return false;
        } else {
            my_context.addClass(subClassName);
            my_context.addSubtype(subClassName, superClassName);
            return true;
        }
    }

    @Override
    public Boolean visit(NodeToken n) {
        //System.out.println(n.tokenImage);
        return true;
    }
}