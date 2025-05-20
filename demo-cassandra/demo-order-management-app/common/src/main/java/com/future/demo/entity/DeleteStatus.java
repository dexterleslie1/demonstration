package com.future.demo.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public enum DeleteStatus {
    Normal("正常"),
    Deleted("已删除");
    private String description;

    DeleteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 基于 Jackson 库自定义枚举类型的 Serializer 返回预期格式的 JSON 数据给前端
    public static class DeleteStatusSerializer extends StdSerializer<DeleteStatus> {
        public DeleteStatusSerializer() {
            this(DeleteStatus.class);
        }

        public DeleteStatusSerializer(Class t) {
            super(t);
        }

        @Override
        public void serialize(DeleteStatus deleteStatus, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("name");
            jsonGenerator.writeString(deleteStatus.name());
            jsonGenerator.writeFieldName("description");
            jsonGenerator.writeString(deleteStatus.getDescription());
            jsonGenerator.writeEndObject();
        }

    }
}
