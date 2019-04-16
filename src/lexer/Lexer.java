package lexer;

public class Lexer {

    public Lexer(SourceBuffer buff) {
        this.sBuff = buff;
        this.current_line_num = 1;
    }

    public Token scan() {

        String token_value;
        char cur_ch = sBuff.next();

        // Encounter eof.
        if(cur_ch == '\0') {
            return new Token("", Token.Type.eof, current_line_num, true);
        }

        // Skip any white space and count line number.
        while(true) {
            if(cur_ch == ' ' || cur_ch == '\t') {
                cur_ch = sBuff.next();
            }
            else if(cur_ch == '\n') {
                cur_ch = sBuff.next();
                current_line_num += 1;
            }
            else {
                break;
            }
        }

        beginIndex = sBuff.getOffset()-1;

        // Try to detect identifier.
        if(Character.isLetter(cur_ch)) {
            do {
                cur_ch = sBuff.next();
            } while(Character.isLetterOrDigit(cur_ch) || cur_ch == '_');
            sBuff.back();
            token_value = sBuff.substring(beginIndex, sBuff.getOffset());
            return new Token(token_value, Token.Type.identifier, current_line_num, true);
        }

        // Try to detect number.
        if(Character.isDigit((cur_ch))) {
            do {
                cur_ch = sBuff.next();
            } while (Character.isDigit(cur_ch));

            // Determine whether it is a real number.
            if(cur_ch != '.') {
                sBuff.back();
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, Token.Type.integerConstant, current_line_num, true);
            }

            // construct real number.
            do {
                cur_ch = sBuff.next();
            } while(Character.isDigit((cur_ch)));
            sBuff.back();

            token_value = sBuff.substring(beginIndex, sBuff.getOffset());
            return new Token(token_value, Token.Type.floatConstant, current_line_num, true);
        }

        // Try to detect character literal
        if(cur_ch == '\'') {
            cur_ch = sBuff.next();
            while(cur_ch != '\'' && cur_ch != '\n' && cur_ch != '\0') {
                cur_ch = sBuff.next();
            }
            token_value = sBuff.substring(beginIndex, sBuff.getOffset());
            if(cur_ch != '\'') {
                return new Token(token_value, token_value, current_line_num, false);
            } else {
                return new Token(token_value, token_value, current_line_num, true);
            }
        }

        // Try to detect string literal
        if(cur_ch == '\"') {
            cur_ch = sBuff.next();
            while(cur_ch != '\"' && cur_ch != '\n' && cur_ch != '\0') {
                cur_ch = sBuff.next();
            }
            token_value = sBuff.substring(beginIndex, sBuff.getOffset());
            if(cur_ch != '\"') {
                return new Token(token_value, token_value, current_line_num, false);
            } else {
                return new Token(token_value, token_value, current_line_num, true);
            }
        }

        // Try to detect operators.
        switch (cur_ch) {
            case '+':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.add_assign, true);
                }
//                System.out.println("Operators: +");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '-':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.sub_assign, true);

                }
//                System.out.println("Operators: -");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '*':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.mult_assign, true);
                }
//                System.out.println("Operators: *");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '/':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.div_assign, true);
                }
//                System.out.println("Operators: /");
                // TODO: skip comment here?
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '%':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.mod_assign, true);
                }
//                System.out.println("Operators: %");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '&':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.and_assign, true);
                }
                if(sBuff.peek('&')) {
                    return constructToken(Token.Type.rel_and, true);
                }
//                System.out.println("Operators: &");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '|':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.or_assign, true);
                }
                if(sBuff.peek('|')) {
                    return constructToken(Token.Type.rel_or, true);
                }
//                System.out.println("Operators: |");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '^':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.xor_assign, true);
                }
//                System.out.println("Operators: ^");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '~':
//                System.out.println("Operators: ~");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '!':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.not_equal, true);
                }
//                System.out.println("Operators: !");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '=':
                if(sBuff.peek('=')) {
                    return constructToken(Token.Type.equal, true);
                }
//                System.out.println("Operators: =");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
            case '>':
                if(sBuff.peek('=')) {
                    token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                    return new Token(token_value, Token.Type.large_equal, current_line_num, true);
                }
                if(sBuff.peek('>')) {
                    token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                    return new Token(token_value, Token.Type.rshift, current_line_num, true);
                }
//                System.out.println("Operators: >");
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);

            case '<':
                if(sBuff.peek('=')) {
                    token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                    return new Token(token_value, Token.Type.less_equal, current_line_num, true);
                }
                if(sBuff.peek('<')) {
                    token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                    return new Token(token_value, Token.Type.lshift, current_line_num, true);
                }
                token_value = sBuff.substring(beginIndex, sBuff.getOffset());
                return new Token(token_value, token_value, current_line_num, true);
        }

        // Try to detect separators
        switch (cur_ch) {
            case '(': case ')': case '[': case ']': case '{': case '}': case ',': case ';':
                return constructToken(true);
        }
        return new Token(Token.Type.unknown, Token.Type.unknown, current_line_num, false);
    }

    private Token constructToken(boolean valid) {
        String token_value = sBuff.substring(beginIndex, sBuff.getOffset());
        return constructToken(token_value, valid);
    }

    private Token constructToken(String type, boolean valid) {
        String token_value = sBuff.substring(beginIndex, sBuff.getOffset());
        return new Token(token_value, type, current_line_num, valid);
    }

    // Private Properties
    private SourceBuffer sBuff;

    private int current_line_num;

    private int beginIndex; // beginning index of the token.
}
