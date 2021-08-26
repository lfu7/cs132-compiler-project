package Support;

import cs132.IR.sparrow.Instruction;
import cs132.IR.token.Identifier;

import java.util.ArrayList;

public class IdWrapper {
    private ArrayList<Instruction> instructions;
    private Identifier id;
    private String varType;
    private boolean isField;
    private String name;

    public IdWrapper() {
        instructions = new ArrayList<>();
        id = null;
        varType = "";
        isField = false;
        name = "";
    }

    public void setInstructions(ArrayList<Instruction> i) {
        instructions = i;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setIdentifier(Identifier i) {
        id = i;
    }

    public Identifier getIdentifier() {
        return id;
    }

    public void setIsField(boolean isField) {
        this.isField = isField;
    }

    public boolean isField()  {
        return isField;
    }


    public void setVarType(String typeName) {
        varType = typeName;
    }

    public String getVarType() {
        return varType;
    }

    public void setName(String varname) {
        name = varname;
    }
    public String getName() {
        return name;
    }

}