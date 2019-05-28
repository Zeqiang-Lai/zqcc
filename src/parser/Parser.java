package parser;

import ast.DeclNode;
import ast.ExprNode;
import ast.StmtNode;
import error.ErrorCollector;
import error.ParserError;
import lexer.Token;
import lexer.TokenType;

import java.util.List;
import java.util.Vector;

import static lexer.TokenType.*;
import static error.ParserError.ErrorType.*;

public class Parser {
    // region Properties

    private List<Token> tokens;
    private int last_index;
    private ErrorCollector errorCollector = ErrorCollector.getInstance();

    // endregion

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public StmtNode.CompilationUnit parse() {
        try {
            return parseCompilationUnit(0);
        } catch (ParserError parserError) {
            errorCollector.add(parserError);
            return null;
        }
    }

    /**
     * Error recovery.
     */
    private int recovery(int index) {
        int i;
        int current_line = tokens.get(index).line;
        for(i=index+1; i<tokens.size(); ++i) {
            if(tokens.get(i).line != current_line)
                return i;
            if(check(i, SEMICOLON, RIGHT_PAREN))
                return i+1;
        }
        return i;
    }

    private StmtNode.CompilationUnit parseCompilationUnit(int index) throws ParserError {
        Vector<StmtNode.Declaration> items = new Vector<>();
        StmtNode.Declaration item;

        ParserError best_error = null;

        while(!isAtEnd(index)) {
            try {
                item = parseDeclaration(index);
                index = last_index;
                items.add(item);
                continue;
            } catch (ParserError parserError) {
//                parserError.printStackTrace();
                if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                    best_error = parserError;
            }

            try {
                item = parseFunctionDefinition(index);
                index = last_index;
                items.add(item);
                continue;
            } catch (ParserError parserError) {
//                parserError.printStackTrace();
                if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                    best_error = parserError;
            }
            if(isAtEnd(index)) {
                break;
            }
            index = recovery(index);
            errorCollector.add(best_error);
        }

        if(isAtEnd(index))
            return new StmtNode.CompilationUnit(items);
        else
            throw best_error;
    }

    private StmtNode.Declaration parseFunctionDefinition(int index) throws ParserError {
        Vector<Token> specs = parseDeclSpecifiers(index);
        index = last_index;
        DeclNode decl = parseDeclarator(index);
        index = last_index;
        StmtNode.Compound body = parseCompoundStatement(index);
        DeclNode.Root root = new DeclNode.Root(specs, decl);
        return new StmtNode.Declaration(root, body);
    }


    // region Declaration

    /**
     * declaration = specifiers + declarator + initializer.
     * e.g. int a = 3, b;
     */
    private StmtNode.Declaration parseDeclaration(int index) throws ParserError {
        Vector<Token> specs = parseDeclSpecifiers(index);
        index = last_index;

        if(check(index, SEMICOLON)) {
            last_index = index + 1;
            DeclNode.Root root = new DeclNode.Root(specs);
            return new StmtNode.Declaration(root, null);
        }

        Vector<DeclNode> decls = new Vector<>();
        Vector<ExprNode> inits = new Vector<>();

        while(true){
            DeclNode node = parseDeclarator(index);
            index = last_index;
            decls.add(node);

            if(check(index, ASSIGN)) {
                index += 1;
                ExprNode expr = parseAssignment(index);
                index = last_index;
                inits.add(expr);
            } else {
                inits.add(null);
            }

            if(check(index, COMMA))
                index += 1;
            else
                break;
        }
        index = match(index, SEMICOLON);
        last_index = index;

        DeclNode.Root root = new DeclNode.Root(specs, decls, inits);
        return new StmtNode.Declaration(root, null);
    }

    /**
     * Parse declaration specifiers. At this time, it only recognize
     * simple sepcifiers, such as `int`, `double`, `char`, `void`.
     *
     * TODO: combination, such as `unsigned int`, `static int`.
     */
    private Vector<Token> parseDeclSpecifiers(int index) throws ParserError {
        Vector<Token> specs = new Vector<>();

        if (check(index, INT) || check(index, DOUBLE) ||
                check(index, CHAR) || check(index, VOID)) {
            specs.add(tokens.get(index));
        }
        if(specs.isEmpty())
            throw new ParserError(index, "expect specifiers", tokens, BEFORE);
        //TODO: raise error if it is empty.
        last_index = index + 1;
        return specs;
    }


    /**
     * This function recognize three types of declarator.
     * 1. identifier. e.g. int apple = 10; apple is the declarator.
     * 2. array: int apple[10][2]; apple[10][2] is the declarator.
     * 3. function: int f(int a, int b) {...} f(int a, int b) is the declarator.
     */
    private DeclNode parseDeclarator(int index) throws ParserError {
        // find the range of the declarator. We need the last token to
        // identifier the type of the declarator.
        int end = findDeclaratorEnd(index);
        DeclNode decl = parseDeclarator(index, end);
        // FIXME: last_index could not be end, when error happens.
        last_index = end;
        return decl;
    }

    /**
     * Parse declarator between start and end(exclusive).
     */
    private DeclNode parseDeclarator(int start, int end) throws ParserError {

        if(start + 1 == end && check(start, IDENTIFIER)) {
            // TODO: add to symbol table.
            return new DeclNode.Identifier(tokens.get(start));
        }

        // array = declarator[expression]
        else if(check(end-1, RIGHT_BRACKET)) {
            int right = findPairBackward(end-1, RIGHT_BRACKET);

            DeclNode decl = parseDeclarator(start, right);

            ExprNode num;
            if(right == end-2) num = null;
            else {
                num = parseExperssion(right+1);
                if(num == null) {
                    throw new ParserError(last_index, "unexpected token in array size", tokens, AFTER);
                }
            }

            return new DeclNode.Array(num, decl);

        }

        // function = declarator(parameter list)
        else if(check(end-1, RIGHT_PAREN)){
            int left = findPairBackward(end-1, RIGHT_PAREN);

            DeclNode decl = parseDeclarator(start, left);
            Vector<DeclNode> params = parseParameterList(left+1, end);

            return new DeclNode.Function(decl, params);
        }

        else {
            throw new ParserError(start, "invalid declarator", tokens, AT);
        }
    }

    private Vector<DeclNode> parseParameterList(int index, int end) throws ParserError {
        Vector<DeclNode> params = new Vector<>();

        if(check(index, RIGHT_PAREN)) {
            last_index = index;
            return params;
        }

        while(true) {
            Vector<Token> specs = parseDeclSpecifiers(index);
            index = last_index;

            DeclNode decl = parseDeclarator(index);
            index = last_index;

            params.add(new DeclNode.Root(specs, decl));

            if(check(index, COMMA))
                index += 1;
            else if(index != end-1) {
                last_index = index;
                throw new ParserError(index, "expect ','", tokens, AFTER);
            }else
                break;
        }

        last_index = index;
        return params;
    }


    /**
     * Find the end of the declarator that starts at given index.
     * Note that this function also check the match status of parenthesises and brackets.
     * @param index : start index
     * @return : end index. The range of the declarator is [start, end).
     */
    private int findDeclaratorEnd(int index) throws ParserError {
        if(check(index, IDENTIFIER))
            return findDeclaratorEnd(index+1);
        else if(check(index, LEFT_PAREN)) {
            int close = findPairForward(index, LEFT_PAREN);
            return findDeclaratorEnd(close+1);
        } else if(check(index, LEFT_BRACKET)) {
            int close = findPairForward(index, LEFT_BRACKET);
            return findDeclaratorEnd(close+1);
        } else
            // After matching parenthesis, bracket and identifier,
            // we should meet the end of the declarator.
            return index;
    }

    // endregion

    // region Statement

    private StmtNode.Compound parseCompoundStatement(int index) throws ParserError {
        index = match(index, LEFT_BRACE);

        ParserError best_error = null;
        Vector<StmtNode> items = new Vector<>();
        StmtNode item;

        int right = -1;
        try {
            right = findPairForward(index - 1, LEFT_BRACE);
        } catch (ParserError parserError) {
//            errorCollector.add(new ParserError(index, "unmatched { ", tokens, AT));
        }

        while (!isAtEnd(index)) {
            try {
                item = parseDeclaration(index);
                index = last_index;
                items.add(item);
                continue;
            } catch (ParserError parserError) {
                if (best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                    best_error = parserError;
            }

            try {
                item = parseStatement(index);
                index = last_index;
                items.add(item);
                continue;
            } catch (ParserError parserError) {
                if (best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                    best_error = parserError;
            }
            if (isAtEnd(index))
                break;

            if (right != -1 && index >= right) {
                index = right;
                break;
            }
            index = recovery(index);
            errorCollector.add(best_error);
        }

        try {
            index = match(index, RIGHT_BRACE);
            last_index = index;
            return new StmtNode.Compound(items);
        } catch (ParserError parserError) {
            errorCollector.add(parserError);
            return new StmtNode.Compound(items);
        }
    }

    private StmtNode parseStatement(int index) throws ParserError {
        ParserError best_error = null;

        try {
            return parseIfStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseWhileStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseReturnStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseBreakStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseContinueStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseCompoundStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseEmptyStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseExpressionStatement(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        throw best_error;
    }

    private StmtNode parseIfStatement(int index) throws ParserError {
        index = match(index, IF);
        index = match(index, LEFT_PAREN);

        ExprNode cond = parseExperssion(index);
        index = last_index;

        index = match(index, RIGHT_PAREN);

        StmtNode if_body = parseStatement(index);
        index = last_index;

        StmtNode else_body = null;
        if(check(index, ELSE)) {
            index += 1;
            else_body = parseStatement(index);
            index = last_index;
        }

        last_index = index;
        return new StmtNode.If(cond, if_body, else_body);
    }

    private StmtNode parseWhileStatement(int index) throws ParserError {
        index = match(index, WHILE);
        index = match(index, LEFT_PAREN);

        ExprNode cond = parseExperssion(index);
        index = last_index;

        index = match(index, RIGHT_PAREN);

        StmtNode body = parseStatement(index);

        return new StmtNode.While(cond, body);
    }

    private StmtNode parseReturnStatement(int index) throws ParserError {
        index = match(index, RETURN);
        try {
            ExprNode value = parseExperssion(index);
            index = last_index;
            try {
                last_index = match(index, SEMICOLON);
            } catch (ParserError parserError) {
                errorCollector.add(parserError);
            }
            return new StmtNode.Return(value);
        } catch (Exception e) {
            try {
                last_index = match(index, SEMICOLON);
            } catch (ParserError parserError) {
                errorCollector.add(parserError);
            }
            return new StmtNode.Return(null);
        }
    }

    private StmtNode parseBreakStatement(int index) throws ParserError {
        index = match(index, BREAK);
        index = match(index, SEMICOLON);
        last_index = index;
        return new StmtNode.Break();
    }

    private StmtNode parseContinueStatement(int index) throws ParserError {
        index = match(index, CONTINUE);
        index = match(index, SEMICOLON);
        last_index = index;
        return new StmtNode.Continue();
    }

    private StmtNode parseExpressionStatement(int index) throws ParserError {
        ExprNode expr = parseExperssion(index);

        index = last_index;
        last_index = match(index, SEMICOLON);

        return new StmtNode.Expression(expr);
    }

    private StmtNode parseEmptyStatement(int index) throws ParserError {
        if(check(index, SEMICOLON)) {
            last_index = index + 1;
            return new StmtNode.Empty();
        }
        throw new ParserError(index, "expect ';'", tokens, AFTER);
    }

    // endregion

    // region Expression

    // TODO: extract the common part.

    private ExprNode parseExperssion(int index) throws ParserError {
        ExprNode node = parseAssignment(index);
        return node;
    }

    private ExprNode parseAssignment(int index) throws ParserError {
        int save = index;

        ParserError best_error = null;

        ExprNode node;
        try {
            node = parseUnary(index);
            index = last_index;
            index = match(index, ASSIGN, ADD_ASSIGN, SUB_ASSIGN, MULTI_ASSIGN, DIV_ASSIGN);
            int op = index-1;
            ExprNode another = parseAssignment(index);
            switch (tokens.get(op).type) {
                case ASSIGN:
                    node = new ExprNode.Assign(node, another);
                    break;
                case ADD_ASSIGN:
                    node = new ExprNode.AddAssign(node, another);
                    break;
                case SUB_ASSIGN:
                    node = new ExprNode.SubAssign(node, another);
                    break;
                case MULTI_ASSIGN:
                    node = new ExprNode.MultiAssign(node, another);
                    break;
                case DIV_ASSIGN:
                    node = new ExprNode.DivAssign(node, another);
                    break;
            }
            return node;
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        try {
            return parseLogicalOr(save);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        throw best_error;
    }

    private ExprNode parseLogicalOr(int index) throws ParserError {
        ExprNode node = parseLogicalAnd(index);
        index = last_index;

        while(check(index, REL_OR)) {
            index += 1;
            ExprNode another = parseLogicalAnd(index);
            index = last_index;
            node = new ExprNode.LogicalOr(node, another);
        }

        return node;
    }

    private ExprNode parseLogicalAnd(int index) throws ParserError {
        ExprNode node = parseBitOR(index);
        index = last_index;

        while(check(index, REL_AND)) {
            index += 1;
            ExprNode another = parseBitOR(index);
            index = last_index;
            node = new ExprNode.LogicalAnd(node, another);
        }

        return node;
    }

    private ExprNode parseBitOR(int index) throws ParserError {
        ExprNode node = parseBitXOR(index);
        index = last_index;

        while(check(index, OR)) {
            index += 1;
            ExprNode another = parseBitXOR(index);
            index = last_index;
            node = new ExprNode.BitOR(node, another);
        }

        return node;
    }

    private ExprNode parseBitXOR(int index) throws ParserError {
        ExprNode node = parseBitAnd(index);
        index = last_index;

        while(check(index, XOR)) {
            index += 1;
            ExprNode another = parseBitAnd(index);
            index = last_index;
            node = new ExprNode.BitXOR(node, another);
        }

        return node;
    }

    private ExprNode parseBitAnd(int index) throws ParserError {
        ExprNode node = parseEquality(index);
        index = last_index;

        while(check(index, AND)) {
            index += 1;
            ExprNode another = parseBitAnd(index);
            index = last_index;
            node = new ExprNode.BitAnd(node, another);
        }

        return node;
    }

    private ExprNode parseEquality(int index) throws ParserError {
        ExprNode node = parseRelational(index);
        index = last_index;

        while(check(index, EQUAL, NOT_EQUAL)) {
            int op = index;
            index += 1;
            ExprNode another = parseRelational(index);
            index = last_index;
            if(tokens.get(op).type == EQUAL)
                node = new ExprNode.Equality(node, another);
            else
                node = new ExprNode.Inequality(node, another);
        }

        return node;
    }

    private ExprNode parseRelational(int index) throws ParserError {
        ExprNode node = parseShift(index);
        index = last_index;

        while(check(index, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            int op = index;
            index += 1;
            ExprNode another = parseShift(index);
            index = last_index;
            switch (tokens.get(op).type) {
                case GREATER:
                    node = new ExprNode.Greater(node, another);
                    break;
                case GREATER_EQUAL:
                    node = new ExprNode.GreaterEqual(node, another);
                    break;
                case LESS:
                    node = new ExprNode.Less(node, another);
                    break;
                case LESS_EQUAL:
                    node = new ExprNode.LessEqual(node, another);
                    break;
            }
        }
        return node;
    }

    private ExprNode parseShift(int index) throws ParserError {
        ExprNode node = parseAdditive(index);
        index = last_index;

        while(check(index, LEFT_SHIFT, RIGHT_SHIFT)) {
            int op = index;
            index += 1;
            ExprNode another = parseAdditive(index);
            index = last_index;
            if(tokens.get(op).type == LEFT_SHIFT)
                node = new ExprNode.LShift(node, another);
            else
                node = new ExprNode.Rshift(node, another);
        }
        return node;
    }

    private ExprNode parseAdditive(int index) throws ParserError {
        ExprNode node = parseMultiplicative(index);
        index = last_index;

        while(check(index, ADD, SUB)) {
            int op = index;
            index += 1;
            ExprNode another = parseMultiplicative(index);
            index = last_index;
            if(tokens.get(op).type == ADD)
                node = new ExprNode.Add(node, another);
            else
                node = new ExprNode.Minus(node, another);
        }
        return node;
    }

    private ExprNode parseMultiplicative(int index) throws ParserError {
        ExprNode node = parseCast(index);
        index = last_index;

        while(check(index, MULTI, DIV, MOD)) {
            int op = index;
            index += 1;
            ExprNode another = parseUnary(index);
            index = last_index;
            switch (tokens.get(op).type) {
                case MULTI:
                    node = new ExprNode.Multi(node, another);
                    break;
                case DIV:
                    node = new ExprNode.Div(node, another);
                    break;
                case MOD:
                    node = new ExprNode.Mod(node, another);
                    break;
            }
        }
        return node;
    }

    /**
     * <cast-expression> = <unary-expression> |
     *                     ‘(‘ <type-name> ’)’ <cast-expression>
     */

    private ExprNode parseCast(int index) throws ParserError {
        int save = index;
        ParserError best_error = null;

        try {
            index = match(index, LEFT_PAREN);
            Vector<Token> specs = parseDeclSpecifiers(index);
            index = last_index;
            index = match(index, RIGHT_PAREN);
            ExprNode expr = parseCast(index);
            return new ExprNode.Cast(specs, expr);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }
        try {
            return parseUnary(save);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }

        throw best_error;
    }

    /**
     * <unary-expression> = <postfix-expression> |
     *                      <unary-operator> <cast-expression>
     */
    private ExprNode parseUnary(int index) throws ParserError {
        int save = index;
        if(check(index, REL_NOT, ADD, SUB)) {
            index += 1;
            ExprNode expr;
            switch (tokens.get(index-1).type) {
                case REL_NOT:
                    expr = parseCast(index);
                    return new ExprNode.LogicalNot(expr);
                case ADD:
                    expr = parseCast(index);
                    return new ExprNode.UnaryPlus(expr);
                case SUB:
                    expr = parseCast(index);
                    return new ExprNode.UnaryMinus(expr);
            }
        }
        return parsePostfix(save);
    }

    private ExprNode parsePostfix(int index) throws ParserError {
        ExprNode node;
        node = parsePrimary(index);
        index = last_index;

        while(true) {
            if(check(index, LEFT_BRACKET)) {
                index += 1;
                ExprNode expr = parseExperssion(index);
                index = last_index;
                index = match(index, RIGHT_BRACKET);
                node = new ExprNode.ArraySub(node, expr);
                continue;
            }

            if(check(index, LEFT_PAREN)) {
                index += 1;
                Vector<ExprNode> args = new Vector<>();
                if(check(index, RIGHT_PAREN)) {
                    index += 1;
                    node = new ExprNode.FunCall(node, args);
                }
                else {
                    while (true) {
                        ExprNode arg = parseAssignment(index);
                        args.add(arg);
                        index = last_index;

                        if (check(index, COMMA))
                            index += 1;
                        else
                            break;
                    }
                    index = match(index, RIGHT_PAREN);
                    node = new ExprNode.FunCall(node, args);
                }
            }

            break;
        }
        last_index = index;
        return node;
    }

    private ExprNode parsePrimary(int index) throws ParserError {
        ParserError best_error = null;

        try {
            return parseIdentifier(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }
        try {
            return parseNumber(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }
        try {
            return parseString(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }
        try {
            return parseParenExpression(index);
        } catch (ParserError parserError) {
            if(best_error == null || best_error.parsed_amount < parserError.parsed_amount)
                best_error = parserError;
        }
//        throw best_error;
        // TODO: throw which error is better?
        throw new ParserError(index, "expect expression", tokens, AFTER);
    }

    private ExprNode parseIdentifier(int index) throws ParserError {
        index = match(index, IDENTIFIER);
        last_index = index;
        return new ExprNode.Identifier(tokens.get(index-1).value);
    }

    private ExprNode parseNumber(int index) throws ParserError {
        if(check(index, INTEGER_CONSTANT, DOUBLE_CONSTANT, CHARACTER_CONSTANT)) {
            last_index = index + 1;
            double constant;
            if(tokens.get(index).type == CHARACTER_CONSTANT)
                constant = (int)(tokens.get(index).value.charAt(0));
            else
                constant = Double.valueOf(tokens.get(index).value);
            return new ExprNode.Number(constant);
        } else {
            throw new ParserError(index, "expect number", tokens, AFTER);
        }
    }

    private ExprNode parseString(int index) throws ParserError {
        index = match(index, STRING);
        last_index = index;
        return new ExprNode.StringExpr(tokens.get(index-1).value);
    }

    private ExprNode parseParenExpression(int index) throws ParserError {
        index = match(index, LEFT_PAREN);
        ExprNode expr = parseExperssion(index);
        index = last_index;
        index = match(index, RIGHT_PAREN);
        last_index = index;
        return expr;
    }



    // endregion

    // region FindPair
    /**
     * Find the index of the paired token for the token started at `index`
     * This function throw a `ParseError` when there is mismatch. However, it does not
     * guarantee the correctness when no error is thrown.
     */
    private int findPairForward(int index, TokenType match) throws ParserError {
        if(match == LEFT_PAREN) {
            String msg = "mismatched parenthesis in declaration";
            return findPairForward(index, LEFT_PAREN, RIGHT_PAREN, msg);
        }else if(match == LEFT_BRACKET) {
            String msg = "mismatched square brackets in declaration";
            return findPairForward(index, LEFT_BRACKET, RIGHT_BRACKET, msg);
        } else {
            String msg = "mismatched square brace in declaration";
            return findPairForward(index, LEFT_BRACE, RIGHT_BRACE, msg);
        }
    }

    /**
     * The same as `findPairForward`, but in a reverse order.
     */
    private int findPairBackward(int index, TokenType match) throws ParserError {
        if(match == RIGHT_PAREN) {
            String msg = "mismatched parenthesis in declaration";
            return findPairBackward(index, LEFT_PAREN, RIGHT_PAREN, msg);
        } else if(match == RIGHT_BRACKET){
            String msg = "mismatched square brackets in declaration";
            return findPairBackward(index, LEFT_BRACKET, RIGHT_BRACKET, msg);
        }
        String msg = "mismatched square brace in declaration";
        return findPairForward(index, LEFT_BRACE, RIGHT_BRACE, msg);
    }

    private int findPairForward(int index, TokenType left, TokenType right, String msg) throws ParserError {
        int depth = 0;
        int i;
        for(i=index; i<tokens.size(); i++) {
            if(check(i, left))
                depth += 1;
            else if(check(i, right))
                depth -= 1;
            if(depth == 0)
                break;
        }
        if(depth != 0)
            throw new ParserError(index, msg, tokens, AT);
        // TODO: raise mismatch error.
        return i;
    }

    private int findPairBackward(int index, TokenType left, TokenType right, String msg) throws ParserError {
        int depth = 0;
        int i;
        for(i=index; i>=0; i--) {
            if(check(i, right))
                depth += 1;
            else if(check(i, left))
                depth -= 1;
            if(depth == 0)
                break;
        }
        if(depth != 0)
            throw new ParserError(index, msg,tokens, AT);
        return i;
    }
    // endregion

    //region Function Relates Tokens

    private int match(int index, TokenType... types) throws ParserError {
        if (isAtEnd(index)) {

            throw new ParserError(index, "expect " + strRepr(types),tokens, AFTER);
        }
        if (check(index, types))
            return index + 1;
        else {
            throw new ParserError(index, "expect " + strRepr(types),tokens, AFTER);
        }
    }

    private boolean check(int index, TokenType... types) {
        if (isAtEnd(index)) return false;
        for(TokenType type : types) {
            if (tokens.get(index).type == type)
                return true;
        }
        return false;
    }

    private boolean isAtEnd(int index) {
        return index >= tokens.size();
    }

    // TODO: improve it
    private String strRepr(TokenType... types) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i<types.length; ++i) {
            if(i != 0)
                builder.append(" || ");
            if(value.containsKey(types[i]))
                builder.append("'" + value.get(types[i]) + "'");
            else
                builder.append(types[i].toString().toLowerCase());
        }

        return builder.toString();
    }
    //endregion
}
