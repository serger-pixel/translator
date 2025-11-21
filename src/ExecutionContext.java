import java.util.HashMap;
import java.util.Map;


//Таблица переменных
public class ExecutionContext {
    private final Map<String, Integer> variables = new HashMap<>();

    public void setVariable(String name, Integer value) {
        variables.put(name, value);
    }

    public Integer getVariable(String name) {
        if (!variables.containsKey(name)) {
            // Семантический анализ уже должен был поймать это!
            throw new RuntimeException("Переменная не инициализирована: " + name);
        }
        return variables.get(name);
    }
}