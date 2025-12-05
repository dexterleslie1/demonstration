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
     * @param name
     * @return
     */
    @GetMapping("list")
    public R list(@RequestParam(value = "name", defaultValue = "") String name) {
        List<Dept> deptList = deptService.list(name);
        return R.success(deptList);
    }

    /**
     * 根据部门id删除部门
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    public R delete(@RequestParam("id") Long id) {
        deptService.delete(id);
        return R.success();
    }

    /**
     * 新增部门
     *
     * @param dept
     * @return
     */
    @PostMapping("add")
    public R add(@RequestBody Dept dept) {
        deptService.add(dept);
        return R.success();
    }

    /**
     * 更新部门信息
     *
     * @param dept
     * @return
     */
    @PutMapping("update")
    public R update(@RequestBody Dept dept) {
        deptService.update(dept);
        return R.success();
    }
}
