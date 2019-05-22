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

    public String toXMLString(int indent) {
        StringBuffer str = new StringBuffer();
        append(str, "<token>\n", indent);
        append(str, "<number>" + this.number + "</number>\n", indent+1);
        append(str, "<value>" + this.value + "</value>\n", indent+1);
        append(str,"<type>" + this.type + "</type>\n", indent+1);
        append(str,"<line>" + this.line + "</line>\n", indent+1);
        append(str,"<valid>" + this.valid + "</valid>\n", indent+1);
        append(str,"</token>\n", indent);
        return new String(str);
    }

    private String indentString(int num) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<num; ++i)
            builder.append("    ");
        return builder.toString();
    }

    private void append(StringBuffer buffer, String text, int indent) {
        buffer.append(indentString(indent));
        buffer.append(text);
    }
}
