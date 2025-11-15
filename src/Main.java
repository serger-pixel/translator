import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String program =
                "VAR a, b, babag:INTEGER;\n" +
                "BEGIN\n"  + "\n"  +
                "CASE (20+10) OF " +
                        "4: " +
                        "a = ~(1 + (10) / 0);" +
                        "3:" +
                        "a = 2; " +
                "END_CASE;\n"+
                "READ(a, b);\n"+
                "WRITE(a, b);\n"+
                "END";

        Boolean result = SyntaxAnalyzer.check(program);
        result = SemanticAnalyzer.createTable(program);
        result = SemanticAnalyzer.checkAssignment(program);
        Map<String, String> variables = SemanticAnalyzer.tableValue;
        System.out.println(result);
    }
}
