import java.io.InputStream;

import cs132.IR.sparrow.Program;
import cs132.minijava.syntaxtree.Node;
import cs132.minijava.MiniJavaParser;
import cs132.minijava.ParseException;
import java.io.PrintStream;
import java.io.File;


import Support.*;

public class J2S{
    public static void printOutputToFile() {
	try {
            System.setOut(new PrintStream(new File("test.sparrow")));
            System.out.println("hello world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void main(String [] args) {
	try {
	    InputStream in = System.in;
	    Node root = new MiniJavaParser(in).Goal();


	    Context c = new Context();
	    ClassVisitor cv = new ClassVisitor(c);
	    root.accept(cv);

	    GoalVisitor gv = new GoalVisitor(c);
	    root.accept(gv);

        Program p = gv.getProgram();
        System.out.println(p.toString());

	    //c.printAll();

	    
	} catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
    
}
