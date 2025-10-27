import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public  class SyntaxAnalyzer {
    //Пустой таккт
    private static String emptyProcess = "-";
    //Обычный такт
    private static String fullProcess = "+";


    //Таблица нетерминалов
    private static LinkedHashMap<String, String> notTerminals = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("A", "<Цифра>"),
            Map.entry("B", "<Конст>"),
            Map.entry("C", "<Конст'>"),
            Map.entry("D", "<Идент>"),
            Map.entry("E", "<Идент'>"),
            Map.entry("F", "<Ун. оп.>"),
            Map.entry("G", "<Бин. оп.>"),
            Map.entry("H", "<Буква>"),
            Map.entry("I", "<Программа>"),
            Map.entry("J", "<Объявление переменных>"),
            Map.entry("K", "<Описание вычислений>"),
            Map.entry("L", "<Список переменных>"),
            Map.entry("M", "<Список переменных'>"),
            Map.entry("N", "<Присваивание>"),
            Map.entry("O", "<Список операторов>"),
            Map.entry("P", "<Оператор>"),
            Map.entry("Q", "<Выражение>"),
            Map.entry("R", "<Выражение'>"),
            Map.entry("S", "<Операнд>"),
            Map.entry("T", "<Операнд'>"),
            Map.entry("U", "<Выбор>"),
            Map.entry("V", "<Список выбора>"),
            Map.entry("W", "<Список выбора'>")
    ));

    //Таблица переходов
    private static LinkedHashMap<String, List<String>> jumpTable = new LinkedHashMap<>();

    private static void createJumpTable(){
        char[] operators = {'+', '-', '/'};
        //1-ая группа
        jumpTable.put("BEGIN,K", List.of("+", "END", "O"));
        jumpTable.put("VAR,J", List.of("+", ";INTEGER:", "L"));
        jumpTable.put(",,M", List.of("+", "M", "D", ","));
        jumpTable.put("(,T", List.of("+", ")", "Q", "("));
        jumpTable.put("WRITE,P", List.of("+", ";", "L"));
        jumpTable.put("READ,P", List.of("+", ";", "L"));
        jumpTable.put("CASE,P", List.of("+", ";END_CASE", "V", "OF", "Q"));
        jumpTable.put("~,F", List.of("+"));

        for (char op : operators) {
            jumpTable.put(op + ",G", List.of("+"));
        }

        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",A", List.of("+"));
        }

        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",H", List.of("+"));
        }



        //2-ая группа


        // Переходы для <Конст'>
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",C", List.of("-", "C", "A"));
        }


        // Переходы для <Конст>
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",B", List.of("-", "C", "A"));
        }


        // Переходы для <Идент'>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",E", List.of("-", "H", "E"));
        }


        // Переходы для <Идент>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",D", List.of("-", "H", "E"));
        }


        // Переходы для <Выбор>
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",U", List.of("-", ";", "N", ":", "B"));
        }


        // Переходы для <Список выбора'>
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",W", List.of("-", "W", "U"));
        }


        // Переходы для <Список выбора>
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",V", List.of("-", "W", "U"));
        }


        // Переходы для <Оператор>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",P", List.of("-", "N"));
        }


        // Переходы для <Операнд'>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",T", List.of("-", "D"));
        }
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",T", List.of("-", "B"));
        }
        jumpTable.put("(,T", List.of("-", "T"));


        // Переходы для <Операнд>
        jumpTable.put("~,S", List.of("-", "T", "F"));
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",S", List.of("-", "T"));
        }
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",S", List.of("-", "T"));
        }
        jumpTable.put("(,S", List.of("-", "T"));


        // Переходы для <Выражения'>
        for (char op : operators) {
            jumpTable.put(op + ",R", List.of("-", "R", "S", "G"));
        }


        // Переходы для <Выражение>
        jumpTable.put("~,Q", List.of("-", "R", "S"));
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",Q", List.of("-", "R", "S"));
        }
        for (char c = '0'; c <= '9'; c++) {
            jumpTable.put(c + ",Q", List.of("-", "R", "S"));
        }
        jumpTable.put("(,Q", List.of("-", "R", "S"));


        // Переходы для <Программа>
        jumpTable.put("VAR,I", List.of("-", "K", "J"));


        // Переходы для <Список переменных>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",L", List.of("-", "M", "D"));
        }


        // Переходы для <Список операторов>
        String[] ops = {"READ", "CASE", "WRITE"};
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",O", List.of("-", "O", "P"));
        }
        for (String op : ops) {
            jumpTable.put(op + ",O", List.of("-", "O", "P"));
        }


        // Переходы для <Присваивание>
        for (char c = 'a'; c <= 'z'; c++) {
            jumpTable.put(c + ",N", List.of("-", ";", "Q", "=", "D"));
        }



        //3-ая группа


        // Переходы для <Конст>
        jumpTable.put(":,C", List.of("-"));
        jumpTable.put("+,C", List.of("-"));
        jumpTable.put("-,C", List.of("-"));
        jumpTable.put("/,C", List.of("-"));


        // Переходы для <Идент'>
        jumpTable.put(",,E", List.of("-"));
        jumpTable.put("=,E", List.of("-"));
        jumpTable.put("+,E", List.of("-"));
        jumpTable.put("-,E", List.of("-"));
        jumpTable.put("/,E", List.of("-"));


        // Переходы для <Список выбора'>
        jumpTable.put("END_CASE,W", List.of("-"));


        // Переходы для <Выражение'>
        jumpTable.put(";,R", List.of("-"));
        jumpTable.put("),R", List.of("-"));
        jumpTable.put("OF,R", List.of("-"));
        jumpTable.put("+,R", List.of("-", "R", "S", "G"));
        jumpTable.put("-,R", List.of("-", "R", "S", "G"));
        jumpTable.put("/,R", List.of("-", "R", "S", "G"));


        // Переходы для <Список переменных'>
        jumpTable.put(":,M", List.of("-"));
        jumpTable.put("),M", List.of("-"));


        // Переходы для <Список операторов>
        jumpTable.put("END,O", List.of("-"));



        //4-ая группа
        jumpTable.put(":,:", List.of("+"));
        jumpTable.put(";,;", List.of("+"));
        jumpTable.put("OF,OF", List.of("+"));
        jumpTable.put("END_CASE,END_CASE", List.of("+"));
        jumpTable.put("(,(", List.of("+"));
        jumpTable.put("),)", List.of("+"));
        jumpTable.put("=,=", List.of("+"));
        jumpTable.put("INTEGER,INTEGER", List.of("+"));
        jumpTable.put("END,END", List.of("+"));
    }
}
