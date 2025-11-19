import java.util.ArrayList;
import java.util.List;

//Перевод программы в синтаксическое дерево
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

    public AST.ProgramNode parseProgram() {
        List<AST.StatementNode> stmts = new ArrayList<>();
        while (currentToken() != null) {
            if(currentToken().value.equals("BEGIN") || currentToken().value.equals("END")){
                continue;
            }
            stmts.add(parseStatement());
        }
        return new AST.ProgramNode(stmts);
    }

    private AST.StatementNode parseStatement() {
        if (checkType(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            String var = advance().value;
            if (match("=")) {
                AST.ExpressionNode expr = parseExpressionFirst();
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
            AST.ExpressionNode cond = parseExpressionFirst();
            expect("OF");
            List<AST.CaseBranch> cases = new ArrayList<>();
            while (!match("END_CASE")) {
                AST.ExpressionNode constVal = parseExpressionFirst(); // константа
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

    private AST.ExpressionNode parseExpressionFirst() {
        AST.ExpressionNode expr = parseExpressionSecond();

        while (match("+", "-")) {
            String op = tokens.get(current - 1).value;
            AST.ExpressionNode right = parseExpressionSecond();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    private AST.ExpressionNode parseExpressionSecond() {
        AST.ExpressionNode expr = parseExpressionThird();

        while (match("/")) {
            String op = tokens.get(current - 1).value;
            AST.ExpressionNode right = parseExpressionThird();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    private AST.ExpressionNode parseExpressionThird() {
        if (match("~")) {

            AST.ExpressionNode operand = parseExpressionFouth();
            return new AST.UnaryOpNode("-", operand);
        }

        return parseExpressionFouth();
    }

    private AST.ExpressionNode parseExpressionFouth() {

        if (checkType(LexicalAnalyzer.TokenType.INTNUMBER)) {
            return new AST.NumberNode(Integer.parseInt(advance().value));
        }
        if (checkType(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            return new AST.VariableNode(advance().value);
        }
        if (match("(")) {
            AST.ExpressionNode expr = parseExpressionFirst();
            expect(")");
            return expr;
        }
        throw new RuntimeException("Ожидался фактор (число, переменная или выражение в скобках)");
    }

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