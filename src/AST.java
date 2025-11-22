import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Узлы синтаксического дерева
public class  AST {
    // Базовый узел
    abstract static class  ASTNode {
        public abstract String toPrefix();
    }

    // Выражения
    abstract static class  ExpressionNode extends ASTNode {}

    static class  NumberNode extends ExpressionNode {
        int value;
        NumberNode(int value) { this.value = value; }
        @Override public String toPrefix() { return String.valueOf(value); }
    }

    static class  VariableNode extends ExpressionNode {
        String name;
        VariableNode(String name) { this.name = name; }
        @Override public String toPrefix() { return name; }
    }

    static class UnaryOpNode extends ExpressionNode {
        public BinaryOpNode node;

        public UnaryOpNode(String operator, ExpressionNode operand) {
            node = new BinaryOpNode("-", new NumberNode(0), operand);
        }

        @Override
        public String toPrefix() {
            return node.toPrefix();
        }

    }

    static class  BinaryOpNode extends ExpressionNode {
        String op;
        ExpressionNode left, right;
        BinaryOpNode(String op, ExpressionNode left, ExpressionNode right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
        @Override
        public String toPrefix() {
            return op + " " + left.toPrefix() + " " + right.toPrefix();
        }
    }

    // Операторы
    abstract static class  StatementNode extends ASTNode {}

    public static class VarDeclNode extends StatementNode {
        public final List<String> variableNames;

        public VarDeclNode(List<String> variableNames) {
            this.variableNames = variableNames;
        }

        @Override
        public String toPrefix() {
            return "VAR " + String.join(" ", variableNames);
        }
    }

    static class  AssignNode extends StatementNode {
        String var;
        ExpressionNode expr;
        AssignNode(String var, ExpressionNode expr) {
            this.var = var;
            this.expr = expr;
        }
        @Override
        public String toPrefix() {
            return "= " + var + " " + expr.toPrefix();
        }
    }

    static class  ReadNode extends StatementNode {
        List<String> vars;
        ReadNode(List<String> vars) { this.vars = vars; }
        @Override
        public String toPrefix() {
            return "READ " + String.join(" ", vars);
        }
    }

    static class  WriteNode extends StatementNode {
        List<String> vars;
        WriteNode(List<String> vars) { this.vars = vars; }
        @Override
        public String toPrefix() {
            return "WRITE " + String.join(" ", vars);
        }
    }

    static class  CaseBranch {
        ExpressionNode constant;
        StatementNode body;
        CaseBranch(ExpressionNode constant, StatementNode body) {
            this.constant = constant;
            this.body = body;
        }
    }

    static class  SwitchNode extends StatementNode {
        ExpressionNode condition;
        List<CaseBranch> cases;
        SwitchNode(ExpressionNode condition, List<CaseBranch> cases) {
            this.condition = condition;
            this.cases = cases;
        }
        @Override
        public String toPrefix() {
            StringBuilder sb = new StringBuilder();
            sb.append("CASE ").append(condition.toPrefix());
            sb.append(" (");
            for (CaseBranch branch : cases) {
                sb.append(branch.constant.toPrefix())
                        .append(" (").append(branch.body.toPrefix()).append(") ");
            }
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
            return sb.toString();
        }
    }

    static class  ProgramNode extends StatementNode {
        List<StatementNode> statements;
        ProgramNode(List<StatementNode> statements) {
            this.statements = statements;
        }
        @Override
        public String toPrefix() {
            return statements.stream()
                    .map(ASTNode::toPrefix)
                    .collect(Collectors.joining("\n"));
        }
    }

}
