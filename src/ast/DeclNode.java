package ast;

import lexer.Token;

import java.util.Vector;

public abstract class DeclNode extends Node{
    public interface Visitor<R> {
        R visitRoot(Root node);
        R visitArray(Array node);
        R visitFunction(Function node);
        R visitIdentifier(Identifier node);
    }

    abstract <R> R accept(Visitor<R> visitor);

    public static class Root extends DeclNode {
        Vector<Token> specs;
        Vector<DeclNode> decls;
        Vector<ExprNode> inits;

        public Root(Vector<Token> specs, Vector<DeclNode> decls, Vector<ExprNode> inits) {
            this.specs = specs;
            this.decls = decls;
            this.inits = inits;
        }

        public Root(Vector<Token> specs, Vector<DeclNode> decls) {
            this.specs = specs;
            this.decls = decls;
            this.inits = null;
        }

        public Root(Vector<Token> specs, DeclNode decl) {
            this.specs = specs;
            this.decls = new Vector<>();
            this.decls.add(decl);
            this.inits = null;
        }

        public Root(Vector<Token> specs) {
            this.specs = specs;
            this.decls = new Vector<>();
            this.inits = null;
        }

        public <R> R accept(DeclNode.Visitor<R> visitor) {
            return visitor.visitRoot(this);
        }
    }

    public static class Array extends DeclNode {
        DeclNode decl;
        ExprNode size;

        public Array(ExprNode size, DeclNode decl) {
            this.size = size;
            this.decl = decl;
        }

        public <R> R accept(DeclNode.Visitor<R> visitor) {
            return visitor.visitArray(this);
        }
    }

    public static class Function extends DeclNode {
        DeclNode decl;
        Vector<DeclNode> args;

        public Function(DeclNode decl, Vector<DeclNode> args) {
            this.decl = decl;
            this.args = args;
        }

        public <R> R accept(DeclNode.Visitor<R> visitor) {
            return visitor.visitFunction(this);
        }
    }

    public static class Identifier extends DeclNode {
        Token identifier;

        public Identifier(Token identifier) {
            this.identifier = identifier;
        }

        public <R> R accept(DeclNode.Visitor<R> visitor) {
            return visitor.visitIdentifier(this);
        }
    }
}
