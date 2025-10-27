import java.util.ArrayDeque;

public class Main {
    public static void main(String[] args) {

        String program =
                "VAR a, b, hoho:INTEGER\n" +
                "BEGIN\n" +
                "b = 1;\n" +
                "hoho = 3;\n" +
                "a = (b+hoho);\n" +
                "CASE (a+b) OF 4: c = 10 END_CASE;\n"+
                "READ(a, b, c);\n"+
                "WRITE(a, b, c);\n"+
                "END";

        ArrayDeque<String> result = LexicalAnalyzer.transform(program);
        System.out.println(result);
    }
}
