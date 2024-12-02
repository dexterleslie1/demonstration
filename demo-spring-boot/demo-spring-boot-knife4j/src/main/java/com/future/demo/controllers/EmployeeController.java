package com.future.demo.controllers;

import com.future.demo.response.R;
import com.future.demo.vo.request.EmployeeAddVo;
import com.future.demo.vo.request.EmployeeUpdateVo;
import com.future.demo.vo.response.EmployeeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

// 描述控制器的作用
@Tag(name = "员工管理")
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    // 描述接口的作用
    @Operation(summary = "新增员工")
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public R<String> add(@RequestBody EmployeeAddVo employeeAddVo) {
        return R.ok("成功新增员工");
    }

    @Operation(summary = "根据id查询员工信息")
    @Parameters({
            @Parameter(name = "id", description = "员工id", example = "100", required = true, in = ParameterIn.PATH),
    })
    @RequestMapping(value = "/employee/{id}", method = RequestMethod.GET)
    public R<EmployeeVo> get(@PathVariable("id") Long id) {
        EmployeeVo employeeVo = new EmployeeVo();
        employeeVo.setId(id);
        employeeVo.setName("张三");
        return R.ok(employeeVo);
    }

    @Operation(summary = "根据id删除员工")
    @Parameters({
            @Parameter(name = "id", description = "员工id", example = "100", required = true, in = ParameterIn.PATH),
    })
    @RequestMapping(value = "/employee/{id}", method = RequestMethod.DELETE)
    public R<String> delete(@PathVariable("id") Long id) {
        return R.ok("成功删除员工");
    }

    @Operation(summary = "根据id更新员工")
    @RequestMapping(value = "/employee", method = RequestMethod.PUT)
    public R<String> update(@RequestBody EmployeeUpdateVo employeeUpdateVo) {
        return R.ok("成功更新员工");
    }
}
