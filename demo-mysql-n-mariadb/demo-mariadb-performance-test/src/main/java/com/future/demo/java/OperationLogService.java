package com.future.demo.java;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.future.demo.java.mapper.OperationLogMapper;
import com.yyd.common.bean.ModelMapperUtil;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.PageResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户操作日志服务
 */
@Service
@Slf4j
public class OperationLogService {

    @Resource
    OperationLogMapper operationLogMapper;

    /**
     * 添加操作日志
     *
     * @param userId        当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志
     * @param operatorId
     * @param passiveId
     * @param operationType
     * @param content
     */
    public void add(Long userId,
                    Long operatorId,
                    Long passiveId,
                    OperationType operationType,
                    String content) {
        Assert.isTrue(userId != null && userId > 0, "参数错误1");

        Assert.isTrue(operatorId != null && operatorId > 0, "参数错误2");

        Assert.isTrue(operationType != null, "参数错误3");

        OperationLogModel model = new OperationLogModel();
        model.setAuthId(userId);
        model.setOperatorId(operatorId);
        model.setPassiveId(passiveId);
        model.setOperationType(operationType);
        model.setContent(content);
        model.setCreateTime(new Date());
        this.operationLogMapper.insert(model);
    }

    /**
     * 查询指定用户操作日志
     *
     * @param contextUserId
     * @param operationTypeList
     * @param page
     * @param size
     * @return
     */
    public PageResponse<OperationLogVo> list(Long contextUserId, List<OperationType> operationTypeList, int page, int size) throws BusinessException {
        if (page <= 0) {
            page = 1;
        }

        Assert.isTrue(size >= 1 && size <= 1000, "参数错误1");

        Page<OperationLogModel> pageObject = new Page<>(page, size);
        pageObject.orders().add(new OrderItem("id", false));
        QueryWrapper<OperationLogModel> queryWrapper = Wrappers.query();
        // optimize: 使用覆盖索引会提高查询效率，但是目前测试没有发现很显著的性能提升
        queryWrapper.select("id", "auth_id", "operation_type");
        queryWrapper.eq("auth_id", contextUserId);

        if (operationTypeList != null && operationTypeList.size() > 0) {
            queryWrapper.in("operation_type", operationTypeList);
        }

        pageObject = this.operationLogMapper.selectPage(pageObject, queryWrapper);

        if (pageObject.getRecords() != null && pageObject.getRecords().size() > 0) {

            List<OperationLogModel> modelList = new ArrayList<>(pageObject.getRecords());

            List<OperationLogVo> operationLogVoList = modelList.stream().map(o -> {
                OperationLogVo vo = ModelMapperUtil.ModelMapperInstance.map(o, OperationLogVo.class);
                vo.setOperationType(o.getOperationType().getDescription());
                return vo;
            }).collect(Collectors.toList());

            PageResponse<OperationLogVo> response = new PageResponse<>(page, size, (int) pageObject.getTotal());
            response.setData(operationLogVoList);
            return response;
        }

        return new PageResponse<OperationLogVo>() {{
            setPageSize(size);
        }};
    }

    @ApiModel(value = "操作日志vo")
    @Data
    public static class OperationLogVo {
        @ApiModelProperty(value = "操作日志id")
        private Long id;
        @ApiModelProperty(value = "操作人帐号")
        private String operatorAccount;
        @ApiModelProperty(value = "操作类型")
        private String operationType;
        @ApiModelProperty(value = "操作内容")
        private String content;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @ApiModelProperty(value = "操作时间")
        private Date createTime;
    }

}
