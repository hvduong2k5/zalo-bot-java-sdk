package io.github.hvduong2k5.zalobot.internal.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.exception.ZaloJsonException;

import java.util.Objects;

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
     * Creates an adapter using the supplied {@link ObjectMapper}.
     *
     * <p>The mapper is used as-is. The SDK does not modify or reconfigure it.
     * For correct serialization of SDK models, configure the mapper with
     * {@link PropertyNamingStrategies#SNAKE_CASE}.
     *
     * @param objectMapper the mapper to use; must not be {@code null}
     * @throws NullPointerException if {@code objectMapper} is {@code null}
     */
    public JacksonAdapter(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public String toJson(Object obj) {
        Objects.requireNonNull(obj, "obj must not be null");
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ZaloJsonException("Failed to serialize object to JSON: " + obj.getClass().getName(), e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        Objects.requireNonNull(json, "json must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            // Do not dump raw json into exception message to prevent logging sensitive data
            throw new ZaloJsonException("Failed to deserialize JSON to " + clazz.getName(), e);
        }
    }
}

