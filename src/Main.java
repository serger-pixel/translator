import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Пример программы на твоём языке
        String program = """
            x = (5 + 3) / 2;
            READ(y, z);
            WRITE(x, y);
            CASE a OF
              10: b = 0;
              20: c = 1;
            END_CASE;
            """;

        // 1. Токенизация
        List<LexicalAnalyzer.Token> tokens = LexicalAnalyzer.tokenize(program);
        System.out.println("Токены:");
        tokens.forEach(System.out::println);
        System.out.println("\n--- Префиксная запись ---");

       // 2. Парсинг → AST
        Parser parser = new Parser(tokens);
        AST.ProgramNode ast = parser.parseProgram();
//
        // 3. Генерация промежуточного кода
        String prefix = ast.toPrefix();
        System.out.println(prefix);
    }
}
