import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Пример программы на твоём языке
        String program = """
            VAR x, y: INTEGER;
            BEGIN
            x = ~5 + 3 / 2;
            y = 2;
            WRITE(x, y);
            CASE (20) OF
              10: x = 0;
              20: y = 1;
            END_CASE;
            WRITE(x, y);
            END
            """;

        // 1. Сопоставление каждой лексеме - тип токена
        List<LexicalAnalyzer.Token> tokens = LexicalAnalyzer.tokenize(program);
        System.out.println("Токены:");
        tokens.forEach(System.out::println);
        System.out.println("\n--- Префиксная запись ---");

       // 2. Перевод исходной програмыы абстрактное синтаксическое дерево
        Parser parser = new Parser(tokens);
        AST.ProgramNode ast = parser.parseProgram();

        ExecutionContext ctx = new ExecutionContext();

        // 3. абстрактного синтаксического дерева в префиксную форму
        String prefix = ast.toPrefix();
        System.out.println(prefix);

        // Примеры строк
        String[] lines = prefix.split("\\n");
        for (var line: lines){
            PrefixInterpreter.evaluateLine(line, ctx);
        }
    }
}
