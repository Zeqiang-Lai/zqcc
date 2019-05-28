package error;

import lexer.Token;

import java.util.List;

public class ParserError extends CompilerError {
    public int parsed_amount;    // position of error, index of error token.

    public ParserError(int parsed, String msg, List<Token> tokens) {
        super(null);
        this.parsed_amount = parsed;

        String error_color = (char)27 + "[31m";
        String reset_color = (char)27  + "[0m";
        String bold_color = "\033[1m";
        Token token = tokens.get(parsed);
        StringBuilder builder = new StringBuilder();
        builder.append(token.line);
        builder.append(bold_color);
        builder.append(error_color);
        builder.append(" error:");
        builder.append(reset_color);
        builder.append(bold_color);
        builder.append(msg);
        builder.append(" after ");
        builder.append(token.value);

        this.description = builder.toString();
    }

}
