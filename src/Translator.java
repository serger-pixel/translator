import java.util.*;

public class Translator {
    public class ExecutionContext {
        private final Map<String, Integer> variables = new HashMap<>();

        public void setVariable(String name, Integer value) {
            variables.put(name, value);
        }

        public Integer getVariable(String name) {
            if (!variables.containsKey(name)) {
                // Семантический анализ уже должен был поймать это!
                throw new RuntimeException("Переменная не инициализирована: " + name);
            }
            return variables.get(name);
        }
    }



//Базовые классы
    abstract class ASTNode {
        public abstract void interpret(ExecutionContext ctx);
    }

    abstract class ExpressionNode extends ASTNode {
        public abstract Integer evaluate(ExecutionContext ctx);

        @Override
        public void interpret(ExecutionContext ctx) {
            evaluate(ctx); // просто вычислить (редко используется напрямую)
        }
    }

    public abstract class StatementNode {
        public abstract void interpret(ExecutionContext ctx);
    }


//Операнды
    class NumberNode extends ExpressionNode {
        int value;
        NumberNode(int value) { this.value = value; }
        @Override public Integer evaluate(ExecutionContext ctx) {
            return value;
        }
    }

    class VariableNode extends ExpressionNode {
        String name;
        VariableNode(String name) { this.name = name; }
        @Override public Integer evaluate(ExecutionContext ctx) {
            return ctx.getVariable(name);
        }
    }

    class BinaryOpNode extends ExpressionNode {
        ExpressionNode left, right;
        String op;
        BinaryOpNode(ExpressionNode left, String op, ExpressionNode right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }
        @Override
        public Integer evaluate(ExecutionContext ctx) {
            int l = left.evaluate(ctx);
            int r = right.evaluate(ctx);
            switch (op) {
                case "+": return l + r;
                case "-": return l - r;
                case "/":
                    if (r == 0) throw new RuntimeException("Деление на ноль");
                    return l / r;
                default: throw new RuntimeException("Неизвестный оператор: " + op);
            }
        }
    }


    //Операторы
    class AssignNode extends StatementNode {
        String varName;
        ExpressionNode expr;
        AssignNode(String varName, ExpressionNode expr) {
            this.varName = varName;
            this.expr = expr;
        }
        @Override
        public void interpret(ExecutionContext ctx) {
            Integer value = expr.evaluate(ctx);
            ctx.setVariable(varName, value);
        }
    }

    class WriteNode extends StatementNode {
        List<String> varNames;
        WriteNode(List<String> varNames) {
            this.varNames = varNames;
        }
        @Override
        public void interpret(ExecutionContext ctx) {
            for (int i = 0; i < varNames.size(); i++) {
                if (i > 0) System.out.print(" ");
                System.out.print(ctx.getVariable(varNames.get(i)));
            }
            System.out.println();
        }
    }

    class ReadNode extends StatementNode {
        static final Scanner scanner = new Scanner(System.in);
        List<String> varNames;
        ReadNode(List<String> varNames) {
            this.varNames = varNames;
        }
        @Override
        public void interpret(ExecutionContext ctx) {
            for (String name : varNames) {
                System.out.print("Введите " + name + ": ");
                int value = scanner.nextInt();
                ctx.setVariable(name, value);
            }
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
        StatementNode elseBody;

        SwitchNode(ExpressionNode condition, List<CaseBranch> cases, StatementNode elseBody) {
            this.condition = condition;
            this.cases = cases;
            this.elseBody = elseBody;
        }

        @Override
        public void interpret(ExecutionContext ctx) {
            Integer condVal = condition.evaluate(ctx);
            for (CaseBranch branch : cases) {
                Integer caseVal = branch.constant.evaluate(ctx);
                if (condVal.equals(caseVal)) {
                    branch.body.interpret(ctx);
                    return;
                }
            }
            if (elseBody != null) {
                elseBody.interpret(ctx);
            }
        }
    }

    //Программа
    class ProgramNode extends StatementNode {
        List<StatementNode> statements;
        ProgramNode(List<StatementNode> statements) {
            this.statements = statements;
        }
        @Override
        public void interpret(ExecutionContext ctx) {
            for (StatementNode stmt : statements) {
                stmt.interpret(ctx);
            }
        }
    }

}
