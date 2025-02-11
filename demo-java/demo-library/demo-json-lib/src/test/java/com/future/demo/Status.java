package com.future.demo;

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

    // 注意：使用自定义 DTO 封装枚举类型返回给前端
    /*public static class MyEnumSerializer extends JsonSerializer<Enum<?>> {
        @Override
        public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Map<String, Object> result = new HashMap<>();
            result.put("name", value.name());

            // 获取所有字段 (可选)
            Field[] fields = value.getClass().getDeclaredFields();
            for (Field field : fields) {

                if (Modifier.isPrivate(field.getModifiers()) && !field.getName().equals("$VALUES")) {
                    try {
                        field.setAccessible(true);
                        result.put(field.getName(), field.get(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(); // 处理异常
                    }
                }
            }
            gen.writeObject(result);
        }
    }*/
}
