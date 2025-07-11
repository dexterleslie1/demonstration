package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.service.CommonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/id/picker")
@Validated
public class ApiController {
    @Resource
    CommonService commonService;
    @Resource
    CommonMapper commonMapper;

    /**
     * 获取随机id列表
     *
     * @param size 随机获取id的个数
     * @return
     */
    @GetMapping(value = "listIdRandomly")
    public ListResponse<Long> listIdRandomly(
            @NotNull(message = "请指定flag参数")
            @NotBlank(message = "请指定flag参数")
            @RequestParam(value = "flag") String flag,
            @RequestParam(value = "size", defaultValue = "1") int size
    ) {
        List<Long> bizIdList = commonService.listIdRandomly(flag, size);
        return ResponseUtils.successList(bizIdList);
    }

    /**
     * 新增id列表，id可以重复，有布隆过滤器去重机制
     * todo 布隆过滤器
     *
     * @return
     */
    @PostMapping(value = "addIdList")
    public ObjectResponse<String> addIdList(
            @NotNull(message = "请指定flag参数")
            @NotBlank(message = "请指定flag参数")
            @RequestParam(value = "flag") String flag,
            @RequestParam(value = "idList", required = false) List<Long> idList
    ) {
        if (idList != null && !idList.isEmpty())
            commonMapper.insertIdList(flag, idList);
        return ResponseUtils.successObject("成功新增id列表");
    }

    /**
     * 新增id列表，id可以重复，有布隆过滤器去重机制
     *
     * @return
     */
    @GetMapping(value = "testAddIdList")
    public ObjectResponse<String> testAddIdList(
            @NotNull(message = "请指定flag参数")
            @NotBlank(message = "请指定flag参数")
            @RequestParam(value = "flag") String flag
    ) {
        List<Long> idList = new ArrayList<>();
        while (idList.size() < 1024) {
            Long randomId = RandomUtil.randomLong(1, Long.MAX_VALUE);
            if (!idList.contains(randomId))
                idList.add(randomId);
        }
        commonMapper.insertIdList(flag, idList);
        return ResponseUtils.successObject("成功新增id列表");
    }

    /**
     * 初始化id列表相关表
     *
     * @param flag
     * @return
     */
    @PostMapping("init")
    public ObjectResponse<String> init(
            @NotNull(message = "请指定flag参数")
            @NotBlank(message = "请指定flag参数")
            @RequestParam(value = "flag") String flag
    ) {
        commonService.init(flag);
        return ResponseUtils.successObject("成功初始化");
    }

    /**
     * 重置指定flag的id，重置后id数据被清空，需要重新新增id列表
     *
     * @param flag
     * @return
     */
    @PutMapping(value = "reset")
    public ObjectResponse<String> reset(
            @NotNull(message = "请指定flag参数")
            @NotBlank(message = "请指定flag参数")
            @RequestParam(value = "flag") String flag
    ) {
        commonService.reset(flag);
        return ResponseUtils.successObject("成功重置");
    }
}
