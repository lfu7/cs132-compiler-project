package Support;

public class Terminal {

    public static enum TerminalType {
        LEFT_CURLY,
        RIGHT_CURLY,
        SYSTEM_OUT_PRINTLN,
        LEFT_PAREN,
        RIGHT_PAREN,
        SEMICOLON,
        IF,
        ELSE,
        WHILE,
        TRUE,
        FALSE,
        NEGATE,
	EOF
    };
    

    private TerminalType t_type;
    private String t_string;

    public Terminal(TerminalType t_type,String t_string) {
	this.t_type = t_type;
        this.t_string = t_string;

    }

    
    public String getString() {
	return t_string;
    }

    public TerminalType getType() {
	return t_type;
    }
    
    
};
