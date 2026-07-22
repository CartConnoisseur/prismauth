package sh.cxl.prismauth.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantSerializer implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return Instant.ofEpochSecond(jsonElement.getAsJsonPrimitive().getAsNumber().longValue());
        }

        throw new JsonParseException("invalid type for Date object");
    }

    @Override
    public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(instant.getEpochSecond());
    }
}
