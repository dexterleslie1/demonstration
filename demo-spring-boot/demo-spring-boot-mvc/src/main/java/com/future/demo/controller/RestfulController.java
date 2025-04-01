package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.vo.request.PersonAddVo;
import com.future.demo.vo.request.PersonUpdateVo;
import com.future.demo.vo.response.PersonVo;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// 演示restful风格的控制器
// 演示spring框架的数据校验功能
@RestController
@RequestMapping("/api/v1/restful")
public class RestfulController {

    // 新增person
    @RequestMapping(value = "/person", method = RequestMethod.POST)
    public ObjectResponse<Person> add(@RequestBody @Validated PersonAddVo vo/* 使用vo接收前端提交的数据 */) {
        Person person = new Person();
        BeanUtils.copyProperties(vo, person);
        person.setId(10L);
        ObjectResponse<Person> response = new ObjectResponse<>();
        response.setData(person);
        return response;
    }

    // 根据id获取person
    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    public ObjectResponse<PersonVo> get(@PathVariable("id") Long id) {
        Person person = new Person();
        person.setId(id);
        person.setName("张三");
        person.setAge(18);
        person.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        person.setAddress(new Person.Address("北京市", "海淀区"));

        PersonVo vo = new PersonVo();
        BeanUtils.copyProperties(person, vo);

        ObjectResponse<PersonVo> response = new ObjectResponse<>();
        response.setData(vo);
        return response;
    }

    // 根据id更新person
    @RequestMapping(value = "/person", method = RequestMethod.PUT)
    public ObjectResponse<PersonVo> update(@RequestBody PersonUpdateVo vo) {
        PersonVo personVo = new PersonVo();
        BeanUtils.copyProperties(vo, personVo);
        ObjectResponse<PersonVo> response = new ObjectResponse<>();
        response.setData(personVo);
        return response;
    }

    // 根据id删除person
    @RequestMapping(value = "/person/{id}", method = RequestMethod.DELETE)
    public ObjectResponse<String> delete(@PathVariable("id") Long id) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功删除id=" + id);
        return response;
    }
}
