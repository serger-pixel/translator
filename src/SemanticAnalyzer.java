import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyzer {
    //Таблица переменных
    public static HashMap<String, String> tableValue = new HashMap<>();

    public static String noValue = "-";
    public static String yesValue = "+";

    //Символы для разделения строк
    public static final String regexSep = "(;|:|OF)";

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
            if (String.valueOf(row.charAt(0)).matches(LexicalAnalyzer.regexIdentifier)) {
                String expression = row.substring(row.indexOf("="));
                Matcher matcher = pattern.matcher(expression);
                if (!tableValue.containsKey(String.valueOf(row.charAt(0)))) {
                    return false;
                }
                while (matcher.find()) {
                    if (tableValue.get(matcher.group()).equals(noValue)) {
                        return false;
                    }
                }
                tableValue.put(String.valueOf(row.charAt(0)), yesValue);
            }

            //WRITE/READ
            if (String.valueOf(row.charAt(0)).matches("(READ)|(WRITE)")) {
                Matcher matcher = pattern.matcher(row);
                while (matcher.find()) {
                    if (tableValue.get(matcher.group()).equals(noValue)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
