package sh.cxl.prismauth.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDSerializer implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            return UUID.fromString(jsonElement.getAsJsonPrimitive().getAsString().replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
        }

        throw new JsonParseException("invalid type for UUID object");
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(uuid.toString().replaceAll("-", ""));
    }
}
