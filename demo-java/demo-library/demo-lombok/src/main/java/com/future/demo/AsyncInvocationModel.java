package com.future.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AsyncInvocationModel {
    protected final static ObjectMapper OMInstance = new ObjectMapper();

    private AsyncInvocationType type;
    private Map<String, Object> parameters;

    /**
     * @return
     */
    public String toJson() {
        String json = null;
        try {
            json = OMInstance.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    /**
     * @param json
     * @return
     */
    public static AsyncInvocationModel fromJson(String json, Class<? extends AsyncInvocationModel> cls) throws IOException {
        AsyncInvocationModel asyncInvocationModel = null;
        try {
            asyncInvocationModel = OMInstance.readValue(json, cls);
        } catch (IOException e) {
            throw e;
        }
        return asyncInvocationModel;
    }
}
