package dto;

import com.google.gson.JsonElement;

public class DatabaseRequest {
    private String type;
    private JsonElement key;
    private JsonElement value;

    public DatabaseRequest(String type) {
        this.type = type;
    }

    public DatabaseRequest(String type, JsonElement key) {
        this.type = type;
        this.key = key;
    }

    public DatabaseRequest(String type, JsonElement key, JsonElement value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getKey() {
        return key;
    }

    public void setKey(JsonElement key) {
        this.key = key;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DatabaseRequest{" +
                "type='" + type + '\'' +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
