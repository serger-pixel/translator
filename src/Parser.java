import java.util.ArrayList;
import java.util.List;

class Parser {
    private final List<LexicalAnalyzer.Token> tokens;
    private int current = 0;

    Parser(List<LexicalAnalyzer.Token> tokens) {
        this.tokens = tokens;
    }

    private LexicalAnalyzer.Token advance() {
        return tokens.get(current++);
    }

    public AST.ProgramNode parseProgram() {
        List<AST.StatementNode> stmts = new ArrayList<>();
        while (current < tokens.size()) {
            String val = tokens.get(current).value;
            if ("BEGIN".equals(val) || "END".equals(val)) {
                current++; // пропускаем
                continue;
            }
            stmts.add(parseStatement());
        }
        return new AST.ProgramNode(stmts);
    }

    private AST.StatementNode parseStatement() {
        String first = tokens.get(current).value;

        if ("VAR".equals(first)) {
            current++; // пропускаем VAR
            List<String> vars = parseIdentList();
            current += 3; // пропускаем ':', 'INTEGER', ';'
            return new AST.VarDeclNode(vars);
        }

        if ("READ".equals(first)) {
            current++; // READ
            current++; // '('
            List<String> vars = parseIdentList();
            current += 2; // ')', ';'
            return new AST.ReadNode(vars);
        }

        if ("WRITE".equals(first)) {
            current++; // WRITE
            current++; // '('
            List<String> vars = parseIdentList();
            current += 2; // ')', ';'
            return new AST.WriteNode(vars);
        }

        if ("CASE".equals(first)) {
            current++; // CASE
            AST.ExpressionNode cond = parseExpressionFirst();
            current++; // OF
            List<AST.CaseBranch> cases = new ArrayList<>();
            while (!"END_CASE".equals(tokens.get(current).value)) {
                AST.ExpressionNode constVal = parseExpressionFirst();
                current++; // ':'
                AST.StatementNode body = parseStatement();
                cases.add(new AST.CaseBranch(constVal, body));
            }
            current++; // END_CASE
            current++; // ';'
            return new AST.SwitchNode(cond, cases);
        }

        // Присваивание: идентификатор = выражение ;
        String var = advance().value; // идентификатор
        current++; // '='
        AST.ExpressionNode expr = parseExpressionFirst();
        current++; // ';'
        return new AST.AssignNode(var, expr);
    }

    private List<String> parseIdentList() {
        List<String> list = new ArrayList<>();
        list.add(advance().value); // первый идентификатор
        while (",".equals(tokens.get(current).value)) {
            current++; // ','
            list.add(advance().value); // следующий идентификатор
        }
        return list;
    }

    private AST.ExpressionNode parseExpressionFirst() {
        AST.ExpressionNode expr = parseExpressionSecond();
        while ("+".equals(tokens.get(current).value) || "-".equals(tokens.get(current).value)) {
            String op = advance().value;
            AST.ExpressionNode right = parseExpressionSecond();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    private AST.ExpressionNode parseExpressionSecond() {
        AST.ExpressionNode expr = parseExpressionThird();
        while ("/".equals(tokens.get(current).value)) {
            String op = advance().value;
            AST.ExpressionNode right = parseExpressionThird();
            expr = new AST.BinaryOpNode(op, expr, right);
        }
        return expr;
    }

    private AST.ExpressionNode parseExpressionThird() {
        if ("~".equals(tokens.get(current).value)) {
            current++; // '~'
            AST.ExpressionNode operand = parseExpressionFouth();
            return new AST.UnaryOpNode("-", operand);
        }
        return parseExpressionFouth();
    }

    private AST.ExpressionNode parseExpressionFouth() {
        LexicalAnalyzer.TokenType type = tokens.get(current).type;
        if (type == LexicalAnalyzer.TokenType.INTNUMBER) {
            int val = Integer.parseInt(advance().value);
            return new AST.NumberNode(val);
        }
        if (type == LexicalAnalyzer.TokenType.IDENTIFIER) {
            String name = advance().value;
            return new AST.VariableNode(name);
        }
        // Скобки
        current++; // '('
        AST.ExpressionNode expr = parseExpressionFirst();
        current++; // ')'
        return expr;
    }
}