package lexer;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    COMMA, SEMICOLON, COMPL,

    // One or two character tokens.
    ADD, ADD_ASSIGN,
    SUB, SUB_ASSIGN,
    MULTI, MULTI_ASSIGN,
    DIV, DIV_ASSIGN,
    MOD, MOD_ASSIGN,
    AND, AND_ASSIGN, REL_AND,
    OR, OR_ASSIGN, REL_OR,
    XOR, XOR_ASSIGN,

    REL_NOT, NOT_EQUAL,
    ASSIGN, EQUAL,
    GREATER, GREATER_EQUAL, RIGHT_SHIFT,
    LESS, LESS_EQUAL, LEFT_SHIFT,

    // Literals.
    IDENTIFIER, STRING, INTEGER_CONSTANT, DOUBLE_CONSTANT, CHARACTER_CONSTANT,

    // Keywords.
    IF, ELSE, WHILE, RETURN, PRINT, BREAK, CONTINUE,

    // Types
    INT, DOUBLE, CHAR, VOID,

    EOF,
    UNKNOWN;

    static public Map<TokenType, String> value = new HashMap<>();

    static {
        value.put(LEFT_PAREN, "(");
        value.put(RIGHT_PAREN, ")");
        value.put(LEFT_BRACE, "{");
        value.put(RIGHT_BRACE, "}");
        value.put(LEFT_BRACKET, "[");
        value.put(RIGHT_BRACKET, "]");
        value.put(SEMICOLON, ";");
        value.put(ASSIGN, "=");
    }
}
