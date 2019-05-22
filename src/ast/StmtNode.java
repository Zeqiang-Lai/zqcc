package ast;

import java.util.Vector;

public abstract class StmtNode extends Node{
    public interface Visitor<R> {
        R visitCompilationUnit(CompilationUnit node);
        R visitDeclaration(Declaration node);

        R visitCompound(Compound stmt);
        R visitIf(If stmt);
        R visitWhile(While stmt);
        R visitEmpty(Empty stmt);
        R visitReturn(Return stmt);
        R visitBreak(Break stmt);
        R visitContinue(Continue stmt);
        R visitExpression(Expression stmt);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static public class CompilationUnit extends StmtNode {
        Vector<Declaration> nodes;

        public CompilationUnit(Vector<Declaration> nodes) {
            this.nodes = nodes;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitCompilationUnit(this);
        }
    }

    static public class Declaration extends StmtNode {
        DeclNode.Root decl;
        StmtNode.Compound body;

        public Declaration(DeclNode.Root decl, StmtNode.Compound body) {
            this.decl = decl;
            this.body = body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitDeclaration(this);
        }
    }

    public static class Compound extends StmtNode {
        // item could be declaration or statement.
        Vector<StmtNode> items;

        public Compound(Vector<StmtNode> items) {
            this.items = items;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitCompound(this);
        }
    }

    public static class If extends StmtNode {
        ExprNode cond;
        StmtNode if_body;
        StmtNode else_body;

        public If(ExprNode cond, StmtNode if_body, StmtNode else_body) {
            this.cond = cond;
            this.if_body = if_body;
            this.else_body = else_body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitIf(this);
        }
    }

    public static class While extends StmtNode {
        ExprNode cond;
        StmtNode body;

        public While(ExprNode cond, StmtNode body) {
            this.cond = cond;
            this.body = body;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitWhile(this);
        }
    }

    public static class Empty extends StmtNode {
        public Empty() {}

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitEmpty(this);
        }
    }

    public static class Return extends StmtNode {
        ExprNode value;

        public Return(ExprNode value) {
            this.value = value;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitReturn(this);
        }
    }

    public static class Break extends StmtNode {
        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitBreak(this);
        }
    }

    public static class Continue extends StmtNode {
        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitContinue(this);
        }
    }

    public static class Expression extends StmtNode {
        ExprNode expr;

        public Expression(ExprNode expr) {
            this.expr = expr;
        }

        public <R> R accept(StmtNode.Visitor<R> visitor) {
            return visitor.visitExpression(this);
        }
    }
}
