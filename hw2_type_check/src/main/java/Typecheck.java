import java.io.InputStream;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.MiniJavaParser;
import cs132.minijava.ParseException;

import Support.*;

public class Typecheck{
    public static void main(String [] args) {
        //To do:
		//class visitor:
		//check not acyclic

		//context builder:
		//check no overloading
		//check no variables = parameters

	try {
	    //System.out.println("hello world");
	    InputStream in = System.in;
	    Node root = new MiniJavaParser(in).Goal();
	    //PrintVisitor p = new PrintVisitor();
	    //root.accept(p);

	    Context c = new Context();

	    //adds classes to the Context, makes sure no duplicates, no cycles
		//determine all existing so that identifiers can be processed in later passes
	    ClassVisitor cv = new ClassVisitor(c);
	    Boolean pass1 = root.accept(cv);

	    if (c.checkCycle()) {
		System.out.println("Type error");
		return;
	    }

	    if (!pass1) {
		System.out.println("Type error");
		return;
	    }

	    ContextBuilder cb = new ContextBuilder(c);
	    Boolean pass2 = root.accept(cb);

	    if (!pass2) {
		System.out.println("Type error");
		return;
	    }

	    //c.printAll();

	    TypeChecker tc = new TypeChecker(c);
	    String pass3 = root.accept(tc);

	    //c.printSubtypes();
	    System.out.println(pass3);

	} catch (ParseException e) {
	    System.out.println(e.toString());
	}
	    
    }
}
