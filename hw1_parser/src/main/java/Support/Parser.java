package Support;

import java.util.List;
import java.util.ArrayList;

import Support.Terminal.TerminalType;

public class Parser {

    private static List<Terminal> terminals;
    private static Terminal terminal;
    private static int currIndex;
    
    public Parser(List<Terminal> terminals) {
	this.terminals = terminals;
	currIndex = -1;
    }
    
    public static Boolean goal() {
	terminal = nextTerminal();
	Boolean curr = S() && eat(TerminalType.EOF);
	return curr;
    }

    private static Boolean S() {
	Boolean curr = true;

        if (terminal.getType() == TerminalType.SYSTEM_OUT_PRINTLN) {                                                             
            curr = curr && eat(TerminalType.SYSTEM_OUT_PRINTLN) && eat(TerminalType.LEFT_PAREN) && E() && eat(TerminalType.RIGHT_PAREN) && eat(TerminalType.SEMICOLON);
	    return curr;
        } else if (terminal.getType() == TerminalType.WHILE) {
	    curr = curr && eat(TerminalType.WHILE) && eat(TerminalType.LEFT_PAREN) && E() && eat(TerminalType.RIGHT_PAREN) && S();
	    return curr;
	} else if (terminal.getType() == TerminalType.IF) {
	    curr = curr && eat(TerminalType.IF) && eat(TerminalType.LEFT_PAREN) && E() && eat(TerminalType.RIGHT_PAREN) && S() && eat(TerminalType.ELSE) && S();
	    return curr;
	} else if (terminal.getType() == TerminalType.LEFT_CURLY) {
	    curr = curr && eat(TerminalType.LEFT_CURLY) && L() && eat(TerminalType.RIGHT_CURLY);
	    return curr;

	} else {
	    return false;
	}   
    }

    private static Boolean E() {
        if (terminal.getType() == TerminalType.TRUE) {

	    eat(TerminalType.TRUE);
	    return true;

	    
        } else if (terminal.getType() == TerminalType.FALSE) {
	    eat(TerminalType.FALSE);
	    return true;

	    
        } else if (terminal.getType() == TerminalType.NEGATE) {
	    eat(TerminalType.NEGATE);
	    return E();
	}
	
	else {
            return false;
        }	
    }

    private static Boolean L() {

	//epsilon case
	if (terminal.getType() == TerminalType.RIGHT_CURLY) {
            return true;
        } else {
            Boolean curr = S() && L();
            return curr;
        }
    }
    

    
    private static Boolean eat(TerminalType a) {
	//System.out.println(token.getType());
	//System.out.println(a);
	if (terminal.getType() == a) {

	    if (a == TerminalType.EOF)  {
		return true;
	    } else {
		terminal = nextTerminal();
		return true;
	    }
	} else {
	    return false;
	}
    }

    private static Terminal nextTerminal() {
	currIndex++;
	return terminals.get(currIndex);

    }
    

 }
