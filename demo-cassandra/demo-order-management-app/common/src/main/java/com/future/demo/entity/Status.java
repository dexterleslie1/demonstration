package com.future.demo.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public enum Status {

    Unpay("未支付"),
    Undelivery("未发货"),
    Unreceive("未收货"),
    Received("已签收"),
    Canceled("买家取消");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
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
