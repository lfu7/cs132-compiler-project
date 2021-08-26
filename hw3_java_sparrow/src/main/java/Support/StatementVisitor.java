package Support;

import cs132.IR.sparrow.*;

import cs132.IR.syntaxtree.Move;
import cs132.IR.token.Label;
import cs132.minijava.syntaxtree.*;
import cs132.minijava.syntaxtree.Block;
import cs132.minijava.visitor.GJDepthFirst;

import cs132.IR.token.Identifier;
import cs132.IR.token.FunctionName;

import java.util.ArrayList;
import java.util.List;

public class StatementVisitor extends GJDepthFirst<ArrayList<Instruction>, Context> {

    @Override
    //f0 = NodeChoice
    // Block DONE
    // Assignment Statement DONE
    // Array Assignment Statement NEEDS BOUNDS CHECK
    // If Statement DONE
    // While Statement DONE
    // Print Statement DONE
    public ArrayList<Instruction> visit(Statement n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    //f0 = "{"
    //f1 = more statements
    //f2 = "}"
    public ArrayList<Instruction> visit(Block n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();
        for (Node node: n.f1.nodes) {
            ArrayList<Instruction> temp = node.accept(this,c);
            ret.addAll(temp);
        }
        return ret;
    }

    @Override
    //f0 = Identifier
    //f1 = "="
    //f2 = Expression
    //f3 = ";"
    public ArrayList<Instruction> visit(AssignmentStatement n, Context c) {

        //LEFT SIDE/////////////////////////////////////////////////////////

        c.initializeOn();
        IdVisitor iv = new IdVisitor();
        IdWrapper wrap1 = n.f0.accept(iv, c);
        c.initializeOff();

        ArrayList<Instruction> ret1 = wrap1.getInstructions();
        Identifier lhs = wrap1.getIdentifier();

        //RIGHT SIDE////////////////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i2);
        ArrayList<Instruction> ret2  = n.f2.accept(this,c);
        Identifier rhs = new Identifier("v"+i2);

        //ASSIGN ///////////////////////////////////////////////////////////
        ret1.addAll(ret2);

        if (wrap1.isField()){
            Store storeField = new Store(lhs,0, rhs);
            ret1.add(storeField);


        } else {
            Move_Id_Id assign = new Move_Id_Id(lhs, rhs);
            ret1.add(assign);
        }


        return ret1;
    }

    @Override
    //f0 = Identifier
    //f1 = "["
    //f2 = Expression
    //f3 = "]"
    //f4 = "="
    //f5 = Expression
    //f6 = ";"

    //MISSING: BOUNDS CHECK
    public ArrayList<Instruction> visit(ArrayAssignmentStatement n, Context c) {

        ArrayList<Instruction> ret = new ArrayList<>();
        //LEFT SIDE/////////////////////////////////////////////////////////
        IdVisitor iv = new IdVisitor();
        IdWrapper wrap1 = n.f0.accept(iv, c);
        ArrayList<Instruction> ret1 = wrap1.getInstructions();
        ret.addAll(ret1);
        Identifier base = wrap1.getIdentifier();

        //LEFT SIDE CONT//////////////////////////////////////////////////////

        int i1 = c.getLocalCounter();
        c.incrementCounter();
        Identifier arrayBase = new Identifier("v"+i1);

        if (wrap1.isField()) {
            Load loadArray = new Load(arrayBase, base, 0);
            ret.add(loadArray);
        } else {
            Move_Id_Id copy = new Move_Id_Id(arrayBase, base);
            ret.add(copy);
        }

        //NULL POINTER CHECK////////////////////////////////////////////////
        String varType = wrap1.getVarType();
        //System.out.println(varType);
        String varname = wrap1.getName();
        if (varType.equals("local")) {
            boolean isInitialized = c.isLocalInitialized(varname);
            int i11 = c.getLocalCounter();
            c.incrementCounter();
            Identifier initialized_i = new Identifier("v"+i11);
            if (isInitialized) {
                Move_Id_Integer moveTrue = new Move_Id_Integer(initialized_i, 1);
                ret.add(moveTrue);
            } else {
                //System.out.println("not initialized " + varname);
                Move_Id_Integer moveFalse = new Move_Id_Integer(initialized_i, 0);
                ret.add(moveFalse);
            }

            int i12 = c.getLocalCounter();
            c.incrementCounter();
            Label nullError = new Label("null_error"+i12);
            IfGoto nullCheck = new IfGoto(initialized_i, nullError);
            ret.add(nullCheck);

            int i13 = c.getLocalCounter();
            c.incrementCounter();
            Label pastNullCheck = new Label("no_null_error"+i13);
            Goto gotoPastNullCheck = new Goto(pastNullCheck);
            ret.add(gotoPastNullCheck);

            LabelInstr nullError_i = new LabelInstr(nullError);
            ret.add(nullError_i);
            String null_message = "\"null pointer\"";
            ErrorMessage null_message_e = new ErrorMessage(null_message);
            ret.add(null_message_e);

            LabelInstr pastNullCheck_i = new LabelInstr(pastNullCheck);
            ret.add(pastNullCheck_i);
        } else if (varType.equals("field")) {
            int i12 = c.getLocalCounter();
            c.incrementCounter();
            Label nullError = new Label("null_error"+i12);
            IfGoto nullCheck = new IfGoto(arrayBase, nullError);
            ret.add(nullCheck);

            int i13 = c.getLocalCounter();
            c.incrementCounter();
            Label pastNullCheck = new Label("no_null_error"+i13);
            Goto gotoPastNullCheck = new Goto(pastNullCheck);
            ret.add(gotoPastNullCheck);

            LabelInstr nullError_i = new LabelInstr(nullError);
            ret.add(nullError_i);
            String null_message = "\"null pointer\"";
            ErrorMessage null_message_e = new ErrorMessage(null_message);
            ret.add(null_message_e);

            LabelInstr pastNullCheck_i = new LabelInstr(pastNullCheck);
            ret.add(pastNullCheck_i);
        }

        //EVALUATE EXPRESSION 1/////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i2);
        ArrayList<Instruction> ret2 = n.f2.accept(this,c);
        ret.addAll(ret2);
        Identifier arrayIndex = new Identifier("v"+i2);

        //LESS THAN ZERO INDEX CHECK////////////////////////////////////////
        int i10 = c.getLocalCounter();
        c.incrementCounter();
        Identifier minusOne = new Identifier("v"+i10);
        Move_Id_Integer assignZero = new Move_Id_Integer(minusOne, 0);
        ret.add(assignZero);

        int i14 = c.getLocalCounter();
        c.incrementCounter();
        Identifier one = new Identifier("v"+i14);
        Move_Id_Integer moveOne = new Move_Id_Integer(one, 1);
        ret.add(moveOne);
        Subtract subtractOne = new Subtract(minusOne, minusOne, one);
        ret.add(subtractOne);

        int i11 = c.getLocalCounter();
        c.incrementCounter();
        Identifier negative = new Identifier("v"+i11);
        LessThan compNegative_i = new LessThan(negative, minusOne, arrayIndex);
        ret.add(compNegative_i);

        int i12 = c.getLocalCounter();
        c.incrementCounter();
        Label failCheck2 = new Label("if_else"+i12);
        IfGoto if2 = new IfGoto(negative, failCheck2);
        ret.add(if2);

        //if pass check
        int i13 = c.getLocalCounter();
        c.incrementCounter();
        Label passCheck2 = new Label("if_end"+i13);
        Goto g2 = new Goto(passCheck2);
        ret.add(g2);

        //if fail check
        LabelInstr failCheck_i2 = new LabelInstr(failCheck2);
        ret.add(failCheck_i2);
        String arrayOutOfBounds2 = "\"array index out of bounds\"";
        ErrorMessage e2 = new ErrorMessage(arrayOutOfBounds2);
        ret.add(e2);

        //goto next instruction....
        LabelInstr passCheck_i2 = new LabelInstr(passCheck2);
        ret.add(passCheck_i2);



        //BOUNDS CHECK/////////////////////////////////////////////////////

        //get length
        int i7 = c.getLocalCounter();
        c.incrementCounter();
        Identifier arrayLength = new Identifier("v"+i7);
        Load loadLength = new Load(arrayLength, arrayBase, 0);
        ret.add(loadLength);

        //make comparison
        int i8 = c.getLocalCounter();
        c.incrementCounter();
        Identifier inBound = new Identifier("v"+i8);
        LessThan boundCheck = new LessThan(inBound, arrayIndex, arrayLength);
        ret.add(boundCheck);

        //create if statement
        int i9 = c.getLocalCounter();
        c.incrementCounter();
        Label failCheck = new Label("if_else"+i9);
        IfGoto if1 = new IfGoto(inBound, failCheck);
        ret.add(if1);

        //if pass check
        int i20 = c.getLocalCounter();
        c.incrementCounter();
        Label passCheck = new Label("if_end"+i20);
        Goto g1 = new Goto(passCheck);
        ret.add(g1);

        //if fail check
        LabelInstr failCheck_i = new LabelInstr(failCheck);
        ret.add(failCheck_i);
        String arrayOutOfBounds = "\"array index out of bounds\"";
        ErrorMessage e = new ErrorMessage(arrayOutOfBounds);
        ret.add(e);

        //goto next instruction....
        LabelInstr passCheck_i = new LabelInstr(passCheck);
        ret.add(passCheck_i);


        //GET POINTER TO LOCATION///////////////////////////////////////////
        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Identifier four = new Identifier("v"+i3);
        Move_Id_Integer moveFour = new Move_Id_Integer(four, 4);
        ret.add(moveFour);

        int i4 = c.getLocalCounter();
        c.incrementCounter();
        Identifier finalOffset = new Identifier("v"+i4);
        Multiply multFour = new Multiply(finalOffset, arrayIndex, four);
        Add addFour = new Add(finalOffset, finalOffset, four);

        ret.add(multFour);
        ret.add(addFour);

        int i5 = c.getLocalCounter();
        c.incrementCounter();
        Identifier finalPointer = new Identifier("v"+i5);
        Add addOffset = new Add(finalPointer, arrayBase, finalOffset);
        ret.add(addOffset);


        //EVALUATE EXPRESSION 2/////////////////////////////////////////////
        int i6 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i6);
        ArrayList<Instruction> ret3 = n.f5.accept(this,c);
        ret.addAll(ret3);

        Identifier expr2 = new Identifier("v"+i6);


        //ASSIGN///////////////////////////////////////////////////////////
        Store assign = new Store(finalPointer, 0, expr2);
        ret.add(assign);

        return ret;
    }

    @Override
    //f0 = "if"
    //f1 = "("
    //f2 = Expression -> Boolean
    //f3 = ")"
    //f4 = Statement
    //f5 = "else"
    //f6 = Statement
    public ArrayList<Instruction> visit(IfStatement n, Context c) {

        //EVALUATE EXPRESSION 1/////////////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        ArrayList<Instruction> ret1 = n.f2.accept(this,c);
        Identifier condition = new Identifier("v"+i1);


        //IF STATEMENT//////////////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        Label if_else_i = new Label("if_else_"+i2);
        IfGoto ig1 = new IfGoto(condition, if_else_i);
        ret1.add(ig1);

        //IF TRUE///////////////////////////////////////////////////////////
        ArrayList<Instruction> ret2 = n.f4.accept(this,c);
        ret1.addAll(ret2);

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Label if_end_i = new Label("if_end_"+i3);
        Goto goto_end = new Goto(if_end_i);
        ret1.add(goto_end);

        //IF FALSE//////////////////////////////////////////////////////////
        LabelInstr l_else = new LabelInstr(if_else_i);
        ret1.add(l_else);

        ArrayList<Instruction> ret3 = n.f6.accept(this,c);
        ret1.addAll(ret3);

        LabelInstr l_end = new LabelInstr(if_end_i);
        ret1.add(l_end);

        return ret1;
    }

    @Override
    //f0 = "while"
    //f1 = "("
    //f2 = Expression -> Boolean
    //f3 = ")"
    //f4 = Statement
    public ArrayList<Instruction> visit(WhileStatement n, Context c) {
        ArrayList<Instruction> ret = new ArrayList<>();

        int i1 = c.getLocalCounter();
        c.incrementCounter();
        Label backToWhile = new Label("while"+i1);
        LabelInstr backToWhile_i = new LabelInstr(backToWhile);
        ret.add(backToWhile_i);

        //EVALUATE EXPRESSION 1/////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i2);
        ArrayList<Instruction> ret1 = n.f2.accept(this,c);
        ret.addAll(ret1);

        Identifier condition = new Identifier("v"+i2);

        //IF STATEMENT//////////////////////////////////////////////////////
        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Label break_i = new Label("break_while_"+i3);
        IfGoto ig1 = new IfGoto(condition, break_i);
        ret.add(ig1);

        //IF TRUE///////////////////////////////////////////////////////////
        ArrayList<Instruction> ret2 = n.f4.accept(this,c);
        ret.addAll(ret2);

        Goto goto_start = new Goto(backToWhile);
        ret.add(goto_start);

        //IF FALSE//////////////////////////////////////////////////////////
        LabelInstr l_else = new LabelInstr(break_i);
        ret.add(l_else);

        return ret;

    }

    @Override
    //f0 = System.out.println
    //f1 = "("
    //f2 = Expression -> int
    //f3 = ")"
    //f4 = ";"
    public ArrayList<Instruction> visit(PrintStatement n, Context c) {

        int currCount = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(currCount);
        ArrayList<Instruction> ret = n.f2.accept(this, c);

        //desired value should be in this variable
        Identifier temp = new Identifier("v"+currCount);
        Print p = new Print(temp);


        ret.add(p);

        return ret;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //EXPRESSIONS//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    //Different types of Expressions
    // And Expression DONE
    // Compare Expression DONE
    // Plus Expression DONE
    // Minus Expression DONE
    // Times Expression DONE
    // Array Lookup -> NEEDS NULL CHECK
    // Array Length DONE
    // Message Send DONE
    // Primary Expression DONE
    public ArrayList<Instruction> visit(Expression n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    public ArrayList<Instruction> visit(ExpressionRest n, Context c) {
        return n.f1.accept(this, c);
    }

    @Override
    //f0 = Primary Expression
    //f1 = "+"
    //f2 = Primary Expression
    public ArrayList<Instruction> visit(PlusExpression n, Context c) {

        int top = c.popStack();
        String sumName = "v" + top;
        Identifier sum = new Identifier(sumName);


        int currCounter1 = c.getLocalCounter();
        c.incrementCounter();
        String name1 = "v" + currCounter1;
        c.pushStack(currCounter1);
        c.checkInitializeOn();
        ArrayList<Instruction> ret1 = n.f0.accept(this, c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier(name1);


        int currCounter2 = c.getLocalCounter();
        c.incrementCounter();
        String name2 = "v" + currCounter2;
        c.pushStack(currCounter2);
        c.checkInitializeOn();
        ArrayList<Instruction> ret2 = n.f2.accept(this, c);
        c.checkInitializeOff();
        Identifier argu2 = new Identifier(name2);

        Add a = new Add(sum, argu1, argu2);

        ret1.addAll(ret2);
        ret1.add(a);

        return ret1;
    }

    @Override
    //f0 = Primary Expression
    //f1 = "-"
    //f2 = Primary Expression
    public ArrayList<Instruction> visit(MinusExpression n, Context c) {
        int top = c.popStack();
        String differenceName = "v" + top;
        Identifier difference = new Identifier(differenceName);


        int currCounter1 = c.getLocalCounter();
        c.incrementCounter();
        String name1 = "v" + currCounter1;
        c.pushStack(currCounter1);
        c.checkInitializeOn();
        ArrayList<Instruction> ret1 = n.f0.accept(this, c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier(name1);

        int currCounter2 = c.getLocalCounter();
        c.incrementCounter();
        String name2 = "v" + currCounter2;
        c.pushStack(currCounter2);
        c.checkInitializeOn();
        ArrayList<Instruction> ret2 = n.f2.accept(this, c);
        c.checkInitializeOff();
        Identifier argu2 = new Identifier(name2);

        Subtract s = new Subtract(difference, argu1, argu2);

        ret1.addAll(ret2);
        ret1.add(s);

        return ret1;
    }

    @Override
    //f0 = Primary Expression
    //f1 = "*"
    //f2 = Primary Expression
    public ArrayList<Instruction> visit(TimesExpression n, Context c) {
        int top = c.popStack();
        String productName = "v" + top;
        Identifier product = new Identifier(productName);


        int currCounter1 = c.getLocalCounter();
        c.incrementCounter();
        String name1 = "v" + currCounter1;
        c.pushStack(currCounter1);
        c.checkInitializeOn();
        ArrayList<Instruction> ret1 = n.f0.accept(this, c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier(name1);

        int currCounter2 = c.getLocalCounter();
        c.incrementCounter();
        String name2 = "v" + currCounter2;
        c.pushStack(currCounter2);
        c.checkInitializeOn();
        ArrayList<Instruction> ret2 = n.f2.accept(this, c);
        c.checkInitializeOff();
        Identifier argu2 = new Identifier(name2);

        Multiply m = new Multiply(product, argu1, argu2);

        ret1.addAll(ret2);
        ret1.add(m);

        return ret1;
    }

    @Override
    //f0 = Primary Expression
    //f1 = "<"
    //f2 = Primary Expression
    public ArrayList<Instruction> visit(CompareExpression n, Context c) {
        int top = c.popStack();
        String compName = "v" + top;
        Identifier comp = new Identifier(compName);


        int currCounter1 = c.getLocalCounter();
        c.incrementCounter();
        String name1 = "v" + currCounter1;
        c.pushStack(currCounter1);
        c.checkInitializeOn();
        ArrayList<Instruction> ret1 = n.f0.accept(this, c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier(name1);


        int currCounter2 = c.getLocalCounter();
        c.incrementCounter();
        String name2 = "v" + currCounter2;
        c.pushStack(currCounter2);
        c.checkInitializeOn();
        ArrayList<Instruction> ret2 = n.f2.accept(this, c);
        c.checkInitializeOff();
        Identifier argu2 = new Identifier(name2);

        LessThan lt = new LessThan(comp, argu1, argu2);

        ret1.addAll(ret2);
        ret1.add(lt);

        return ret1;
    }

    @Override
    //f0 = Primary Expression
    //f1 = "&&"
    //f2 = Primary Expression
    public ArrayList<Instruction> visit(AndExpression n, Context c) {
        int top = c.popStack();
        String andName = "v" + top;
        Identifier and = new Identifier(andName);


        int currCounter1 = c.getLocalCounter();
        c.incrementCounter();
        String name1 = "v" + currCounter1;
        c.pushStack(currCounter1);
        c.checkInitializeOn();
        ArrayList<Instruction> ret1 = n.f0.accept(this, c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier(name1);


        int currCounter2 = c.getLocalCounter();
        c.incrementCounter();
        String name2 = "v" + currCounter2;
        c.pushStack(currCounter2);
        c.checkInitializeOn();
        ArrayList<Instruction> ret2 = n.f2.accept(this, c);
        c.checkInitializeOff();
        Identifier argu2 = new Identifier(name2);

        Multiply m = new Multiply(and, argu1, argu2);

        ret1.addAll(ret2);
        ret1.add(m);

        return ret1;
    }

    @Override
    //f0 = Primary Expression
    //f1 = "["
    //f2 = Primary Expression
    //f3 = "]"

    //CHECK FOR NULL POINTER
    public ArrayList<Instruction> visit(ArrayLookup n, Context c) {
        int top = c.popStack();
        String lhs_name = "v" + top;
        Identifier lhs = new Identifier(lhs_name);
        ArrayList<Instruction> ret;

        //GET POINTER TO START OF ARRAY ////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        c.checkInitializeOn();
        ret = n.f0.accept(this,c);
        c.checkInitializeOff();

        Identifier base = new Identifier("v"+i1);

        //EVALUATE EXPRESSION //////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i2);

        ArrayList<Instruction> ret2 = n.f2.accept(this,c);

        Identifier arrayIndex = new Identifier("v"+i2);
        ret.addAll(ret2);

        //CHECK LESS THAN ZERO//////////////////////////////////////////////

        int i10 = c.getLocalCounter();
        c.incrementCounter();
        Identifier minusOne = new Identifier("v"+i10);
        Move_Id_Integer assignZero = new Move_Id_Integer(minusOne, 0);
        ret.add(assignZero);

        int i14 = c.getLocalCounter();
        c.incrementCounter();
        Identifier one = new Identifier("v"+i14);
        Move_Id_Integer moveOne = new Move_Id_Integer(one, 1);
        ret.add(moveOne);
        Subtract subtractOne = new Subtract(minusOne, minusOne, one);
        ret.add(subtractOne);

        int i11 = c.getLocalCounter();
        c.incrementCounter();
        Identifier negative = new Identifier("v"+i11);
        LessThan compNegative_i = new LessThan(negative, minusOne, arrayIndex);
        ret.add(compNegative_i);

        int i12 = c.getLocalCounter();
        c.incrementCounter();
        Label failCheck2 = new Label("if_else"+i12);
        IfGoto if2 = new IfGoto(negative, failCheck2);
        ret.add(if2);

        //if pass check
        int i13 = c.getLocalCounter();
        c.incrementCounter();
        Label passCheck2 = new Label("if_end"+i13);
        Goto g2 = new Goto(passCheck2);
        ret.add(g2);

        //if fail check
        LabelInstr failCheck_i2 = new LabelInstr(failCheck2);
        ret.add(failCheck_i2);
        String arrayOutOfBounds2 = "\"array index out of bounds\"";
        ErrorMessage e2 = new ErrorMessage(arrayOutOfBounds2);
        ret.add(e2);

        //goto next instruction....
        LabelInstr passCheck_i2 = new LabelInstr(passCheck2);
        ret.add(passCheck_i2);

        //BOUNDS CHECK/////////////////////////////////////////////////////

        //get length
        int i6 = c.getLocalCounter();
        c.incrementCounter();
        Identifier arrayLength = new Identifier("v"+i6);
        Load loadLength = new Load(arrayLength, base, 0);
        ret.add(loadLength);

        //make comparison
        int i7 = c.getLocalCounter();
        c.incrementCounter();
        Identifier inBound = new Identifier("v"+i7);
        LessThan boundCheck = new LessThan(inBound, arrayIndex, arrayLength);
        ret.add(boundCheck);

        //create if statement
        int i8 = c.getLocalCounter();
        c.incrementCounter();
        Label failCheck = new Label("if_else"+i8);
        IfGoto if1 = new IfGoto(inBound, failCheck);
        ret.add(if1);

        //if pass check
        int i9 = c.getLocalCounter();
        c.incrementCounter();
        Label passCheck = new Label("if_end"+i9);
        Goto g1 = new Goto(passCheck);
        ret.add(g1);

        //if fail check
        LabelInstr failCheck_i = new LabelInstr(failCheck);
        ret.add(failCheck_i);
        String arrayOutOfBounds = "\"array index out of bounds\"";
        ErrorMessage e = new ErrorMessage(arrayOutOfBounds);
        ret.add(e);

        //goto next instruction....
        LabelInstr passCheck_i = new LabelInstr(passCheck);
        ret.add(passCheck_i);

        //SCALE INDEX//////////////////////////////////////////////////////

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Identifier four = new Identifier("v"+i3);
        Move_Id_Integer moveFour = new Move_Id_Integer(four, 4);
        ret.add(moveFour);

        int i4 = c.getLocalCounter();
        c.incrementCounter();
        Identifier finalOffset = new Identifier("v"+i4);
        Multiply multFour = new Multiply(finalOffset, arrayIndex, four);
        Add addFour = new Add(finalOffset, finalOffset, four);

        ret.add(multFour);
        ret.add(addFour);

        int i5 = c.getLocalCounter();
        c.incrementCounter();
        Identifier finalPointer = new Identifier("v"+i5);
        Add addOffset = new Add(finalPointer, base, finalOffset);
        ret.add(addOffset);


        //GET VAL///////////////////////////////////////////////////////////

        Load load_from_arr = new Load(lhs, finalPointer, 0);
        ret.add(load_from_arr);

        return ret;
    }

    @Override
    //f0 = Primary Expression
    //f1 = .
    //f2 = length
    public ArrayList<Instruction> visit(ArrayLength n, Context c) {
        int top = c.popStack();
        String lhs_name = "v" + top;
        Identifier lhs = new Identifier(lhs_name);
        ArrayList<Instruction> ret;

        //GET POINTER TO START OF ARRAY ////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        c.checkInitializeOn();
        ret = n.f0.accept(this,c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier("v"+i1);

        //GET VAL///////////////////////////////////////////////////////////
        Load load_from_arr = new Load(lhs, argu1, 0);
        ret.add(load_from_arr);


        return ret;
    }

    @Override
    //method call ????
    //f0 = Primary Expression
    //f1 = "."
    //f2 = Identifier
    //f3 = "("
    //f4 = Expression List
    //f5 = ")"

    //NOT DONE YET
    public ArrayList<Instruction> visit(MessageSend n, Context c) {
        int top = c.popStack();
        String lhs_name = "v" + top;
        Identifier lhs = new Identifier(lhs_name);
        ArrayList<Instruction> ret;

        //GET POINTER TO VARIABLE///////////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        c.checkInitializeOn();
        ret = n.f0.accept(this,c);
        c.checkInitializeOff();
        Identifier argu1 = new Identifier("v"+i1);

        //GET OBJECT////////////////////////////////////////////////////////
        NameGetter ng = new NameGetter();
        String objType = n.f0.accept(ng, c);
        TypeClass obj = c.getClass(objType);

        //GET FUNCTION//////////////////////////////////////////////////////
        String methodName = n.f2.f0.tokenImage;
        int methodOffset = obj.getMethodOffset(methodName);

        int i2 = c.getLocalCounter();
        c.incrementCounter();
        Identifier vmt_address = new Identifier("vmt_address"+i2);

        Load load1 = new Load(vmt_address, argu1, 0);
        ret.add(load1);

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Identifier func = new Identifier("f"+i3);
        Load load2 = new Load(func, vmt_address, methodOffset);
        ret.add(load2);

        //GET PARAMS/////////////////////////////////////////////////////////
        ArrayList<Identifier> identifiers = new ArrayList<>();
        identifiers.add(argu1);
        ParamVisitor pv = new ParamVisitor();
        ParamWrapper w = n.f4.accept(pv, c);

        ArrayList<Instruction> w_instructions = w.getInstructions();
        ArrayList<Identifier> w_identifiers = w.getIdentifiers();
        identifiers.addAll(w_identifiers);

        ret.addAll(w_instructions);


        //CALL METHOD////////////////////////////////////////////////////////
        Call methodCall = new Call(lhs, func, identifiers);
        ret.add(methodCall);

        return ret;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRIMARY EXPRESSIONS//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    // Types of Expressions
    // Integer DONE
    // True DONE
    // False DONE
    // Identifier
    // This DONE
    // new int[x] DONE
    // new id() DONE
    // ! Expression DONE
    // (expression) DONE
    public ArrayList<Instruction> visit(PrimaryExpression n, Context c) {
        return n.f0.accept(this, c);
    }

    @Override
    public ArrayList<Instruction> visit(IntegerLiteral n, Context c) {

        String f0 = n.f0.tokenImage;
        int rhs = Integer.parseInt(f0);
        int top = c.popStack();

        String name = "v" + top;
        Identifier lhs = new Identifier(name);

        Move_Id_Integer m1 = new Move_Id_Integer(lhs, rhs);

        ArrayList<Instruction> ret = new ArrayList<>();
        ret.add(m1);
        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(TrueLiteral n, Context c) {
        int top = c.popStack();
        String name = "v" + top;
        Identifier lhs = new Identifier(name);

        Move_Id_Integer m1 = new Move_Id_Integer(lhs, 1);
        ArrayList<Instruction> ret = new ArrayList<>();
        ret.add(m1);
        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(FalseLiteral n, Context c) {
        int top = c.popStack();
        String name = "v" + top;
        Identifier lhs = new Identifier(name);

        Move_Id_Integer m1 = new Move_Id_Integer(lhs, 0);
        ArrayList<Instruction> ret = new ArrayList<>();
        ret.add(m1);
        return ret;
    }

    @Override
    public ArrayList<Instruction> visit(cs132.minijava.syntaxtree.Identifier n, Context c) {
        int top = c.popStack();
        String name = "v" + top;
        Identifier lhs = new Identifier(name);
        ArrayList<Instruction> ret = new ArrayList<>();

        String f0 = n.f0.tokenImage;
        String variableType = c.getVariableType(f0);

            //LOCAL/////////////////////////////////////////////////////////////
        if (variableType.equals("local")) {

            //NULL CHECK
            boolean isInitialized = c.isLocalInitialized(f0);
            int i11 = c.getLocalCounter();
            c.incrementCounter();
            Identifier initialized_i = new Identifier("v"+i11);
            if (isInitialized) {
                Move_Id_Integer moveTrue = new Move_Id_Integer(initialized_i, 1);
                ret.add(moveTrue);
            } else {
                //System.out.println("not initialized " + varname);
                Move_Id_Integer moveFalse = new Move_Id_Integer(initialized_i, 0);
                ret.add(moveFalse);
            }

            int i12 = c.getLocalCounter();
            c.incrementCounter();
            Label nullError = new Label("null_error"+i12);
            IfGoto nullCheck = new IfGoto(initialized_i, nullError);
            ret.add(nullCheck);

            int i13 = c.getLocalCounter();
            c.incrementCounter();
            Label pastNullCheck = new Label("no_null_error"+i13);
            Goto gotoPastNullCheck = new Goto(pastNullCheck);
            ret.add(gotoPastNullCheck);

            LabelInstr nullError_i = new LabelInstr(nullError);
            ret.add(nullError_i);
            String null_message = "\"null pointer\"";
            ErrorMessage null_message_e = new ErrorMessage(null_message);
            ret.add(null_message_e);

            LabelInstr pastNullCheck_i = new LabelInstr(pastNullCheck);
            ret.add(pastNullCheck_i);


            //ASSIGN///////////////////////////////////////////////////////////
            Identifier rhs = new Identifier(f0);
            Move_Id_Id assign = new Move_Id_Id(lhs, rhs);
            ret.add(assign);

            return ret;

            //PARAM/////////////////////////////////////////////////////////////
        } else if (variableType.equals("param")) {
            Identifier rhs = new Identifier(f0);
            Move_Id_Id assign = new Move_Id_Id(lhs, rhs);
            ret.add(assign);
            return ret;

            //FIELD/////////////////////////////////////////////////////////////
        } else if (variableType.equals("field")) {

            //GET OFFSET////////////////////////////////////////////////////////
            TypeClass currClass = c.getCurrClass();
            int fieldOffset = currClass.getFieldOffset(f0);

            int i1 = c.getLocalCounter();
            c.incrementCounter();
            Identifier fieldOffset_i = new Identifier("v"+i1);
            Move_Id_Integer assignOffset = new Move_Id_Integer(fieldOffset_i, fieldOffset);
            ret.add(assignOffset);

            //SCALE OFFSET//////////////////////////////////////////////////////
            int i2 = c.getLocalCounter();
            c.incrementCounter();
            Identifier four = new Identifier("v"+i2);
            Move_Id_Integer assignFour = new Move_Id_Integer(four, 4);
            ret.add(assignFour);

            /*
            int i3 = c.getLocalCounter();
            c.incrementCounter();
            Identifier eight = new Identifier("v"+i3);
            Move_Id_Integer assignEight = new Move_Id_Integer(eight, 8);
            ret.add(assignEight);

             */

            Add addFour = new Add(fieldOffset_i, fieldOffset_i, four);
            ret.add(addFour);

            int i4 = c.getLocalCounter();
            c.incrementCounter();
            Identifier fieldPointer = new Identifier("v"+i4);

            //LOAD FIELD////////////////////////////////////////////////////////
            Identifier base = new Identifier("this");
            Add addOffset = new Add(fieldPointer, base, fieldOffset_i);
            ret.add(addOffset);




            //NULL CHECK////////////////////////////////////////////////////////

            if (c.isCheckInitialize()) {

                //System.out.println(f0);

                int i10 = c.getLocalCounter();
                c.incrementCounter();
                Identifier fieldInitialized = new Identifier("v"+i10);

                Move_Id_Id copyAddress = new Move_Id_Id(fieldInitialized, fieldPointer);
                Add addFour2 = new Add(fieldInitialized, fieldInitialized, four);

                ret.add(copyAddress);
                ret.add(addFour2);

                int i11 = c.getLocalCounter();
                c.incrementCounter();
                Identifier isInitialized = new Identifier("v"+i11);

                Load loadInitialized = new Load(isInitialized, fieldInitialized, 0);
                ret.add(loadInitialized);

                int i12 = c.getLocalCounter();
                c.incrementCounter();
                Label nullError = new Label("null_error"+i12);
                IfGoto nullCheck = new IfGoto(isInitialized, nullError);
                ret.add(nullCheck);

                int i13 = c.getLocalCounter();
                c.incrementCounter();
                Label pastNullCheck = new Label("no_null_error"+i13);
                Goto gotoPastNullCheck = new Goto(pastNullCheck);
                ret.add(gotoPastNullCheck);

                LabelInstr nullError_i = new LabelInstr(nullError);
                ret.add(nullError_i);
                String null_message = "\"null pointer\"";
                ErrorMessage null_message_e = new ErrorMessage(null_message);
                ret.add(null_message_e);

                LabelInstr pastNullCheck_i = new LabelInstr(pastNullCheck);
                ret.add(pastNullCheck_i);
            }

            ///////////////////////////////////////////////////////////////////
            Load loadField = new Load(lhs, fieldPointer, 0);
            ret.add(loadField);

            return ret;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Instruction> visit(ThisExpression n, Context c) {
        int top = c.popStack();
        String name = "v" + top;
        Identifier lhs = new Identifier(name);

        Identifier this_i = new Identifier("this");
        Move_Id_Id m1 = new Move_Id_Id(lhs, this_i);
        ArrayList<Instruction> ret = new ArrayList<>();
        ret.add(m1);
        return ret;
    }

    @Override
    //f0 = new
    //f1 = int
    //f2 = "["
    //f3 = Expression
    //f4 = "]"
    public ArrayList<Instruction> visit(ArrayAllocationExpression n, Context c) {
        int top = c.popStack();
        Identifier array_start = new Identifier("v"+top);

        //EVALUATE EXPRESSION //////////////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        ArrayList<Instruction> ret = n.f3.accept(this, c);
        Identifier allocSize = new Identifier("v"+i1);

        //CHECK LESS THAN ZERO//////////////////////////////////////////////
        int i10 = c.getLocalCounter();
        c.incrementCounter();
        Identifier zero = new Identifier("v"+i10);
        Move_Id_Integer assignZero = new Move_Id_Integer(zero, 0);
        ret.add(assignZero);

        //make comparison
        int i11 = c.getLocalCounter();
        c.incrementCounter();
        Identifier inBound = new Identifier("v"+i11);
        LessThan boundCheck = new LessThan(inBound, zero, allocSize);
        ret.add(boundCheck);

        //create if statement
        int i12 = c.getLocalCounter();
        c.incrementCounter();
        Label failCheck = new Label("if_else"+i12);
        IfGoto if1 = new IfGoto(inBound, failCheck);
        ret.add(if1);

        //if pass check
        int i13 = c.getLocalCounter();
        c.incrementCounter();
        Label passCheck = new Label("if_end"+i13);
        Goto g1 = new Goto(passCheck);
        ret.add(g1);

        //if fail check
        LabelInstr failCheck_i = new LabelInstr(failCheck);
        ret.add(failCheck_i);
        String arrayOutOfBounds = "\"array index out of bounds\"";
        ErrorMessage e = new ErrorMessage(arrayOutOfBounds);
        ret.add(e);

        //goto next instruction....
        LabelInstr passCheck_i = new LabelInstr(passCheck);
        ret.add(passCheck_i);



        //ALLOCATE SPACE ///////////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        Identifier allocSizeFinal = new Identifier("v"+i2);

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Identifier four = new Identifier("v"+i3);
        Move_Id_Integer moveFour = new Move_Id_Integer(four, 4);
        ret.add(moveFour);


        Multiply multFour = new Multiply(allocSizeFinal, allocSize, four);
        Add addFour = new Add(allocSizeFinal, allocSizeFinal, four);

        ret.add(multFour);
        ret.add(addFour);


        Alloc alloc1 = new Alloc(array_start, allocSizeFinal);
        ret.add(alloc1);

        //ADD LENGTH///////////////////////////////////////////////////////
        Store store1 = new Store(array_start, 0, allocSize);
        ret.add(store1);

        return ret;
    }

    @Override
    //f0 = new
    //f1 = Identifier
    //f2 = "("
    //f3 = ")"
    public ArrayList<Instruction> visit(AllocationExpression n, Context c) {

        int top = c.popStack();
        Identifier obj = new Identifier("v"+top);
        ArrayList<Instruction> ret = new ArrayList<>();

        //OBJECT////////////////////////////////////////////////////////////
        String f1 = n.f1.f0.tokenImage;
        TypeClass type = c.getClass(f1);
        int numFields = type.getNumFields();
        int numMethods = type.getNumMethods();
        int sz1 = 8*numFields + 4;
        int sz2 = 4*(numMethods);

        //ALLOCATE SPACE////////////////////////////////////////////////////
        int i1 =  c.getLocalCounter();
        c.incrementCounter();
        Identifier size1 = new Identifier("v"+i1);
        Move_Id_Integer m1 = new Move_Id_Integer(size1, sz1);
        ret.add(m1);

        int i2 =  c.getLocalCounter();
        c.incrementCounter();
        Identifier size2 = new Identifier("v"+i2);
        Move_Id_Integer m2 = new Move_Id_Integer(size2, sz2);
        ret.add(m2);

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Identifier vmt_address = new Identifier("vmt_address"+i3);

        Alloc a1 = new Alloc(obj, size1);
        ret.add(a1);

        Alloc a2 = new Alloc(vmt_address, size2);
        ret.add(a2);


        //POPULATE TABLE////////////////////////////////////////////////////
        Store s = new Store(obj, 0, vmt_address);
        ret.add(s);

        ArrayList<String> methodNames = type.methodNames2();
        for (String mn: methodNames) {
            int i = c.getLocalCounter();
            c.incrementCounter();
            Identifier id_i = new Identifier("f"+i);
            String methodParent = type.getMethodParent(mn);
            FunctionName fn_i = new FunctionName(methodParent+"_"+mn);
            Move_Id_FuncName move_i = new Move_Id_FuncName(id_i, fn_i);
            ret.add(move_i);

            int offset_i = type.getMethodOffset(mn);
            //System.out.println(offset_i);
            //System.out.println(f1+"_"+mn);
            Store s_i = new Store(vmt_address, offset_i, id_i);
            ret.add(s_i);
        }


        return ret;
    }

    @Override
    //f0 = "!"
    //f1 = Expression -> boolean
    public ArrayList<Instruction> visit(NotExpression n, Context c) {
        int top = c.popStack();
        Identifier not_bool = new Identifier("v"+top);
        ArrayList<Instruction> ret;

        //EVALUATE EXPRESSION///////////////////////////////////////////////
        int i1 = c.getLocalCounter();
        c.incrementCounter();
        c.pushStack(i1);
        ret = n.f1.accept(this, c);
        Identifier bool = new Identifier("v"+i1);


        //IF STATEMENT//////////////////////////////////////////////////////
        int i2 = c.getLocalCounter();
        c.incrementCounter();
        Label if_else_i = new Label("if_else_"+i2);
        IfGoto ig1 = new IfGoto(bool, if_else_i);
        ret.add(ig1);

        //IF TRUE///////////////////////////////////////////////////////////
        Move_Id_Integer assign0 = new Move_Id_Integer(not_bool, 0);
        ret.add(assign0);

        int i3 = c.getLocalCounter();
        c.incrementCounter();
        Label if_end_i = new Label("if_end_"+i3);
        Goto goto_end = new Goto(if_end_i);
        ret.add(goto_end);

        //IF FALSE//////////////////////////////////////////////////////////
        LabelInstr li = new LabelInstr(if_else_i);
        ret.add(li);
        Move_Id_Integer assign1 = new Move_Id_Integer(not_bool, 1);
        ret.add(assign1);

        LabelInstr end_i = new LabelInstr(if_end_i);
        ret.add(end_i);

        return ret;
    }

    @Override
    //f0 = "("
    //f1 = Expression
    //f2 = ")"
    public ArrayList<Instruction> visit(BracketExpression n, Context c) {
        return n.f1.accept(this, c);
    }
}
