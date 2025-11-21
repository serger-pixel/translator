import java.util.*;

public class PrefixInterpreter {

    // Точка входа: выполняет ОДНУ строку префиксной записи
    public static void evaluateLine(String line, ExecutionContext ctx) {
        if (line.trim().isEmpty()) return;
        String[] tokens = line.trim().split("\\s+");
        int[] index = {0}; // mutable индекс
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
                ctx.setVariable(var, value);
            }

            case "READ" -> {
                Scanner sc = new Scanner(System.in);
                while (i[0] < tokens.length) {
                    String var = tokens[i[0]++];
                    System.out.print("Введите " + var + ": ");
                    int val = sc.nextInt();
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
                        vals.add(ctx.getVariable(tok));
                    }
                }
                for (int j = 0; j < vals.size(); j++) {
                    if (j > 0) System.out.print(" ");
                    System.out.print(vals.get(j));
                }
                System.out.println();
            }

            case "CASE" -> {
                int cond = evaluateExpression(tokens, i, ctx);
                boolean matched = false;

                while (i[0] < tokens.length) {
                    // Попытка прочитать константу
                    try {
                        int caseVal = Integer.parseInt(tokens[i[0]]);
                        i[0]++; // consume константу

                        if (i[0] >= tokens.length) break;

                        // Следующий токен — тело в скобках?
                        String bodyToken = tokens[i[0]];
                        if (bodyToken.startsWith("(") && bodyToken.endsWith(")")) {
                            i[0]++; // consume тело
                            if (cond == caseVal && !matched) {
                                // Убираем скобки
                                String inner = bodyToken.substring(1, bodyToken.length() - 1).trim();
                                if (!inner.isEmpty()) {
                                    evaluateLine(inner, ctx); // рекурсивно
                                }
                                matched = true;
                            }
                        } else {
                            // Если нет скобок — ошибка или другой формат
                            break;
                        }
                    } catch (NumberFormatException e) {
                        // Константа не число — конец списка case
                        break;
                    }
                }
            }

            default -> throw new RuntimeException("Неизвестный оператор: " + op);
        }
    }

    // Вычисление выражения в префиксной форме
    private static int evaluateExpression(String[] tokens, int[] i, ExecutionContext ctx) {
        if (i[0] >= tokens.length) {
            throw new RuntimeException("Неожиданный конец выражения");
        }

        String token = tokens[i[0]++];

        // Число?
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException ignored) {}

        // Унарный минус? (в твоём случае, возможно, "~")
        if ("~".equals(token)) {
            return -evaluateExpression(tokens, i, ctx);
        }

        // Переменная?
        if (!isOperator(token)) {
            return ctx.getVariable(token);
        }

        // Бинарная операция
        int left = evaluateExpression(tokens, i, ctx);
        int right = evaluateExpression(tokens, i, ctx);

        return switch (token) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "/" -> {
                if (right == 0) throw new RuntimeException("Деление на ноль");
                yield left / right;
            }
            default -> throw new RuntimeException("Неизвестный оператор: " + token);
        };
    }

    private static boolean isOperator(String s) {
        return "+".equals(s) || "-".equals(s) || "/".equals(s) || "~".equals(s);
    }
}