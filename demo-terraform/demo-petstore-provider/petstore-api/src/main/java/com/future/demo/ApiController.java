package com.future.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mapper.PetMapper;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ListResponse;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/pet")
public class ApiController {

    @Resource
    PetMapper petMapper;

    @PostMapping("add")
    ObjectResponse<Long> add(@RequestParam(value = "name", defaultValue = "") String name,
                             @RequestParam(value = "age", defaultValue = "0") Integer age) throws Exception {
        PetModel petModel = new PetModel();
        petModel.setName(name);
        petModel.setAge(age);
        petModel.setCreateTime(new Date());
        this.petMapper.insert(petModel);
        return ResponseUtils.successObject(petModel.getId());
    }

    @DeleteMapping("delete")
    ObjectResponse<String> delete(@RequestParam(value = "id", defaultValue = "0") Long id) {
        this.petMapper.deleteById(id);
        return ResponseUtils.successObject("成功删除pet");
    }

    @PutMapping("update")
    ObjectResponse<String> update(@RequestParam(value = "id", defaultValue = "0") Long id,
                                  @RequestParam(value = "name", defaultValue = "") String name,
                                  @RequestParam(value = "age", defaultValue = "0") Integer age) {
        UpdateWrapper<PetModel> updateWrapper = Wrappers.update();
        updateWrapper.set("name", name);
        updateWrapper.set("age", age);
        updateWrapper.eq("id", id);
        this.petMapper.update(null, updateWrapper);

        return ResponseUtils.successObject("成功修改pet");
    }

    @GetMapping("get")
    ObjectResponse<PetModel> get(@RequestParam(value = "id", defaultValue = "0") Long id) {
        return ResponseUtils.successObject(this.petMapper.selectById(id));
    }

    @GetMapping("list")
    ListResponse<PetModel> list() {
        return ResponseUtils.successList(this.petMapper.selectList(Wrappers.query()));
    }
}
