import java.util.ArrayDeque;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Пример программы на твоём языке
        String program = """
            VAR x, y: INTEGER;
            BEGIN
            x = ~5 + 30 / 1;
            y = 2;
            WRITE(x, y);
            CASE;
            WRITE(x, y);
            END
            """;

        // 1. Сопоставление каждой лексеме - тип токена
        List<LexicalAnalyzer.Token> tokens = LexicalAnalyzer.tokenize(program);
        System.out.println("Токены:");
        tokens.forEach(System.out::println);
        System.out.println("\n--- Префиксная запись ---");


        // 2. Проверка синтаксиса
        ArrayDeque<LexicalAnalyzer.Token> tokenStack = new ArrayDeque<>();
        for (var token: tokens){
            if (token.type == LexicalAnalyzer.TokenType.INTNUMBER){
                for(int i = 0; i < token.value.length(); i++){
                    tokenStack.add(new LexicalAnalyzer.Token(LexicalAnalyzer.TokenType.INTNUMBER, String.valueOf(token.value.charAt(i))));
                }
            }
            else{
                tokenStack.add(token);
            }


        }
        SyntaxAnalyzer.read(tokenStack);

       // 3. Перевод исходной програмы абстрактное синтаксическое дерево
        Parser parser = new Parser(tokens);
        AST.ProgramNode ast = parser.parseProgram();

        ExecutionContext ctx = new ExecutionContext();

        // 4. абстрактного синтаксического дерева в префиксную форму
        String prefix = ast.toPrefix();
        System.out.println(prefix);

//         Примеры строк
        String[] lines = prefix.split("\\n");
        for (var line: lines){
            PrefixInterpreter.evaluateLine(line, ctx);
        }
    }
}
