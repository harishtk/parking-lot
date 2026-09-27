import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;

record FloorId(@JsonValue String id) {
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public FloorId {
    }
}

public class TestJacksonMap {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<FloorId, String> map = new HashMap<>();
        map.put(new FloorId("F1"), "Floor 1");
        String json = mapper.writeValueAsString(map);
        System.out.println("Serialized: " + json);
        Map<FloorId, String> deserialized = mapper.readValue(json, new TypeReference<Map<FloorId, String>>() {});
        System.out.println("Deserialized: " + deserialized);
    }
}
