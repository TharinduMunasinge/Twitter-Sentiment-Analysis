package sentiment;

import java.util.Map;
import java.util.HashMap;

public class EmoticonStrength extends TokenStrength {
    private Map<String, Double> data;

    public EmoticonStrength() {
        data = new HashMap<String, Double>();
        data.put(":D", 1.0);
        data.put("BD", 1.0);
        data.put("XD", 1.0);
        data.put("\\m/", 1.0);
        data.put(":)", 0.5);
        data.put("=)", 0.5);
        data.put(":-)", 0.5);
        data.put(":*", 0.5);
        data.put(":|", 0.0);
        data.put(":\\", 0.0);
        data.put(":(", -0.5);
        data.put("</3", -0.5);
        data.put("B(", -0.5);
        data.put(":’(", -1.0);
        data.put("X-(", -1.0);
    }

    public double extract(String emoticon, String metadata) {
        Double value = data.get(emoticon);
        if (value == null) {
            value = 0.0;
            System.out.println("Warning: unknown emoticon found: " + emoticon);
            //TODO log alert?
        }
        return value;
    }
}
