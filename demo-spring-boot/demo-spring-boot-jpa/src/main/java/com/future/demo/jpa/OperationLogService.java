package com.future.demo.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.future.common.exception.BusinessException;
import com.future.common.http.PageResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    OperationLogRepository operationLogRepository;

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
        this.operationLogRepository.save(model);
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

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OperationLogModel> pageObject = null;
        if (operationTypeList != null && operationTypeList.size() > 0) {
            pageObject = this.operationLogRepository.find(contextUserId, operationTypeList, pageable);
        } else {
            pageObject = this.operationLogRepository.find(contextUserId, pageable);
        }

        if (pageObject.getNumberOfElements() > 0) {

            List<OperationLogModel> modelList = new ArrayList<>(pageObject.getContent());

            List<OperationLogVo> operationLogVoList = modelList.stream().map(o -> {
                OperationLogVo vo = new OperationLogVo();
                BeanUtils.copyProperties(o, vo);
                vo.setOperationType(o.getOperationType().getDescription());
                return vo;
            }).collect(Collectors.toList());

            PageResponse<OperationLogVo> response = new PageResponse<>(page, size, (int) pageObject.getTotalElements());
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
