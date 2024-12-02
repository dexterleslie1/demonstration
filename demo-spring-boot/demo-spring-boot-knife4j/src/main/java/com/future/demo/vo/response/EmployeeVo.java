package com.future.demo.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "员工信息vo")
@Data
public class EmployeeVo {
    @Schema(description = "员工id")
    private Long id;
    @Schema(description = "员工姓名")
    private String name;
}
