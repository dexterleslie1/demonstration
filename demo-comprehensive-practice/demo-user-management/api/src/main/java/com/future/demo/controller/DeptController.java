package com.future.demo.controller;

import com.future.demo.R;
import com.future.demo.entity.Dept;
import com.future.demo.service.DeptService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dept")
public class DeptController {

    @Resource
    DeptService deptService;

    /**
     * 查询所有部门列表
     *
     * @return
     */
    @GetMapping("list")
    public R list() {
        List<Dept> deptList = deptService.list();
        return R.success(deptList);
    }
}
