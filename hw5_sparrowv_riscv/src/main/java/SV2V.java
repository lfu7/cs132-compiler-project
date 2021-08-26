import java.io.InputStream;
import cs132.IR.ParseException;
import cs132.IR.SparrowParser;
import cs132.IR.syntaxtree.Node;
import cs132.IR.registers.*;
import cs132.IR.visitor.SparrowVConstructor;
import cs132.IR.sparrowv.*;
import Support.*;

public class SV2V {
    public static void main(String [] args) {
        Registers.SetRiscVregs();
        try {
            Node root = new SparrowParser(System.in).Program();

            Context c = new Context();
            SparrowVConstructor sc = new SparrowVConstructor();
            root.accept(sc);
            Program p = sc.getProgram();

            ContextBuilder cb = new ContextBuilder(c);
            p.accept(cb);

            //c.printAll();

            ProgramBuilder pb = new ProgramBuilder(c);
            p.accept(pb);

        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }

}