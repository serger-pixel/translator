import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        Path file = Paths.get("input/input.txt");

        String program = Files.readString(file);
        // 1. Сопоставление каждой лексеме - тип токена
        List<LexicalAnalyzer.Token> tokens = LexicalAnalyzer.tokenize(program);
        System.out.println("Токены:");
        tokens.forEach(System.out::println);


        // 2. Проверка синтаксиса
        ArrayDeque<LexicalAnalyzer.Token> tokenStack = new ArrayDeque<>();
        for (var token: tokens){
            if (token.type == LexicalAnalyzer.TokenType.INTNUMBER || token.type == LexicalAnalyzer.TokenType.IDENTIFIER){
                for(int i = 0; i < token.value.length(); i++){
                    LexicalAnalyzer.TokenType localType = token.type;
                    tokenStack.add(new LexicalAnalyzer.Token(localType, String.valueOf(token.value.charAt(i))));
                }
            }

            else{
                tokenStack.add(token);
            }
        }
        if (!SyntaxAnalyzer.read(tokenStack)){
            return;
        };

        System.out.println("\n--- Префиксная запись ---");
        // 3. Перевод исходной программы абстрактное синтаксическое дерево
        TranslatorToPrefix parser = new TranslatorToPrefix(tokens);
        AST.ProgramNode ast = parser.parseProgram();

        // 4. Перевод абстрактного синтаксического дерева в префиксную форму
        String prefix = ast.toPrefix();
        System.out.println(prefix);

        // 5. Выполнение программы
        System.out.println("\n--- Выполнение программы ---");
        ExecutionContext ctx = new ExecutionContext();
        String[] lines = prefix.split("\\n");
        for (var line: lines){
            PrefixInterpreter.evaluateLine(line, ctx);
        }
    }
}
