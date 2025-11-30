import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzer {

    public enum TokenType {
        KEYWORD, OPERATOR, TYPE, IDENTIFIER, INTNUMBER, SPECIALSYMBOL
    }

    public static class Token {
        public final TokenType type;
        public final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + "('" + value + "')";
        }
    }


    private static final String regexKeyword = "\\b(BEGIN|VAR|OF|END)\\b";
    private static final String regexOperator = "\\b(READ|CASE|END_CASE|WRITE)\\b|[+\\-/~=]";
    private static final String regexType = "\\b(INTEGER)\\b";
    private static final String regexIdentifier = "\\b[a-z]{1,7}\\b";
    private static final String regexIntNumbers = "\\b\\d{1,8}\\b";
    private static final String regexSpecialSymbols = "[:;(),]";


    private static final LinkedHashMap<TokenType, String> tokenRegexMap = new LinkedHashMap<>();
    static {
        tokenRegexMap.put(TokenType.KEYWORD, regexKeyword);
        tokenRegexMap.put(TokenType.TYPE, regexType);
        tokenRegexMap.put(TokenType.OPERATOR, regexOperator);
        tokenRegexMap.put(TokenType.INTNUMBER, regexIntNumbers);
        tokenRegexMap.put(TokenType.SPECIALSYMBOL, regexSpecialSymbols);
        tokenRegexMap.put(TokenType.IDENTIFIER, regexIdentifier);
    }



    public static List<Token> tokenize(String program) {
        List<Token> tokens = new ArrayList<>();
        String code = program;

        String tokenPattern = tokenRegexMap.values().stream().reduce((a,b) -> a + "|" + b).get();
        Pattern pattern = Pattern.compile(tokenPattern);
        Matcher matcher = pattern.matcher(code);

        int pos = 0;
        while (matcher.find(pos)) {
            String lexeme = matcher.group();
            boolean matched = false;

            for (Map.Entry<TokenType, String> entry : tokenRegexMap.entrySet()) {
                if (lexeme.matches(entry.getValue())) {
                    tokens.add(new Token(entry.getKey(), lexeme));
                    matched = true;
                    break;
                }
            }
            pos = matcher.end();
        }


        String cleanInput = program.replaceAll("\\s+", "");

        int i = 0; // указатель в cleanInput
        int tokenIndex = 0; // текущий индекс в списке образцов

        // Шаг 3: проходим по строке
        while (i < cleanInput.length() && tokenIndex < tokens.size()) {
            String currentPattern = tokens.get(tokenIndex).value;
            int len = currentPattern.length();

            if (i + len > cleanInput.length()) {
                throw new RuntimeException("Недопустимая лексема: " + cleanInput.substring(i));
            }

            String substring = cleanInput.substring(i, i + len);
            if (substring.equals(currentPattern)) {
                i += len;
                tokenIndex++;
            } else {
                throw new RuntimeException("Недопустимая лексема: " + substring);
            }
        }
        return tokens;
    }

}

