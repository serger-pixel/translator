import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String program =
                "VAR a, b, babag, a:INTEGER;\n" +
                "BEGIN\n" +
                "CASE (a+b) OF " +
                        "4: " +
                        "a = (1 + (10));" +
                        "3:" +
                        "a = 2; " +
                "END_CASE;\n"+
                "READ(a, b, c);\n"+
                "WRITE(a, b, c);\n"+
                "END";

        Boolean result = SyntaxAnalyzer.check(program);
        result = SemanticAnalyzer.createTable(program);
        result = SemanticAnalyzer.checkAssignment(program);
        Map<String, String> variables = SemanticAnalyzer.tableValue;
        System.out.println(result);
    }
}
