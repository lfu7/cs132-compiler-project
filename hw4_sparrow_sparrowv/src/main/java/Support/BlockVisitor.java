package Support;

import cs132.IR.sparrowv.Move_Id_Reg;
import cs132.IR.sparrowv.Move_Reg_Id;
import cs132.IR.syntaxtree.Block;
import cs132.IR.syntaxtree.Node;
import cs132.IR.sparrowv.Instruction;
import cs132.IR.token.Register;
import cs132.IR.visitor.GJDepthFirst;

import cs132.IR.token.Identifier;

import java.util.ArrayList;

public class BlockVisitor extends GJDepthFirst<cs132.IR.sparrowv.Block, Context> {

    @Override
    public cs132.IR.sparrowv.Block visit(Block n, Context c) {
        ArrayList<Instruction> retInstructions = new ArrayList<>();

        ArrayList<Instruction> blockInstructions = new ArrayList<>();
        InstructionVisitor iv = new InstructionVisitor();

        c.resetDfn();
        for (Node node: n.f0.nodes) {
            blockInstructions.addAll(node.accept(iv, c));
            c.cleanRegisters();
            c.incrementDfn();
        }

        ArrayList<Instruction> prologueCalleeSave = new ArrayList<>();
        ArrayList<Instruction> epilogueCalleeRestore = new ArrayList<>();
        if (c.getSaveCalleeRegisters()) {
            prologueCalleeSave.addAll(c.save_callee_registers());
            epilogueCalleeRestore.addAll(c.restore_callee_registers());
        }

        ArrayList<Instruction> saveRet = new ArrayList<>();
        String ret_name = n.f2.f0.tokenImage;
        Identifier ret_id = new Identifier(ret_name);
        if (!c.getVarLocation(ret_name).equals("stack")) {
            String registerId = c.getRegisterId(ret_name);
            Register ret_r = new Register(registerId);
            Move_Id_Reg saveRetToStack = new Move_Id_Reg(ret_id, ret_r);
            saveRet.add(saveRetToStack);
        }


        retInstructions.addAll(prologueCalleeSave);
        retInstructions.addAll(blockInstructions);
        retInstructions.addAll(saveRet);
        retInstructions.addAll(epilogueCalleeRestore);



        cs132.IR.sparrowv.Block ret = new cs132.IR.sparrowv.Block(retInstructions, ret_id);

        return ret;
    }
}