import java.util.ArrayList;
import java.util.List;

class Parser {
    private final List<LexicalAnalyzer.Token> tokens;
    private int current = 0;

    Parser(List<LexicalAnalyzer.Token> tokens) {
        this.tokens = tokens;
    }

    private LexicalAnalyzer.Token currentToken() {
        if (current >= tokens.size()) return null;
        return tokens.get(current);
    }

    private LexicalAnalyzer.Token advance() {
        return tokens.get(current++);
    }

    private boolean match(String... values) {
        LexicalAnalyzer.Token tok = currentToken();
        if (tok == null) return false;
        for (String val : values) {
            if (val.equals(tok.value)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean checkType(LexicalAnalyzer.TokenType type) {
        LexicalAnalyzer.Token tok = currentToken();
        return tok != null && tok.type == type;
    }

    private boolean checkValue(String value) {
        LexicalAnalyzer.Token tok = currentToken();
        return tok != null && value.equals(tok.value);
    }

    // === ОСНОВНЫЕ МЕТОДЫ ===

    public AST.ProgramNode parseProgram() {
        List<AST.StatementNode> stmts = new ArrayList<>();
        while (currentToken() != null) {
            stmts.add(parseStatement());
        }
        return new AST.ProgramNode(stmts);
    }

    private AST.StatementNode parseStatement() {
        if (checkType(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            // x = ...
            String var = advance().value;
            if (match("=")) {
                AST.ExpressionNode expr = parseExpression();
                expect(";");
                return new AST.AssignNode(var, expr);
            }
            throw new RuntimeException("Ожидалось '=' после идентификатора");
        }
        if (match("READ")) {
            expect("(");
            List<String> vars = parseIdentList();
            expect(")");
            expect(";");
            return new AST.ReadNode(vars);
        }
        if (match("WRITE")) {
            expect("(");
            List<String> vars = parseIdentList();
            expect(")");
            expect(";");
            return new AST.WriteNode(vars);
        }
        if (match("CASE")) {
            AST.ExpressionNode cond = parseExpression();
            expect("OF");
            List<AST.CaseBranch> cases = new ArrayList<>();
            while (!match("END_CASE")) {
                AST.ExpressionNode constVal = parseExpression(); // константа
                expect(":");
                AST.StatementNode body = parseStatement(); // например: x = 0;
                cases.add(new AST.CaseBranch(constVal, body));
            }
            expect(";");
            return new AST.SwitchNode(cond, cases);
        }
        throw new RuntimeException("Неизвестный оператор");
    }

    private List<String> parseIdentList() {
        List<String> list = new ArrayList<>();
        list.add(expectIdent());
        while (match(",")) {
            list.add(expectIdent());
        }
        return list;
    }

    // --- Высокий уровень: выражения с + и - ---
    private AST.ExpressionNode parseExpression() {
        AST.ExpressionNode expr = parseTerm(); // сначала парсим терм

        // Обрабатываем цепочку: expr + term - term + ...
        while (match("+", "-")) {
            String op = tokens.get(current - 1).value; // только что съеденный оператор
            AST.ExpressionNode right = parseTerm();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    // --- Средний уровень: термы с * и / ---
    private AST.ExpressionNode parseTerm() {
        AST.ExpressionNode expr = parseFactor();

        while (match("/")) {
            String op = tokens.get(current - 1).value;
            AST.ExpressionNode right = parseFactor();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    // --- Низкий уровень: атомарные значения ---
    private AST.ExpressionNode parseFactor() {
        if (checkType(LexicalAnalyzer.TokenType.INTNUMBER)) {
            return new AST.NumberNode(Integer.parseInt(advance().value));
        }
        if (checkType(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            return new AST.VariableNode(advance().value);
        }
        if (match("(")) {
            AST.ExpressionNode expr = parseExpression();
            expect(")");
            return expr;
        }
        throw new RuntimeException("Ожидался фактор (число, переменная или выражение в скобках)");
    }

    // Вспомогательные
    private String expectIdent() {
        if (!checkType(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            throw new RuntimeException("Ожидался идентификатор");
        }
        return advance().value;
    }

    private void expect(String symbol) {
        if (!match(symbol)) {
            throw new RuntimeException("Ожидался символ: " + symbol);
        }
    }
}