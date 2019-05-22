package ast;

import lexer.Token;

import java.util.Vector;

public abstract class ExprNode extends Node{

    public interface Visitor<R> {
        R visitIdentifier(Identifier expr);
        R visitNumber(Number expr);
        R visitStringExpr(StringExpr expr);
        R visitParenExpr(ParenExpr expr);


        R visitArraySub(ArraySub expr);
        R visitFunCall(FunCall expr);

        R visitUnaryPlus(UnaryPlus expr);
        R visitUnaryMinus(UnaryMinus expr);

        R visitCast(Cast expr);

        R visitMulti(Multi expr);
        R visitDiv(Div expr);
        R visitMod(Mod expr);

        R visitAdd(Add expr);
        R visitMinus(Minus expr);

        R visitLShift(LShift expr);
        R visitRshift(Rshift expr);

        R visitGreater(Greater expr);
        R visitLess(Less expr);
        R visitGreaterEqual(GreaterEqual expr);
        R visitLessEqual(LessEqual expr);

        R visitEquality(Equality expr);
        R visitInequality(Inequality expr);

        R visitBitOR(BitOR expr);
        R visitBitXOR(BitXOR expr);
        R visitBitAnd(BitAnd expr);

        R visitLogicalAnd(LogicalAnd expr);
        R visitLogicalOr(LogicalOr expr);
        R visitLogicalNot(LogicalNot expr);

        R visitAssign(Assign expr);
        R visitAddAssign(AddAssign expr);
        R visitSubAssign(SubAssign expr);
        R visitMultiAssign(MultiAssign expr);
        R visitDivAssign(DivAssign expr);
    }

    abstract <R> R accept(Visitor<R> visitor);

    // region Primary

    public static class Identifier extends ExprNode {
        String identifier;

        public Identifier(String identifier) {
            this.identifier = identifier;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitIdentifier(this);
        }
    }

    public static class Number extends ExprNode {
        double number;

        public Number(double number) {
            this.number = number;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitNumber(this);
        }
    }

    public static class StringExpr extends ExprNode {
        String value;

        public StringExpr(String value) {
            this.value = value;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitStringExpr(this);
        }
    }

    public static class ParenExpr extends ExprNode {
        ExprNode expr;

        public ParenExpr(ExprNode expr) {
            this.expr = expr;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitParenExpr(this);
        }
    }

    // endregion

    // region Postfix

    public static class ArraySub extends ExprNode {
        ExprNode array;
        ExprNode sub;

        public ArraySub(ExprNode array, ExprNode sub) {
            this.array = array;
            this.sub = sub;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitArraySub(this);
        }
    }

    public static class FunCall extends ExprNode {
        ExprNode func;
        Vector<ExprNode> args;

        public FunCall(ExprNode func, Vector<ExprNode> args) {
            this.func = func;
            this.args = args;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitFunCall(this);
        }
    }

    // endregion

    // region Unary

    public static class UnaryPlus extends ExprNode {
        ExprNode operand;

        public UnaryPlus(ExprNode operand) {
            this.operand = operand;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitUnaryPlus(this);
        }
    }

    public static class UnaryMinus extends ExprNode {
        ExprNode operand;

        public UnaryMinus(ExprNode operand) {
            this.operand = operand;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitUnaryMinus(this);
        }
    }

    // endregion

    // region Cast

    public static class Cast extends ExprNode {
        Vector<Token> types;
        ExprNode expr;

        public Cast(Vector<Token> types, ExprNode expr) {
            this.types = types;
            this.expr = expr;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitCast(this);
        }
    }

    // endregion

    // region Multiplicative
    public static class Multi extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Multi(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitMulti(this);
        }
    }

    public static class Div extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Div(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitDiv(this);
        }
    }

    public static class Mod extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Mod(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitMod(this);
        }
    }

    // endregion

    // region Additive

    public static class Add extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Add(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitAdd(this);
        }
    }

    public static class Minus extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Minus(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitMinus(this);
        }
    }

    // endregion

    // region Shift

    public static class LShift extends ExprNode {
        ExprNode left;
        ExprNode right;

        public LShift(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLShift(this);
        }
    }

    public static class Rshift extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Rshift(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitRshift(this);
        }
    }

    // endregion

    // region Relational

    public static class Greater extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Greater(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitGreater(this);
        }
    }

    public static class Less extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Less(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLess(this);
        }
    }

    public static class GreaterEqual extends ExprNode {
        ExprNode left;
        ExprNode right;

        public GreaterEqual(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitGreaterEqual(this);
        }
    }

    public static class LessEqual extends ExprNode {
        ExprNode left;
        ExprNode right;

        public LessEqual(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLessEqual(this);
        }
    }

    // endregion

    // region Equality

    public static class Equality extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Equality(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitEquality(this);
        }
    }

    public static class Inequality extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Inequality(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitInequality(this);
        }
    }

    // endregion

    // region Bitwise

    public static class BitOR extends ExprNode {
        ExprNode left;
        ExprNode right;

        public BitOR(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitBitOR(this);
        }
    }

    public static class BitXOR extends ExprNode {
        ExprNode left;
        ExprNode right;

        public BitXOR(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitBitXOR(this);
        }
    }

    public static class BitAnd extends ExprNode {
        ExprNode left;
        ExprNode right;

        public BitAnd(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitBitAnd(this);
        }
    }

    // endregion

    // region Logical

    public static class LogicalAnd extends ExprNode {
        ExprNode left;
        ExprNode right;

        public LogicalAnd(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLogicalAnd(this);
        }
    }

    public static class LogicalOr extends ExprNode {
        ExprNode left;
        ExprNode right;

        public LogicalOr(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLogicalOr(this);
        }
    }

    public static class LogicalNot extends ExprNode {
        ExprNode expr;

        public LogicalNot(ExprNode expr) {
            this.expr = expr;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitLogicalNot(this);
        }
    }

    // endregion

    // region Assignment

    public static class Assign extends ExprNode {
        ExprNode left;
        ExprNode right;

        public Assign(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitAssign(this);
        }
    }

    public static class AddAssign extends ExprNode {
        ExprNode left;
        ExprNode right;

        public AddAssign(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitAddAssign(this);
        }
    }

    public static class SubAssign extends ExprNode {
        ExprNode left;
        ExprNode right;

        public SubAssign(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitSubAssign(this);
        }
    }

    public static class MultiAssign extends ExprNode {
        ExprNode left;
        ExprNode right;

        public MultiAssign(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitMultiAssign(this);
        }
    }

    public static class DivAssign extends ExprNode {
        ExprNode left;
        ExprNode right;

        public DivAssign(ExprNode left, ExprNode right) {
            this.left = left;
            this.right = right;
        }

        public <R> R accept(ExprNode.Visitor<R> visitor) {
            return visitor.visitDivAssign(this);
        }
    }
    // endregion
//
//    public static class Joint extends ExprNode {
//
//    }
}
