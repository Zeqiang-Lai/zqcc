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

    public Token(int number, String value, TokenType type, int line, boolean valid) {
        this.number = number;
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

    static public Token fromXML(String[] strs) {
        int number = -1;
        String value = null;
        TokenType type = null;
        int line = -1;
        boolean valid = true;

        for(String str : strs) {
            str = str.trim();
            if(str.startsWith("<token>") || str.startsWith("</token>"))
                continue;
            if(str.startsWith("<number>") && str.endsWith("</number>"))
                number = Integer.valueOf(str.substring(8, str.length()-9));
            else if(str.startsWith("<value>") && str.endsWith("</value>"))
                value = str.substring(7, str.length()-8);
            else if(str.startsWith("<type>") && str.endsWith("</type>"))
                type = TokenType.valueOf(str.substring(6, str.length()-7));
            else if(str.startsWith("<line>") && str.endsWith("</line>"))
                line = Integer.valueOf(str.substring(6, str.length()-7));
            else if(str.startsWith("<valid>") && str.endsWith("</valid>"))
                valid = Boolean.valueOf(str.substring(7, str.length()-8));
            else
                throw new IllegalArgumentException("invalid xml string: " + str);
        }
        if(number == -1 || value == null || type == null || line == -1)
            throw new IllegalArgumentException("invalid xml string: " + strs);

        return new Token(number, value, type, line, valid);
    }


}
