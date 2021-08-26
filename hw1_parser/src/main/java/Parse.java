import java.util.*;
import Support.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;


public class Parse {
    public static void main (String [] args) {

	//Scanner scanner = new Scanner(System.in);

      	//String input_string = getInput(scanner);
	String input_string = "";
	
	try  {
	    input_string = _getInput();
	}
	catch (Exception e) {
	}
	
	    
        //System.out.println(input_string);
	String ns_input_string = removeWhiteSpaces(input_string);

	//delete all the whitespace/newlines before passing to tokenizer
	//String ns_input_string = input_string.replaceAll("[\\n\\t ]", "");
	//System.out.println(ns_input_string);

	Tokenizer t = new Tokenizer(ns_input_string);
	List<Terminal> terminals = t.tokenize();

	
	//for (Terminal terminal: terminals) {
	//System.out.println(terminal.getString());
        //}
	
	
	Boolean isNull = (terminals == null);
	if (isNull) {
	    System.out.println("Parse error");
	    return;
	}
		
	Parser p = new Parser(terminals);
	Boolean parse_success = p.goal();

	if (parse_success) {
	    System.out.println("Program parsed successfully");

	} else {
	    System.out.println("Parse error");
	}
    }
    /*
    private static String getInput (Scanner scanner) {

	//set delimiter to be start of string instead of whitespace
	scanner.useDelimiter("\\A");
	String ret = new String();
	while (scanner.hasNext()) {
	    String myString = scanner.next();
	    ret += myString;
        }
	
	return ret;
    }
    */

    //works differently wrt end of file???
    //question 60 on piazza
    private static String _getInput () throws Exception {
	try (InputStreamReader instream = new InputStreamReader(System.in);
	     BufferedReader buffer = new BufferedReader(instream)) {
	    String ret = "";
	    String line;
	    while ((line = buffer.readLine()) != null) {
		ret += line;
	    }
	    
	    return ret;
	} catch (Exception e) {
	    e.printStackTrace();
	    return "";
	}
    }

    //DO NOT remove white spaces in the middle of the string
    private static String removeWhiteSpaces (String input) {

	int n = input.length();

	int i1 = 0;
	int i2 = n - 1;

	while (i1 < n && isWhiteSpace(input.charAt(i1))) {
	    i1++;
	};

	while (i2 > -1 && isWhiteSpace(input.charAt(i2))) {
	    i2--;
	}

	return input.substring(i1, i2 + 1);

    }

    private static Boolean isWhiteSpace(char s) {
	if (s == ' ' || s == '\n') {
	    return true;
	}

	return false;
    }
    
}
