package com.future.demo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "响应结果")
@Data
public class R<T> {
    @Schema(description = "状态码")
    private Integer code;
    @Schema(description = "错误消息")
    private String message;
    @Schema(description = "响应数据")
    private T data;

    public static<T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setData(data);
        return r;
    }
}
