package error;

import lexer.Token;

import java.util.List;
import static error.TextColor.*;

public class ParserError extends CompilerError {
    public enum ErrorType {
        AT, AFTER, BEFORE
    }

    public int parsed_amount;    // position of error, index of error token.

    public ParserError(int parsed, String msg, List<Token> tokens, ErrorType type) {
        super(null);

        if(type == ErrorType.BEFORE)
            this.parsed_amount = parsed;
        else
            this.parsed_amount = parsed-1;

        Token token = tokens.get(parsed_amount);

        StringBuilder builder = new StringBuilder();
        builder.append(token.line);
        if(!is_windows) {
            builder.append(bold_color);
            builder.append(red_color);
        }
        builder.append(" error: ");
        if(!is_windows) {
            builder.append(reset_color);
            builder.append(bold_color);
        }
        builder.append(msg);
        switch (type) {
            case AT:
                builder.append(" at ");
                break;
            case AFTER:
                builder.append(" after ");
                break;
            case BEFORE:
                builder.append(" before ");
                break;
        }
        builder.append(token.value);

        builder.append("\n    ");

        // TODO: get source line from lexer.
        StringBuilder line = new StringBuilder();
        int left = parsed_amount;
        while(left >= 0 && tokens.get(left).line == token.line) left -= 1;
        left += 1;
        int right = parsed_amount;
        int col = 0;
        for(int i=left; i<=right; i++) {
            line.append(tokens.get(i).value).append(" ");
            if(i <= parsed_amount)
                col += tokens.get(i).value.length()+1;
        }
        builder.append(line.toString()).append("\n");
        for(int i=0; i<col+2; ++i)
            builder.append(" ");
        if(type == ErrorType.AFTER)
            builder.append(" ");

        if(is_windows)
            builder.append("^");
        else
            builder.append(green_color + "^" + reset_color);

        this.description = builder.toString();
    }

}
