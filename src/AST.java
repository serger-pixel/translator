import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AST {
    // Базовый узел
    abstract class ASTNode {
        public abstract String toPrefix();
    }

    // Выражения
    abstract class ExpressionNode extends ASTNode {}

    class NumberNode extends ExpressionNode {
        int value;
        NumberNode(int value) { this.value = value; }
        @Override public String toPrefix() { return String.valueOf(value); }
    }

    class VariableNode extends ExpressionNode {
        String name;
        VariableNode(String name) { this.name = name; }
        @Override public String toPrefix() { return name; }
    }

    class BinaryOpNode extends ExpressionNode {
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
    abstract class StatementNode extends ASTNode {}

    class AssignNode extends StatementNode {
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

    class ReadNode extends StatementNode {
        List<String> vars;
        ReadNode(List<String> vars) { this.vars = vars; }
        @Override
        public String toPrefix() {
            return "READ " + String.join(" ", vars);
        }
    }

    class WriteNode extends StatementNode {
        List<String> vars;
        WriteNode(List<String> vars) { this.vars = vars; }
        @Override
        public String toPrefix() {
            return "WRITE " + String.join(" ", vars);
        }
    }

    class CaseBranch {
        ExpressionNode constant;
        StatementNode body;
        CaseBranch(ExpressionNode constant, StatementNode body) {
            this.constant = constant;
            this.body = body;
        }
    }

    class SwitchNode extends StatementNode {
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
            for (CaseBranch branch : cases) {
                sb.append(" ").append(branch.constant.toPrefix())
                        .append(" (").append(branch.body.toPrefix()).append(")");
            }
            return sb.toString();
        }
    }

    class ProgramNode extends StatementNode {
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
