package io.github.hvduong2k5.zalobot.internal.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JacksonAdapterTest {

    private JacksonAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JacksonAdapter();
    }

    // Dummy class to test snake_case and ignore unknown properties
    static class DummyData {
        private String myCustomField;
        private Integer someNumber;

        // No-arg constructor for Jackson
        public DummyData() {}

        public DummyData(String myCustomField, Integer someNumber) {
            this.myCustomField = myCustomField;
            this.someNumber = someNumber;
        }

        public String getMyCustomField() { return myCustomField; }
        public void setMyCustomField(String myCustomField) { this.myCustomField = myCustomField; }
        
        public Integer getSomeNumber() { return someNumber; }
        public void setSomeNumber(Integer someNumber) { this.someNumber = someNumber; }
    }

    @Test
    void toJson_usesSnakeCase() {
        DummyData data = new DummyData("hello world", 42);
        String json = adapter.toJson(data);
        assertEquals("{\"my_custom_field\":\"hello world\",\"some_number\":42}", json);
    }

    @Test
    void fromJson_readsSnakeCaseAndIgnoresUnknown() {
        // 'unknown_extra_field' should be ignored without throwing exception
        String json = "{\"my_custom_field\":\"test\",\"some_number\":99,\"unknown_extra_field\":true}";
        
        DummyData data = adapter.fromJson(json, DummyData.class);
        
        assertEquals("test", data.getMyCustomField());
        assertEquals(99, data.getSomeNumber());
    }

    @Test
    void fromJson_handlesMissingFields() {
        String json = "{\"my_custom_field\":\"test\"}";
        
        DummyData data = adapter.fromJson(json, DummyData.class);
        
        assertEquals("test", data.getMyCustomField());
        assertNull(data.getSomeNumber());
    }
}
