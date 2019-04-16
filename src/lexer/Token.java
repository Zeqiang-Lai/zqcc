package lexer;

import utils.XMLPrintable;

public class Token implements XMLPrintable {
    public int number;
    public String value;
    public String type;
    public int line;
    public boolean valid;

    public static class Type {
        public static String keyword = "keyword";
        public static String identifier = "identifier";
        public static String floatConstant = "float_constant";
        public static String integerConstant = "integer_constant";
        public static String punctuator = "punctuator";
        public static String eof = "eof";
        public static String unknown = "unknown";

        public static String lshift = "<<";
        public static String rshift = ">>";

        public static String add_assign = "+=";
        public static String sub_assign = "-=";
        public static String div_assign = "/=";
        public static String mult_assign = "*=";
        public static String mod_assign = "%=";
        public static String xor_assign = "^=";
        public static String or_assign = "|=";
        public static String and_assign = "&=";

        public static String large_equal = ">=";
        public static String less_equal = "<=";
        public static String not_equal = "!=";
        public static String equal = "==";

        public static String rel_or = "||";
        public static String rel_and = "&&";

    }

    public static int count = 0;

    public Token(String value, String type, int line, boolean valid) {
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
