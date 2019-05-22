package lexer;

import java.util.HashMap;
import java.util.Map;

import static lexer.TokenType.*;

public class Lexer {

    private SourceBuffer source;
    private int line;  // current line number
    private int start; // beginning index of the token.

    private static final Map<String, TokenType> keywords;
    private static final Map<String, TokenType> data_types;


    static {
        keywords = new HashMap<>();
        keywords.put("if",     IF);
        keywords.put("else",   ELSE);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("while",  WHILE);

        data_types = new HashMap<>();
        data_types.put("int",     INT);
        data_types.put("double", DOUBLE);
        data_types.put("char",  CHAR);
        data_types.put("void", VOID);
    }

    public Lexer(SourceBuffer buff) {
        this.source = buff;
        this.line = 1;
    }

    public Token scan() {
        char ch = source.next();
        ch  = skipWhitespace(ch);
        // Encounter eof.
        if(ch == '\0') return new Token("", EOF, line, true);
        start = source.getOffset()-1;

        if(Character.isLetter(ch)) return identifier();
        if(Character.isDigit((ch))) return number();
        if(ch == '\'') return charLiteral();
        if(ch == '\"') return stringLiteral();

        switch (ch) {
            case '+': return makeToken(source.peek('=') ? ADD_ASSIGN : ADD);
            case '-': return makeToken(source.peek('=') ? SUB_ASSIGN : SUB);
            case '*': return makeToken(source.peek('=') ? MULTI_ASSIGN : MULTI);
            case '/': return makeToken(source.peek('=') ? DIV_ASSIGN : DIV);
            case '%': return makeToken(source.peek('=') ? MOD_ASSIGN : MOD);
            case '!': return makeToken(source.peek('=') ? NOT_EQUAL : REL_NOT);
            case '=': return makeToken(source.peek('=') ? EQUAL : ASSIGN);
            case '^': return makeToken(source.peek('=') ? XOR_ASSIGN : XOR);

            case '&': return makeToken('=', '&', AND_ASSIGN, REL_AND, AND);
            case '|': return makeToken('=', '|', OR_ASSIGN, REL_OR, OR);
            case '>': return makeToken('=', '>', GREATER_EQUAL, RIGHT_SHIFT, GREATER);
            case '<': return makeToken('=', '<', LESS_EQUAL, LEFT_SHIFT, LESS);

            case '~': return makeToken(COMPL);
            case '(': return makeToken(LEFT_PAREN);
            case ')': return makeToken(RIGHT_PAREN);
            case '[': return makeToken(LEFT_BRACKET);
            case ']': return makeToken(RIGHT_BRACKET);
            case '{': return makeToken(LEFT_BRACE);
            case '}': return makeToken(RIGHT_BRACE);
            case ',': return makeToken(COMMA);
            case ';': return makeToken(SEMICOLON);
        }

        // TODO: log error.
        return makeToken(UNKNOWN);
    }

    // Skip any white space and count line number.
    private char skipWhitespace(char ch) {
        while(true) {
            if(ch == ' ' || ch == '\t') {
                ch = source.next();
            }
            else if(ch == '\n') {
                ch = source.next();
                line += 1;
            }
            else {
                break;
            }
        }
        return ch;
    }

    private Token identifier() {
        char ch;
        do {
            ch = source.next();
        } while(Character.isLetterOrDigit(ch) || ch == '_');
        source.back();
        String token_value = source.substring(start, source.getOffset());
        if(keywords.containsKey(token_value))
            return new Token(token_value, keywords.get(token_value), line, true);
        else if(data_types.containsKey(token_value))
            return new Token(token_value, data_types.get(token_value), line, true);
        else
            return new Token(token_value, IDENTIFIER, line, true);
    }

    private Token number() {
        char ch;
        do {
            ch = source.next();
        } while (Character.isDigit(ch));

        // Determine whether it is a real number.
        if(ch != '.') {
            source.back();
            String token_value = source.substring(start, source.getOffset());
            return new Token(token_value, INTEGER_CONSTANT, line, true);
        }

        // construct real number.
        do {
            ch = source.next();
        } while(Character.isDigit((ch)));
        source.back();

        String token_value = source.substring(start, source.getOffset());
        return new Token(token_value, DOUBLE_CONSTANT, line, true);
    }

    private Token charLiteral() {
        char ch;
        ch = source.next();
        while(ch != '\'' && ch != '\n' && ch != '\0') {
            ch = source.next();
        }
        String token_value = source.substring(start, source.getOffset());
        if(ch != '\'') {
            return new Token(token_value, CHARACTER_CONSTANT, line, false);
        } else {
            return new Token(token_value, CHARACTER_CONSTANT, line, true);
        }
    }

    private Token stringLiteral() {
        char ch;
        ch = source.next();
        while(ch != '\"' && ch != '\n' && ch != '\0') {
            ch = source.next();
        }
        String token_value = source.substring(start, source.getOffset());
        if(ch != '\"') {
            return new Token(token_value, STRING, line, false);
        } else {
            return new Token(token_value, STRING, line, true);
        }
    }

    private Token makeToken(TokenType type) {
        String value = source.substring(start, source.getOffset());
        return new Token(value, type, line, true);
    }

    private Token makeToken(char case1, char case2, TokenType type1, TokenType type2, TokenType type0) {
        TokenType type;
        if(source.peek(case1))
            type = type1;
        else if(source.peek(case2))
            type = type2;
        else
            type = type0;
        return makeToken(type);
    }
}
