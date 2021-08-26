package Support;

import cs132.IR.sparrow.Instruction;
import cs132.IR.token.Identifier;

import java.util.ArrayList;

public class ParamWrapper {
    private ArrayList<Instruction> instructions;
    private ArrayList<Identifier> identifiers;

    public ParamWrapper() {
        instructions = new ArrayList<>();
        identifiers = new ArrayList<>();
    }

    public void setInstructions(ArrayList<Instruction> i) {
        instructions = i;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setIdentifiers(ArrayList<Identifier> i) {
        identifiers = i;
    }

    public ArrayList<Identifier> getIdentifiers() {
        return identifiers;
    }

}