package com.future.demo.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "员工更新vo")
@Data
public class EmployeeUpdateVo {
    @Schema(description = "员工id")
    private Long id;
    @Schema(description = "员工姓名")
    private String name;
}
