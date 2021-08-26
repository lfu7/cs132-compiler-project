package Support;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import Support.Terminal.TerminalType;

public class Tokenizer {

    private static String input;
    
    public Tokenizer(String input) {
	this.input = input;
	
    }
    
    
    public static List<Terminal> tokenize() {
	
	List<Terminal> ret = new ArrayList<>();

	if (input == "") {
	    //Terminal eof = new Terminal(TerminalType.EOF);
	    Terminal eof = new Terminal(TerminalType.EOF, "eof");
	    ret.add(eof);
	    return ret; 
	}
	
	int currIndex = 0;

	//stores the start index of the current substring
	int lastIndex = 0;
	int inputLength = input.length();
	String currString = "";


	// does not deal with white spaces
	/*
	while (currIndex <= inputLength) {
	    currString = input.substring(lastIndex, currIndex);

	    Terminal currTerminal = terminalMap.get(currString);

	    
	    
	    if (currTerminal == null) {

		while (currIndex <= inputLength && terminalMap.get(input.substring(lastIndex, currIndex)) == null) {
		    currIndex++;
		}
		
		//do {
		    //System.out.println(input.substring(lastIndex, currIndex));
		    //currIndex++;
		    //System.out.println(input.substring(lastIndex, currIndex));
		//} while (currIndex <= inputLength && terminalMap.get(input.substring(lastIndex, currIndex)) == null);
	    } else {
		ret.add(currTerminal);
		lastIndex = currIndex;
		currIndex++;
	    }
	}
	*/

	
	// new tokenizer fixes problem with stitching letters together when separated by whitespace, still 93%....
	// misses last character when input read in from buffered reader????

	/*
	while (currIndex < inputLength) {
	    currString = input.substring(lastIndex, currIndex);
	    //System.out.println(currString);
		
	    Terminal currTerminal = terminalMap.get(currString);

	    if (currTerminal == null) {
		while (currIndex < inputLength && terminalMap.get(input.substring(lastIndex, currIndex)) == null) {

		    //System.out.println(input.substring(lastIndex, currIndex));
		    currIndex++;
		    //System.out.println(input.substring(lastIndex, currIndex));
		}
	    } else {
		ret.add(currTerminal);

                while (currIndex < inputLength && isWhiteSpace(input.charAt(currIndex))) {
		    currIndex++;
		}
		lastIndex = currIndex;
	    }
	}
	*/

	//new tokenizer, resolved issues with last character
	while (currIndex <= inputLength) {
	    //System.out.println(lastIndex);
	    //System.out.println(currIndex);
	    currString = input.substring(lastIndex, currIndex);

	    Terminal currTerminal = terminalMap.get(currString);
	    if (currTerminal == null) {

		//needed to break out of infinite loop while counting last character
		if (currIndex >= inputLength) {
		    return null;
		}
		while (currIndex < inputLength && terminalMap.get(input.substring(lastIndex, currIndex)) == null) {
		    currIndex++;
		}
	    } else {
		ret.add(currTerminal);
		while (currIndex < inputLength && isWhiteSpace(input.charAt(currIndex))) {
                    currIndex++;
                }
                lastIndex = currIndex;
		currIndex ++;
	    }
	}
	
	
	
	//System.out.println("BREAK BREAK BREAK");
	
	
	if (ret.isEmpty() && (input.length() > 0)) {
	    return null;
	}

	//Terminal eof = new Terminal(TerminalType.EOF);
	Terminal eof = new Terminal(TerminalType.EOF, "eof");
	ret.add(eof);
	return ret;
    }

    private static Boolean isWhiteSpace(char s) {
        if (s == ' ' || s == '\n') {
            return true;
        }

	return false;
    }
    
    
    	
    private static Map<String, Terminal> generateMap () {

	HashMap<String, Terminal> map = new HashMap<>();

	/*
	map.put("{", new Terminal(TerminalType.LEFT_CURLY));
	map.put("}", new Terminal(TerminalType.RIGHT_CURLY));
	map.put("System.out.println", new Terminal(TerminalType.SYSTEM_OUT_PRINTLN));
	map.put("(", new Terminal(TerminalType.LEFT_PAREN));
	map.put(")", new Terminal(TerminalType.RIGHT_PAREN));
        map.put(";", new Terminal(TerminalType.SEMICOLON));
	map.put("if", new Terminal(TerminalType.IF));
	map.put("else", new Terminal(TerminalType.ELSE));
	map.put("while", new Terminal(TerminalType.WHILE));
	map.put("true", new Terminal(TerminalType.TRUE));
	map.put("false", new Terminal(TerminalType.FALSE));
	map.put("!", new Terminal(TerminalType.NEGATE));
	//map.put("", new Terminal(TerminalType.EOF));
	*/

	//added strings to debug tokenizer, remove if program is too slow
	map.put("{", new Terminal(TerminalType.LEFT_CURLY, "{"));
        map.put("}", new Terminal(TerminalType.RIGHT_CURLY, "}"));
        map.put("System.out.println", new Terminal(TerminalType.SYSTEM_OUT_PRINTLN, "System.out.println"));
        map.put("(", new Terminal(TerminalType.LEFT_PAREN, "("));
        map.put(")", new Terminal(TerminalType.RIGHT_PAREN, ")"));
        map.put(";", new Terminal(TerminalType.SEMICOLON, ";"));
        map.put("if", new Terminal(TerminalType.IF, "if"));
        map.put("else", new Terminal(TerminalType.ELSE, "else"));
        map.put("while", new Terminal(TerminalType.WHILE, "while"));
        map.put("true", new Terminal(TerminalType.TRUE, "true"));
        map.put("false", new Terminal(TerminalType.FALSE, "false"));
        map.put("!", new Terminal(TerminalType.NEGATE, "!"));
	
	
	return map;
	
    }

    private static Map<String, Terminal> terminalMap = generateMap();
	
};
