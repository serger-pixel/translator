import java.util.ArrayList;
import java.util.List;

class TranslatorToPrefix {
    private final List<LexicalAnalyzer.Token> tokens;
    private int current = 0;

    TranslatorToPrefix(List<LexicalAnalyzer.Token> tokens) {
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
            current++;
            List<String> vars = parseIdentList();
            current += 3;
            return new AST.VarDeclNode(vars);
        }

        if ("READ".equals(first)) {
            current++;
            current++;
            List<String> vars = parseIdentList();
            current += 2;
            return new AST.ReadNode(vars);
        }

        if ("WRITE".equals(first)) {
            current++;
            current++;
            List<String> vars = parseIdentList();
            current += 2;
            return new AST.WriteNode(vars);
        }

        if ("CASE".equals(first)) {
            current++;
            AST.ExpressionNode cond = parseExpressionFirst();
            current++;
            List<AST.CaseBranch> cases = new ArrayList<>();
            while (!"END_CASE".equals(tokens.get(current).value)) {
                AST.ExpressionNode constVal = parseExpressionFirst();
                current++; // ':'
                AST.StatementNode body = parseStatement();
                cases.add(new AST.CaseBranch(constVal, body));
            }
            current++;
            current++;
            return new AST.SwitchNode(cond, cases);
        }


        String var = advance().value; // идентификатор
        current++;
        AST.ExpressionNode expr = parseExpressionFirst();
        current++;
        return new AST.AssignNode(var, expr);
    }

    private List<String> parseIdentList() {
        List<String> list = new ArrayList<>();
        list.add(advance().value);
        while (",".equals(tokens.get(current).value)) {
            current++;
            list.add(advance().value);
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

        current++;
        AST.ExpressionNode expr = parseExpressionFirst();
        current++;
        return expr;
    }
}