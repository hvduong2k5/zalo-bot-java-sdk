package io.github.hvduong2k5.zalobot.api.json;

/**
 * Contract for JSON serialization/deserialization.
 * <p>
 * The default implementation uses Jackson ({@code internal.json.JacksonAdapter}),
 * but users may provide a custom implementation via {@code ZaloBotClient.builder().jsonMapper(...)}.
 */
public interface JsonMapper {

    /**
     * Serializes an object to a JSON string.
     *
     * @param obj the object to serialize
     * @return JSON string representation
     */
    String toJson(Object obj);

    /**
     * Deserializes a JSON string to an object of the given class.
     *
     * @param json  the JSON string
     * @param clazz the target class
     * @param <T>   the target type
     * @return the deserialized object
     */
    <T> T fromJson(String json, Class<T> clazz);
}

