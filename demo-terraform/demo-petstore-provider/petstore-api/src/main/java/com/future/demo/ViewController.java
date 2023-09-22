package com.future.demo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mapper.PetMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class ViewController {
    @Resource
    PetMapper petMapper;

    @GetMapping(value = "/")
    public String index(Model model) {
        List<PetModel> petModelList = this.petMapper.selectList(Wrappers.query());
        model.addAttribute("petList", petModelList);
        return "index";
    }
}
