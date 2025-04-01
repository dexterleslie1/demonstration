package com.future.demo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public enum Status {

    Create("创建"),
    Paying("支付中");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusEnumDTO toDto() {
        return StatusEnumDTO.builder().name(this).description(this.getDescription()).build();
    }

    // 基于 Jackson 库自定义枚举类型的 Serializer 返回预期格式的 JSON 数据给前端
    public static class StatusSerializer extends StdSerializer<Status> {
        public StatusSerializer() {
            this(Status.class);
        }

        public StatusSerializer(Class t) {
            super(t);
        }

        @Override
        public void serialize(Status status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("name");
            jsonGenerator.writeString(status.name());
            jsonGenerator.writeFieldName("description");
            jsonGenerator.writeString(status.getDescription());
            jsonGenerator.writeEndObject();
        }

    }
}
