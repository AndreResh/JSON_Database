package dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

public class DatabaseResponse {
    private String response;
    private String reason;
    private JsonElement value;

    public DatabaseResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }
}
