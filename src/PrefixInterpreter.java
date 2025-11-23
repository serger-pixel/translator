import java.util.*;

public class PrefixInterpreter {

    // Точка входа: выполняет ОДНУ строку префиксной записи
    public static void evaluateLine(String line, ExecutionContext ctx) {
        if (line.trim().isEmpty()) return;
        String[] tokens = line.trim().split("\\s+");
        int[] index = {0};
        evaluateStatement(tokens, index, ctx);
    }

    // Выполнение оператора
    private static void evaluateStatement(String[] tokens, int[] i, ExecutionContext ctx) {
        if (i[0] >= tokens.length) return;
        String op = tokens[i[0]++];

        switch (op) {
            case "VAR" -> {
                while (i[0] < tokens.length) {
                    ctx.setVariable(tokens[i[0]++], 0);
                }
            }

            case "=" -> {
                String var = tokens[i[0]++];
                int value = evaluateExpression(tokens, i, ctx);
                ctx.getVariable(var);
                ctx.setVariable(var, value);
            }

            case "READ" -> {
                Scanner sc = new Scanner(System.in);
                while (i[0] < tokens.length) {
                    String var = tokens[i[0]++];
                    System.out.print("Введите " + var + ": ");
                    int val = sc.nextInt();
                    ctx.getVariable(var);
                    ctx.setVariable(var, val);
                }
            }

            case "WRITE" -> {
                List<Integer> vals = new ArrayList<>();
                while (i[0] < tokens.length) {
                    String tok = tokens[i[0]++];

                    try {
                        vals.add(Integer.parseInt(tok));
                    } catch (NumberFormatException e) {
                        vals.add(ctx.getVariable(tok)); // <-- может выбросить исключение, если переменная не существует — оставляем
                    }
                }
                for (int j = 0; j < vals.size(); j++) {
                    if (j > 0) System.out.print(" ");
                    System.out.print(vals.get(j));
                }
                System.out.println();
            }

            case "CASE" -> {
                int condValue = evaluateExpression(tokens, i, ctx);
                i[0]++;

                boolean matched = false;
                while (i[0] < tokens.length && !")".equals(tokens[i[0]])) {
                    int caseVal = Integer.parseInt(tokens[i[0]++]);
                    i[0]++;

                    int bodyStart = i[0];
                    int depth = 1;
                    while (i[0] < tokens.length && depth > 0) {
                        if ("(".equals(tokens[i[0]])) depth++;
                        else if (")".equals(tokens[i[0]])) depth--;
                        if (depth > 0) i[0]++;
                    }

                    if (caseVal == condValue && !matched) {
                        String bodyStr = String.join(" ", Arrays.copyOfRange(tokens, bodyStart, i[0]));
                        evaluateLine(bodyStr, ctx);
                        matched = true;
                    }
                    i[0]++;
                }
            }
        }
    }

    // Вычисление выражения в префиксной форме
    private static int evaluateExpression(String[] tokens, int[] i, ExecutionContext ctx) {
        String token = tokens[i[0]++];

        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException ignored) {}

        if (!isOperator(token)) {
            return ctx.getVariable(token);
        }

        int left = evaluateExpression(tokens, i, ctx);
        int right = evaluateExpression(tokens, i, ctx);

        return switch (token) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "/" -> {
                if (right == 0) throw new RuntimeException("Деление на ноль");
                yield left / right;
            }
            default -> 0;
        };
    }

    private static boolean isOperator(String s) {
        return "+".equals(s) || "-".equals(s) || "/".equals(s) || "~".equals(s);
    }
}