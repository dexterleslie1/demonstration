package com.future.demo.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "员工新增vo")
@Data
public class EmployeeAddVo {
    @Schema(description = "员工姓名")
    private String name;
}
