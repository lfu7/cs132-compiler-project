import java.io.InputStream;
import cs132.IR.syntaxtree.Node;
import cs132.IR.SparrowParser;
import cs132.IR.ParseException;
import cs132.IR.sparrowv.Program;

import Support.*;

//////////////////////////////////////////////
//LEFT TO DO//////////////////////////////////
//////////////////////////////////////////////

//WHEN USING S REGISTERS, ALWAYS SAVE
//CALL behavior
//RESTORE S VARIABLES AT THE END OF THE FUNCTION,
//  new data structure to keep track of s registers used at ANY point -> list should suffice


public class S2SV {
    public static void main(String [] args) {
        try {
            InputStream in = System.in;
            Node root = new SparrowParser(System.in).Program();

            Context c = new Context();

            GoalVisitor gv = new GoalVisitor();
            Program p = root.accept(gv,c);
            System.out.println(p.toString());


        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}
