package Support;

import cs132.IR.sparrow.*;
import cs132.IR.syntaxtree.Move;
import cs132.minijava.syntaxtree.Identifier;
import cs132.minijava.visitor.GJDepthFirst;

import java.util.ArrayList;

public class IdVisitor extends GJDepthFirst<IdWrapper, Context> {

    @Override
    public IdWrapper visit(Identifier n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();
        IdWrapper ret_wrapper = new IdWrapper();
        cs132.IR.token.Identifier id;
        String f0 = n.f0.tokenImage;


        String variableType = c.getVariableType(f0);
        ret_wrapper.setVarType(variableType);

            //LOCAL/////////////////////////////////////////////////////////////
        if (variableType.equals("local")) {
            if (c.initializeVariables()) {
                //System.out.println("initializing " + f0);
                c.setLocalInitialized(f0);
            }
            id = new cs132.IR.token.Identifier(f0);


            //PARAM/////////////////////////////////////////////////////////////

        } else if (variableType.equals("param")) {
            id = new cs132.IR.token.Identifier(f0);

            //FIELD/////////////////////////////////////////////////////////////
        } else if (variableType.equals("field")) {

            //GET OFFSET////////////////////////////////////////////////////////
            TypeClass currClass = c.getCurrClass();
            int fieldOffset = currClass.getFieldOffset(f0);

            int i1 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier fieldOffset_i = new cs132.IR.token.Identifier("v"+i1);
            Move_Id_Integer assignOffset = new Move_Id_Integer(fieldOffset_i, fieldOffset);
            ret.add(assignOffset);

            //SCALE OFFSET//////////////////////////////////////////////////////
            int i2 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier four = new cs132.IR.token.Identifier("v"+i2);
            Move_Id_Integer assignFour = new Move_Id_Integer(four, 4);
            ret.add(assignFour);

            /*
            int i3 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier eight = new cs132.IR.token.Identifier("v"+i3);
            Move_Id_Integer assignEight = new Move_Id_Integer(eight, 8);
            ret.add(assignEight);

             */


            Add addFour = new Add(fieldOffset_i, fieldOffset_i, four);
            ret.add(addFour);

            int i4 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier fieldPointer = new cs132.IR.token.Identifier("v"+i4);


            //LOAD FIELD////////////////////////////////////////////////////////
            cs132.IR.token.Identifier base = new cs132.IR.token.Identifier("this");
            Add addOffset = new Add(fieldPointer, base, fieldOffset_i);
            ret.add(addOffset);

            ret_wrapper.setIsField(true);
            //returns address of field
            id = fieldPointer;

            //SET INITIALIZED//////////////////////////////////////////////////
            int i5 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier one = new cs132.IR.token.Identifier("v"+i5);
            Move_Id_Integer assignOne = new Move_Id_Integer(one, 1);
            ret.add(assignOne);

            int i6 = c.getLocalCounter();
            c.incrementCounter();
            cs132.IR.token.Identifier fieldInitialized = new cs132.IR.token.Identifier("v"+i6);
            Move_Id_Id copyAddress = new Move_Id_Id(fieldInitialized, fieldPointer);
            ret.add(copyAddress);

            Add addFour2 = new Add(fieldInitialized, fieldInitialized, four);
            ret.add(addFour2);

            Store setInitialized = new Store(fieldInitialized, 0, one);
            ret.add(setInitialized);

        } else {

            //System.out.println("fail");
            return null;
        }

        ret_wrapper.setName(f0);
        ret_wrapper.setIdentifier(id);
        ret_wrapper.setInstructions(ret);

        return ret_wrapper;
    }
}