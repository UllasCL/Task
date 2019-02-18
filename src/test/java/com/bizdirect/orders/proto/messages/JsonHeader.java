package com.bizdirect.orders.proto.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class JsonHeader {

    @NotNull
    @JsonProperty("payments_info")
    private Map<String, String> payments_info = new HashMap<>();

    public JsonHeader() {
    }

    public JsonHeader(@NotNull Map<String, String> requestMap) {
        this.payments_info = requestMap;
    }

    public Map<String, String> getRequestMap() {
        return payments_info;
    }

    public void setRequestMap(Map<String, String> requestMap) {
        this.payments_info = requestMap;
    }
}