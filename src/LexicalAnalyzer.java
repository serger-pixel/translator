import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    public static final String regexKeyWord = "(BEGIN|VAR|OF|END)";
    public static final String regexOperator = "(READ|CASE|end_case|WRITE|\\+|-|/|~)";
    public static final String regexType = "(INTEGER)";
    public static final String regexIdentifier = "[a-z]{1,7}";
    public static final String regexNotIdentifier = "[a-z]{7,}";
    public static final String regexIntNumbers = "\\d{1,8}";
    public static final String regexSpecialSymbols = "(=|:|;|\\(|\\)|,)";
    public static final String regexLexem =
            "^(?:$|\\n$|(?:KEYWORD|OPERATOR|TYPE|IDENT|INT|SPECIAL)\\[\\S+\\])$";

    public static final String KEYWORD = "KEYWORD";
    public static final String OPERATOR = "OPERATOR";
    public static final String TYPE = "TYPE";
    public static final String IDENTIFIER = "IDENT";
    public static final String INTNUMBER = "INT";
    public static final String SPECIALSYMBOL = "SPECIAL";

    public static Map<String, String> TokensAndRegex = Map.of(
            KEYWORD, regexKeyWord,
            TYPE, regexType,
            INTNUMBER, regexIntNumbers,
            SPECIALSYMBOL, regexSpecialSymbols
    );


    //Проверка лексем
    public static boolean checkLexems(String program){
        String[] lexems = program.split(" ");

        for(int i = 0; i < lexems.length; i++){
            if(!Pattern.matches(regexLexem, lexems[i])){ return false;}
        }
        return true;
    }

    //Проверка программы на верные лексемы
    public static boolean readProgram(String program){

        String result = program;
        result = result.replaceAll(regexNotIdentifier, "_");
        result = result.replaceAll("(END_CASE)", "end_case");
        result = result.replaceAll(regexOperator, " "+OPERATOR+"[$0]"+" ");
        for(Map.Entry<String, String> item : TokensAndRegex.entrySet())
        {
            result = result.replaceAll(item.getValue(), " "+item.getKey()+"[$0]"+" ");
        }
        result = result.replaceAll("(end_case)", "END_CASE");
        result = result.replaceAll(regexIdentifier, " " + IDENTIFIER +"[$0]" + " ");
        //System.out.println(result);
        return checkLexems(result);
    }

    //Сопоставление токена каждой лексеме - строчка
    public static ArrayDeque<String> transform(String program){
        String result = program;
        result = result.replaceAll(regexNotIdentifier, "_");
        result = result.replaceAll("(END_CASE)", "end_case");
        result = result.replaceAll(regexOperator,  " $0 ");
        for(Map.Entry<String, String> item : TokensAndRegex.entrySet())
        {
            result = result.replaceAll(item.getValue(), " $0 ");
        }
        result = result.replaceAll("(end_case)", " END_CASE" );
        result = result.replaceAll(regexIdentifier, " $0 ");
        ArrayList<String> listValues = new ArrayList<>(List.of(result.split("( |\\n){1,}")));
        listValues.remove("");
        return new ArrayDeque<>(listValues);

    }

}
