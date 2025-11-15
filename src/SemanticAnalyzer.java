import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyzer {
    //Таблица переменных
    public static HashMap<String, String> tableValue = new HashMap<>();

    public static String noValue = "-";
    public static String yesValue = "+";

    //Символы для разделения строк
    public static final String regexSep = "(;|:|OF|BEGIN|END)";

    //Символы-разделители
    public  static final String regexBreak = "( |\\n)";

    //Заполнение таблицы переменных
    public static Boolean createTable(String program){
        int endDeclaration = program.indexOf(";");
        int startDeclaration = 0;
        String declaration = program.substring(startDeclaration, endDeclaration);
        Pattern pattern = Pattern.compile(LexicalAnalyzer.regexIdentifier);
        Matcher matcher = pattern.matcher(declaration);
        matcher.results();
        while(matcher.find()){
            if (tableValue.containsKey(matcher.group())){
                return false;
            }
            tableValue.put(matcher.group(), noValue);
        }
        return true;
    }


    //Проверка выражений или операторов(READ/WRITE)
    public static Boolean checkAssignment(String program) {
        program = program.replaceAll(regexBreak, "");
        String[] rows = program.split(regexSep);
        Pattern pattern = Pattern.compile(LexicalAnalyzer.regexIdentifier);
        for (String row : rows) {
            //Выражение
            if (row.indexOf('=') != -1) {
                String[] expression = row.split("(=)");
                if (!tableValue.containsKey(expression[0])) {
                    return false;
                }
                Matcher matcher = pattern.matcher(expression[1]);
                while (matcher.find()) {
                    if (tableValue.get(matcher.group()).equals(noValue)) {
                        return false;
                    }
                }
                tableValue.put(expression[0], yesValue);
            }

            //WRITE/READ
            pattern = Pattern.compile("(READ)|(WRITE)|(CASE)");
            Matcher matcher = pattern.matcher(row);
            if (matcher.find()) {
                pattern = Pattern.compile(LexicalAnalyzer.regexIdentifier);
                matcher = pattern.matcher(row);
                while (matcher.find()) {
                    if (!tableValue.containsKey(matcher.group()) || tableValue.get(matcher.group()).equals(noValue)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
