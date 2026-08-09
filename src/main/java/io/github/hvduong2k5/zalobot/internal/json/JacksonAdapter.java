package io.github.hvduong2k5.zalobot.internal.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.hvduong2k5.zalobot.api.json.JsonMapper;

/**
 * Jackson implementation of {@link JsonMapper}.
 * <p>
 * Configured automatically for Zalo Bot API requirements:
 * <ul>
 *   <li>Ignores unknown properties</li>
 *   <li>Uses snake_case for all JSON fields</li>
 * </ul>
 */
public final class JacksonAdapter implements JsonMapper {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private final ObjectMapper objectMapper;

    /**
     * Creates an adapter with a default, pre-configured ObjectMapper.
     */
    public JacksonAdapter() {
        this(DEFAULT_MAPPER);
    }

    /**
     * Creates an adapter with a custom ObjectMapper.
     * <p>
     * Note: The provided ObjectMapper must be configured with {@code PropertyNamingStrategies.SNAKE_CASE}
     * for the SDK POJOs to serialize correctly.
     *
     * @param objectMapper the custom mapper
     */
    public JacksonAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON: " + obj.getClass().getName(), e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + clazz.getName() + ": " + json, e);
        }
    }
}
