package ast;

import lexer.Token;

public class XMLPrinter implements StmtNode.Visitor<String>,
        ExprNode.Visitor<String>, DeclNode.Visitor<String> {

    // region Properties
    private int depth = 0;

    // endregion

    // region Print Interface

    public String print(StmtNode node) {
        return this.visit(node);
    }

    // endregion

    // region Utils

    private String indentString(int num) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<num; ++i)
            builder.append("    ");
        return builder.toString();
    }

    private void append(StringBuilder builder, String text, int indent) {
        builder.append(indentString(indent));
        builder.append(text);
    }

    private String visit(StmtNode node) {
        return node.accept(this);
    }

    private String visit(ExprNode node) {
        return node.accept(this);
    }

    private String visit(DeclNode node) {
        return node.accept(this);
    }

    // endregion

    // region DeclNode

    @Override
    public String visitRoot(DeclNode.Root node) {
        StringBuilder builder = new StringBuilder();
        append(builder, "<decl-root>\n", depth);
        depth += 1;

        append(builder, "<decl-specifiers>\n", depth);
        depth += 1;
        for(Token token : node.specs) {
            append(builder, "<specifiers>", depth);
            builder.append(token.value);
            builder.append("</specifiers>\n");
        }
        depth -= 1;
        append(builder, "</decl-specifiers>\n", depth);

        append(builder, "<decl-declarators>\n", depth);
        depth += 1;
        for(DeclNode decl : node.decls) {
            builder.append(this.visit(decl));
        }
        depth -= 1;
        append(builder, "</decl-declarators>\n", depth);

        append(builder, "<decl-initializer>\n", depth);
        depth += 1;
        if(node.inits != null) {
            for (ExprNode init : node.inits) {
                if (init != null)
                    builder.append(this.visit(init));
            }
        }
        depth -= 1;
        append(builder, "</decl-initializer>\n", depth);

        depth -= 1;
        append(builder, "</decl-root>\n", depth);
        return builder.toString();
    }

    @Override
    public String visitArray(DeclNode.Array node) {
        StringBuilder builder = new StringBuilder();
        append(builder, "<decl-array>\n", depth);
        depth += 1;

        append(builder, "<array>\n", depth);
        depth += 1;
        builder.append(this.visit(node.decl));
        depth -= 1;
        append(builder, "</array>\n", depth);

        append(builder, "<size>\n", depth);
        depth += 1;
        builder.append(this.visit(node.size));
        depth -= 1;
        append(builder, "</size>\n", depth);

        depth -= 1;
        append(builder, "</decl-array>\n", depth);
        return builder.toString();
    }

    @Override
    public String visitFunction(DeclNode.Function node) {
        StringBuilder builder = new StringBuilder();
        append(builder, "<decl-func>\n", depth);
        depth += 1;

        append(builder, "<callee>\n", depth);
        depth += 1;
        builder.append(this.visit(node.decl));
        depth -= 1;
        append(builder, "</callee>\n", depth);

        append(builder, "<parameters>\n", depth);
        depth += 1;
        for(DeclNode arg : node.args) {
            builder.append(this.visit(arg));
        }
        depth -= 1;
        append(builder, "</parameters>\n", depth);

        depth -= 1;
        append(builder, "</decl-func>\n", depth);
        return builder.toString();
    }

    @Override
    public String visitIdentifier(DeclNode.Identifier node) {
        StringBuilder builder = new StringBuilder();
        append(builder, "<decl-identifier>", depth);
        builder.append(node.identifier.value);
        builder.append("</decl-identifier>\n");
        return builder.toString();
    }

    // endregion

    // region ExprNode

    @Override
    public String visitIdentifier(ExprNode.Identifier expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-identifier>");
        builder.append(expr.identifier);
        builder.append("</expr-identifier>\n");
        return builder.toString();
    }

    @Override
    public String visitNumber(ExprNode.Number expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-number>");
        builder.append(expr.number);
        builder.append("</expr-number>\n");
        return builder.toString();
    }

    @Override
    public String visitStringExpr(ExprNode.StringExpr expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-string>");
        builder.append(expr.value);
        builder.append("</expr-string>\n");
        return builder.toString();
    }

    @Override
    public String visitParenExpr(ExprNode.ParenExpr expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-paren>\n");
        builder.append(this.visit(expr.expr));
        builder.append(indentString(depth));
        builder.append("</expr-paren>\n");
        return builder.toString();
    }

    @Override
    public String visitArraySub(ExprNode.ArraySub expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-array-sub>\n");
        depth += 1;

        this.append(builder, "<array>\n", depth);
        builder.append(this.visit(expr.array));
        this.append(builder, "</array>\n", depth);

        this.append(builder, "<subscript>\n", depth);
        builder.append(this.visit(expr.sub));
        this.append(builder, "</subscript>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-array-sub>\n");
        return builder.toString();
    }

    @Override
    public String visitFunCall(ExprNode.FunCall expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-func-call>\n");
        depth += 1;

        append(builder, "<func>\n", depth);
        builder.append(this.visit(expr.func));
        append(builder, "</func>\n", depth);

        append(builder, "<args>\n", depth);
        depth += 1;
        for(ExprNode arg : expr.args)
            builder.append(this.visit(arg));
        depth -= 1;
        append(builder, "</args>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-func-call>\n");
        return builder.toString();
    }

    @Override
    public String visitUnaryPlus(ExprNode.UnaryPlus expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-unary-plus>\n");
        depth += 1;

        append(builder, "<operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.operand));
        depth -= 1;
        append(builder, "</operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-unary-plus>\n");
        return builder.toString();
    }

    @Override
    public String visitUnaryMinus(ExprNode.UnaryMinus expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-unary-minus>\n");
        depth += 1;

        append(builder, "<operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.operand));
        depth -= 1;
        append(builder, "</operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-unary-minus>\n");
        return builder.toString();
    }

    @Override
    public String visitCast(ExprNode.Cast expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-cast>\n");
        depth += 1;

        append(builder, "<types>\n", depth);
        depth += 1;
        for(Token type : expr.types)
            append(builder, "<type>"+type.valid+"</type>\n", depth);
        depth -= 1;
        append(builder, "</types>\n", depth);

        append(builder, "<operand>\n", depth);
        depth+=1;
        builder.append(this.visit(expr.expr));
        depth-=1;
        append(builder, "</operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-cast>\n");
        return builder.toString();
    }

    @Override
    public String visitMulti(ExprNode.Multi expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-multi>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-mutli>\n");
        return builder.toString();
    }

    @Override
    public String visitDiv(ExprNode.Div expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-div>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-div>\n");
        return builder.toString();
    }

    @Override
    public String visitMod(ExprNode.Mod expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expr-mod>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expr-mod>\n");
        return builder.toString();
    }

    @Override
    public String visitAdd(ExprNode.Add expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<add>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</add>\n");
        return builder.toString();
    }

    @Override
    public String visitMinus(ExprNode.Minus expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<minus>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</minus>\n");
        return builder.toString();
    }

    @Override
    public String visitLShift(ExprNode.LShift expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<l-shift>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</l-shift>\n");
        return builder.toString();
    }

    @Override
    public String visitRshift(ExprNode.Rshift expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<r-shift>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</r-shift>\n");
        return builder.toString();
    }

    @Override
    public String visitGreater(ExprNode.Greater expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<greater>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);
        ;
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</greater>\n");
        return builder.toString();
    }

    @Override
    public String visitLess(ExprNode.Less expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<less>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</less>\n");
        return builder.toString();
    }

    @Override
    public String visitGreaterEqual(ExprNode.GreaterEqual expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<greater-equal>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</greater-equal>\n");
        return builder.toString();
    }

    @Override
    public String visitLessEqual(ExprNode.LessEqual expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<less-equal>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</less-equal>\n");
        return builder.toString();
    }

    @Override
    public String visitEquality(ExprNode.Equality expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<equality>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</equality>\n");
        return builder.toString();
    }

    @Override
    public String visitInequality(ExprNode.Inequality expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<inequality>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</inequality>\n");
        return builder.toString();
    }

    @Override
    public String visitBitOR(ExprNode.BitOR expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<bit-or>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</bit-or>\n");
        return builder.toString();
    }

    @Override
    public String visitBitXOR(ExprNode.BitXOR expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<bit-xor>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</bit-xor>\n");
        return builder.toString();
    }

    @Override
    public String visitBitAnd(ExprNode.BitAnd expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<bit-and>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</bit-and>\n");
        return builder.toString();
    }

    @Override
    public String visitLogicalAnd(ExprNode.LogicalAnd expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<logical-and>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</logical-and>\n");
        return builder.toString();
    }

    @Override
    public String visitLogicalOr(ExprNode.LogicalOr expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<logical-or>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</logical-or>\n");
        return builder.toString();
    }

    @Override
    public String visitLogicalNot(ExprNode.LogicalNot expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<logical-not>\n");
        depth += 1;

        append(builder, "<operand>\n", depth);
        depth += 1;
        builder.append(expr.expr);
        depth -= 1;
        append(builder, "</operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</logical-not>\n");
        return builder.toString();
    }

    @Override
    public String visitAssign(ExprNode.Assign expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<div-assign>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</div-assign>\n");
        return builder.toString();
    }

    @Override
    public String visitAddAssign(ExprNode.AddAssign expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<div-assign>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</div-assign>\n");
        return builder.toString();
    }

    @Override
    public String visitSubAssign(ExprNode.SubAssign expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<sub-assign>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</sub-assign>\n");
        return builder.toString();
    }

    @Override
    public String visitMultiAssign(ExprNode.MultiAssign expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<multi-assign>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</multi-assign>\n");
        return builder.toString();
    }

    @Override
    public String visitDivAssign(ExprNode.DivAssign expr) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<div-assign>\n");
        depth += 1;

        append(builder, "<left-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.left));
        depth -= 1;
        append(builder, "</left-operand>\n", depth);

        append(builder, "<right-operand>\n", depth);
        depth += 1;
        builder.append(this.visit(expr.right));
        depth -= 1;
        append(builder, "</right-operand>\n", depth);

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</div-assign>\n");
        return builder.toString();
    }

    // endregion

    // region Stmt.External

    @Override
    public String visitCompilationUnit(StmtNode.CompilationUnit node) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<compilation-unit>\n");
        depth += 1;
        for(StmtNode.Declaration child : node.nodes) {
            builder.append(this.visit(child));
        }
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</compilation-unit>\n");
        return builder.toString();
    }

    @Override
    public String visitDeclaration(StmtNode.Declaration node) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<declaration>\n");
        depth += 1;
        builder.append(this.visit(node.decl));
        if(node.body != null) {
            append(builder, "<body>\n", depth);
            depth += 1;
            builder.append(this.visit(node.body));
            depth -= 1;
            append(builder, "</body>\n", depth);
        }
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</declaration>\n");
        return builder.toString();
    }

    // endregion

    // region StmtNode

    @Override
    public String visitCompound(StmtNode.Compound stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<compound-statement>\n");
        depth += 1;
        for(StmtNode child : stmt.items) {
            builder.append(this.visit(child));
        }
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</compound-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitIf(StmtNode.If stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<if-statement>\n");
        depth += 1;

        append(builder, "<condition>\n", depth);
        depth += 1;
        builder.append(this.visit(stmt.cond));
        depth -= 1;
        append(builder, "</condition>\n", depth);

        append(builder, "<if-body>\n", depth);
        depth += 1;
        builder.append(this.visit(stmt.if_body));
        depth -= 1;
        append(builder, "</if-body>\n", depth);

        if(stmt.else_body != null){
            append(builder, "<else-body>\n", depth);
            depth += 1;
            builder.append(this.visit(stmt.else_body));
            depth -= 1;
            append(builder, "</else-body>\n", depth);
        }

        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</if-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitWhile(StmtNode.While stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<while-statement>\n");
        depth += 1;

        append(builder, "<condition>\n", depth);
        depth += 1;
        builder.append(this.visit(stmt.cond));
        depth -= 1;
        append(builder, "</condition>\n", depth);

        append(builder, "<body>\n", depth);
        depth += 1;
        builder.append(this.visit(stmt.body));
        depth -= 1;
        append(builder, "</body>\n", depth);


        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</while-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitEmpty(StmtNode.Empty stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<empty-statement>\n");
        builder.append(indentString(depth));
        builder.append("</empty-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitReturn(StmtNode.Return stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<return-statement>\n");
        depth += 1;
        if(stmt.value != null) {
            append(builder, "<value>\n", depth);
            depth += 1;
            builder.append(this.visit(stmt.value));
            depth -= 1;
            append(builder, "</value>\n", depth);
        }
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</return-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitBreak(StmtNode.Break stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<break-statement>\n");
        builder.append(indentString(depth));
        builder.append("</break-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitContinue(StmtNode.Continue stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<continue-statement>\n");
        builder.append(indentString(depth));
        builder.append("</continue-statement>\n");
        return builder.toString();
    }

    @Override
    public String visitExpression(StmtNode.Expression stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentString(depth));
        builder.append("<expression-statement>\n");
        depth += 1;
        builder.append(this.visit(stmt.expr));
        depth -= 1;
        builder.append(indentString(depth));
        builder.append("</expression-statement>\n");
        return builder.toString();
    }

    // endregion
}
