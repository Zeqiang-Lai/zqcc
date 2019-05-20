package lexer;

import utils.XMLPrintable;

public class Token implements XMLPrintable {
    public int number;
    public String value;
    public TokenType type;
    public int line;
    public boolean valid;

    public static int count = 0;

    public Token(String value, TokenType type, int line, boolean valid) {
        this.number = Token.count;
        Token.count += 1;

        this.value = value;
        this.type = type;
        this.line = line;
        this.valid = valid;
    }

    @Override
    public String toString() {
        return number + ":" +
                " type = " + type +
                " value= " + value +
                " line= " + line +
                " valid= " + valid;
    }

    public String toXMLString() {
        StringBuffer str = new StringBuffer();
        str.append("<token>\n");
        str.append("<number>" + this.number + "</number>\n");
        str.append("<value>" + this.value + "</value>\n");
        str.append("<type>" + this.type + "</type>\n");
        str.append("<line>" + this.line + "</line>\n");
        str.append("<valid>" + this.valid + "</valid>\n");
        str.append("</token>");
        return new String(str);
    }
}
